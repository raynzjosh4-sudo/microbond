package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.MultisigRosterData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.MultisigRosterMemberDto
import com.microbond.app.core.ui.theme.AvatarColorHelper
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondSearchBarDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch

@Composable
fun ManageAuthorizedSignersScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var rosterData by remember { mutableStateOf<MultisigRosterData?>(null) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var mutatingUserId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var signersExpanded by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun loadRoster() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        errorMsg = null
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getMultisigRoster(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    rosterData = resp.body()!!.data!!
                } else {
                    errorMsg = "Unable to load signers roster."
                }
            } catch (e: Exception) {
                errorMsg = "Network error loading signers."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadRoster()
    }

    fun onAddSigner(member: MultisigRosterMemberDto) {
        if (groupId.isBlank() || !isAdmin || mutatingUserId != null) return

        val prevData = rosterData
        val currentSigners = rosterData?.signers.orEmpty()
        val currentEligible = rosterData?.eligibleMembers.orEmpty()

        // Optimistic update
        val updatedMember = member.copy(isSigner = true)
        val newSigners = currentSigners + updatedMember
        val newEligible = currentEligible.filterNot { it.userId == member.userId }
        val newTotal = newSigners.size
        val currentReq = rosterData?.requiredApprovals ?: 1

        rosterData = rosterData?.copy(
            signers = newSigners,
            eligibleMembers = newEligible,
            totalSigners = newTotal,
            display = "$currentReq of $newTotal Signers"
        )
        mutatingUserId = member.userId

        scope.launch {
            try {
                val resp = NetworkClient.apiService.addSigner(groupId, member.userId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    rosterData = resp.body()!!.data!!
                    snackbarHostState.showSnackbar("Granted signing key to ${member.fullName}")
                } else {
                    rosterData = prevData
                    snackbarHostState.showSnackbar("Failed to grant key: ${resp.message()}")
                }
            } catch (e: Exception) {
                rosterData = prevData
                snackbarHostState.showSnackbar("Network error granting key")
            } finally {
                mutatingUserId = null
            }
        }
    }

    fun onRemoveSigner(member: MultisigRosterMemberDto) {
        if (groupId.isBlank() || !isAdmin || mutatingUserId != null) return

        val prevData = rosterData
        val currentSigners = rosterData?.signers.orEmpty()
        val currentEligible = rosterData?.eligibleMembers.orEmpty()

        if (currentSigners.size <= 1) {
            scope.launch {
                snackbarHostState.showSnackbar("Cannot remove sole signer. At least 1 signer is required.")
            }
            return
        }

        // Optimistic update
        val updatedMember = member.copy(isSigner = false)
        val newSigners = currentSigners.filterNot { it.userId == member.userId }
        val newEligible = currentEligible + updatedMember
        val newTotal = newSigners.size
        val prevReq = rosterData?.requiredApprovals ?: 1
        val newReq = minOf(prevReq, newTotal)

        rosterData = rosterData?.copy(
            signers = newSigners,
            eligibleMembers = newEligible,
            totalSigners = newTotal,
            requiredApprovals = newReq,
            display = "$newReq of $newTotal Signers"
        )
        mutatingUserId = member.userId

        scope.launch {
            try {
                val resp = NetworkClient.apiService.removeSigner(groupId, member.userId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    rosterData = resp.body()!!.data!!
                    snackbarHostState.showSnackbar("Revoked signing key from ${member.fullName}")
                } else {
                    rosterData = prevData
                    snackbarHostState.showSnackbar("Failed to revoke key: ${resp.message()}")
                }
            } catch (e: Exception) {
                rosterData = prevData
                snackbarHostState.showSnackbar("Network error revoking key")
            } finally {
                mutatingUserId = null
            }
        }
    }

    val query = searchQuery.trim().lowercase()
    val filteredSigners = remember(rosterData?.signers, query) {
        rosterData?.signers.orEmpty().filter {
            query.isEmpty() || it.fullName.lowercase().contains(query) || it.phoneNumber.contains(query)
        }
    }
    val filteredEligible = remember(rosterData?.eligibleMembers, query) {
        rosterData?.eligibleMembers.orEmpty().filter {
            query.isEmpty() || it.fullName.lowercase().contains(query) || it.phoneNumber.contains(query)
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Brand Top Bar: Back | Microbond Logo + Text | 3-Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MicrobondTextPrimary
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.microtopicon),
                        contentDescription = "Microbond Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Microbond",
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { loadRoster() }) {
                    if (isLoading || mutatingUserId != null) {
                        CircularProgressIndicator(
                            color = MicrobondActiveGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MicrobondTextPrimary
                        )
                    }
                }
            }
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            // ── Large Page Header ──────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Manage signers",
                        color = MicrobondTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = groupName.ifBlank { "Savings Group" }.uppercase(),
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // ── Treasury Protection Keys Hero Tile ─────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MicrobondActiveGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = MicrobondActiveGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Treasury Protection Keys",
                            color = MicrobondTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val signersCount = rosterData?.signers?.size ?: 0
                        val totalMembers = rosterData?.totalSigners ?: signersCount
                        val countText = if (signersCount > 0) {
                            "$signersCount of $totalMembers signers"
                        } else {
                            val allCount = rosterData?.allMembersCount ?: totalMembers
                            "0 of $allCount signers"
                        }
                        Text(
                            text = countText,
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Require multiple signers to protect treasury actions.",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // ── Search Bar ─────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MicrobondSearchBarDark)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MicrobondInactiveGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search members",
                                        color = MicrobondInactiveGray,
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MicrobondInactiveGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Error notice
            if (errorMsg != null) {
                item {
                    Text(
                        text = "⚠ $errorMsg",
                        color = MicrobondDestructiveRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // ── Section 1: Authorized signers (N) ──────────────────────────────
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { signersExpanded = !signersExpanded }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Authorized signers (${filteredSigners.size})",
                        color = MicrobondTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (signersExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (signersExpanded) "Collapse" else "Expand",
                        tint = MicrobondInactiveGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (signersExpanded) {
                if (filteredSigners.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = if (query.isEmpty()) "No signers designated yet" else "No signers matching \"$query\"",
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(filteredSigners, key = { "signer_${it.userId}" }) { member ->
                        SignerMemberRow(
                            member = member,
                            isSigner = true,
                            isSoleSigner = (rosterData?.signers?.size ?: 0) <= 1,
                            isMutating = mutatingUserId == member.userId,
                            isAdmin = isAdmin,
                            onAction = { onRemoveSigner(member) }
                        )
                        HorizontalDivider(
                            color = MicrobondDividerDark,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 82.dp, end = 20.dp)
                        )
                    }
                }
            }

            // ── Section 2: Eligible group members ──────────────────────────────
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Eligible group members",
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            if (filteredEligible.isEmpty()) {
                item {
                    Text(
                        text = if (query.isEmpty()) "All members are currently authorized signers." else "No members matching \"$query\"",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(filteredEligible, key = { "eligible_${it.userId}" }) { member ->
                    SignerMemberRow(
                        member = member,
                        isSigner = false,
                        isSoleSigner = false,
                        isMutating = mutatingUserId == member.userId,
                        isAdmin = isAdmin,
                        onAction = { onAddSigner(member) }
                    )
                    HorizontalDivider(
                        color = MicrobondDividerDark,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(start = 82.dp, end = 20.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SignerMemberRow(
    member: MultisigRosterMemberDto,
    isSigner: Boolean,
    isSoleSigner: Boolean,
    isMutating: Boolean,
    isAdmin: Boolean,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // WhatsApp-style avatar using MicroBondAvatar + AvatarColorHelper
        MicroBondAvatar(
            avatarUrl = member.profilePictureUrl,
            name = member.fullName,
            backgroundColor = AvatarColorHelper.getColor(member.userId),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Name + Admin Badge + Phone Number
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = member.fullName.ifBlank { "Group Member" },
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (member.role.equals("ADMIN", ignoreCase = true)) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MicrobondActivePillDark)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Admin",
                            color = MicrobondActiveGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (member.phoneNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = member.phoneNumber,
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Action button
        if (isMutating) {
            CircularProgressIndicator(
                color = MicrobondActiveGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else if (isAdmin) {
            if (isSigner) {
                if (isSoleSigner) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MicrobondOverlayWidgetDark)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Sole Signer",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onAction,
                        border = BorderStroke(1.dp, MicrobondDestructiveRed),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MicrobondDestructiveRed
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Revoke",
                            color = MicrobondDestructiveRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onAction,
                    border = BorderStroke(1.dp, MicrobondActiveGreen),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MicrobondActiveGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Grant key",
                        color = MicrobondActiveGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
