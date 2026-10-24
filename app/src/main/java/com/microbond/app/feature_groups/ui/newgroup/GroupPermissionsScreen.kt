package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.switchbutton.MicrobondSwitch
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateGroupPermissionsRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch

/**
 * Group Permissions screen — loads toggle states from the backend via GET /api/v1/groups/{group_id}/settings/permissions.
 * Any member can view the active permissions. Only ADMINs can edit toggles via PATCH /api/v1/groups/{group_id}/settings/permissions.
 */
@Composable
fun GroupPermissionsScreen(
    groupType: SavingsGroupType,
    groupName: String? = null,
    groupId: String = "",
    isAdmin: Boolean = true,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Local toggle state ────────────────────────────────────────────────────
    var editSettings       by remember { mutableStateOf(true) }
    var sendMessages       by remember { mutableStateOf(true) }
    var inviteViaLink      by remember { mutableStateOf(false) }
    var managePayoutRoster by remember { mutableStateOf(false) }
    var approveMembers     by remember { mutableStateOf(false) }
    var allowTurnSwaps     by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving  by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun loadPermissions() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        loadError = null
        scope.launch {
            try {
                val res = NetworkClient.apiService.getGroupPermissions(groupId)
                if (res.isSuccessful && res.body()?.data != null) {
                    val d = res.body()!!.data!!
                    editSettings       = d.membersCanEditSettings
                    sendMessages       = d.membersCanSendMessages
                    inviteViaLink      = d.membersCanInviteViaLink
                    managePayoutRoster = d.membersCanManagePayoutRoster
                    approveMembers     = d.adminsMustApproveNewMembers
                    allowTurnSwaps     = d.allowTurnSwaps
                } else {
                    loadError = "Failed to load permissions (HTTP ${res.code()})"
                }
            } catch (e: Exception) {
                loadError = "Could not reach server: ${e.localizedMessage}"
                android.util.Log.w("GroupPermissionsScreen", "Load error", e)
            } finally {
                isLoading = false
            }
        }
    }

    // ── Load from backend on entry ────────────────────────────────────────────
    LaunchedEffect(groupId) {
        loadPermissions()
    }

    /** Patches a single field on the backend and reverts on failure. */
    fun patch(request: UpdateGroupPermissionsRequest, onRevert: () -> Unit) {
        if (groupId.isBlank() || !isAdmin) return
        isSaving = true
        scope.launch {
            try {
                val res = NetworkClient.apiService.updateGroupPermissions(groupId, request)
                if (res.isSuccessful && res.body()?.data != null) {
                    val d = res.body()!!.data!!
                    editSettings       = d.membersCanEditSettings
                    sendMessages       = d.membersCanSendMessages
                    inviteViaLink      = d.membersCanInviteViaLink
                    managePayoutRoster = d.membersCanManagePayoutRoster
                    approveMembers     = d.adminsMustApproveNewMembers
                    allowTurnSwaps     = d.allowTurnSwaps
                } else {
                    android.util.Log.w("GroupPermissionsScreen", "PATCH failed ${res.code()}")
                    onRevert()
                    snackbarHostState.showSnackbar("Failed to update permission.")
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupPermissionsScreen", "PATCH error", e)
                onRevert()
                snackbarHostState.showSnackbar("Network error: Could not save changes.")
            } finally {
                isSaving = false
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GroupPermissionsTopBar(groupName = groupName, onBack = onBack)
        }
    ) { insets ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MicrobondActiveGreen
                    )
                }
                loadError != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = loadError ?: "Failed to load permissions",
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = { loadPermissions() }) {
                            Text("Retry", color = MicrobondActiveGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // ── Non-admin notice ──────────────────────────────────
                        if (!isAdmin) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .background(Color(0xFF1E2224), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MicrobondInactiveGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Only group admins can change permissions.",
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // ── Section: Members can ──────────────────────────────
                        PermissionSectionLabel("Members can:")

                        PermissionSwitchRow(
                            icon = Icons.Default.Edit,
                            title = "Edit group settings",
                            description = "Choose who can change this group's name, icon, and description.",
                            checked = editSettings,
                            enabled = isAdmin && !isSaving,
                            onCheckedChange = { newVal ->
                                val old = editSettings
                                editSettings = newVal
                                patch(UpdateGroupPermissionsRequest(membersCanEditSettings = newVal)) {
                                    editSettings = old
                                }
                            }
                        )

                        PermissionSwitchRow(
                            icon = Icons.AutoMirrored.Filled.Message,
                            title = "Send new messages",
                            description = "Choose whether members can send messages and media in chat.",
                            checked = sendMessages,
                            enabled = isAdmin && !isSaving,
                            onCheckedChange = { newVal ->
                                val old = sendMessages
                                sendMessages = newVal
                                patch(UpdateGroupPermissionsRequest(membersCanSendMessages = newVal)) {
                                    sendMessages = old
                                }
                            }
                        )

                        PermissionSwitchRow(
                            icon = Icons.Default.Link,
                            title = "Invite via link or QR code",
                            description = "Choose whether members can share the invite link or QR code with others.",
                            checked = inviteViaLink,
                            enabled = isAdmin && !isSaving,
                            onCheckedChange = { newVal ->
                                val old = inviteViaLink
                                inviteViaLink = newVal
                                patch(UpdateGroupPermissionsRequest(membersCanInviteViaLink = newVal)) {
                                    inviteViaLink = old
                                }
                            }
                        )

                        if (groupType == SavingsGroupType.MERRY_GO_ROUND) {
                            PermissionSwitchRow(
                                icon = Icons.AutoMirrored.Filled.ListAlt,
                                title = "Manage payout roster",
                                description = "Allow members to request turn swaps or modify the payout order.",
                                checked = managePayoutRoster,
                                enabled = isAdmin && !isSaving,
                                onCheckedChange = { newVal ->
                                    val old = managePayoutRoster
                                    managePayoutRoster = newVal
                                    patch(UpdateGroupPermissionsRequest(membersCanManagePayoutRoster = newVal)) {
                                        managePayoutRoster = old
                                    }
                                }
                            )

                            PermissionReadOnlyRow(
                                icon = Icons.Default.SwapHoriz,
                                title = "Payout turn swaps",
                                description = if (allowTurnSwaps) "Members can request payout turn swaps with other members." else "Turn swaps are disabled by group governance.",
                                valueLabel = if (allowTurnSwaps) "Allowed" else "Off"
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // ── Section: Admins can ───────────────────────────────
                        PermissionSectionLabel("Admins can:")

                        PermissionSwitchRow(
                            icon = Icons.Outlined.AdminPanelSettings,
                            title = "Approve new members",
                            description = "When turned on, admins must approve anyone who wants to join the group.",
                            checked = approveMembers,
                            enabled = isAdmin && !isSaving,
                            onCheckedChange = { newVal ->
                                val old = approveMembers
                                approveMembers = newVal
                                patch(UpdateGroupPermissionsRequest(adminsMustApproveNewMembers = newVal)) {
                                    approveMembers = old
                                }
                            }
                        )

                        if (isSaving) {
                            Spacer(Modifier.height(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp
                            )
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────
@Composable
private fun PermissionSectionLabel(text: String) {
    Text(
        text = text,
        color = MicrobondInactiveGray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp)
    )
}

// ── Individual permission switch row ──────────────────────────────────────────
@Composable
private fun PermissionSwitchRow(
    icon: ImageVector,
    title: String,
    description: String?,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MicrobondInactiveGray else MicrobondInactiveGray.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(22.dp)
                    .padding(top = 2.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (enabled) MicrobondTextPrimary else MicrobondTextPrimary.copy(alpha = 0.5f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
                if (description != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            MicrobondSwitch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange
            )
        }

        HorizontalDivider(
            color = MicrobondDividerDark,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

// ── Read-only permission row ──────────────────────────────────────────────────
@Composable
private fun PermissionReadOnlyRow(
    icon: ImageVector,
    title: String,
    description: String?,
    valueLabel: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MicrobondInactiveGray.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(22.dp)
                    .padding(top = 2.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
                if (description != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Text(
                text = valueLabel,
                color = MicrobondActiveGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        HorizontalDivider(
            color = MicrobondDividerDark,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun GroupPermissionsTopBar(groupName: String?, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MicrobondDarkBackground)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MicrobondTextPrimary)
        }
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = "Group permissions",
                color = MicrobondTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (!groupName.isNullOrBlank()) {
                Text(text = groupName, color = MicrobondInactiveGray, fontSize = 14.sp)
            }
        }
    }
}
