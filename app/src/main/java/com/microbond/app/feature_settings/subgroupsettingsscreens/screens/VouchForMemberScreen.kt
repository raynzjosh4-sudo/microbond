package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.SubmitVouchRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.VouchRecordDto
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import kotlinx.coroutines.launch

@Composable
fun VouchForMemberScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true,
    isBorrower: Boolean = false
) {
    BackHandler(onBack = onBack)

    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    var vouchesGiven by remember { mutableStateOf<List<VouchRecordDto>>(emptyList()) }
    var vouchesReceived by remember { mutableStateOf<List<VouchRecordDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccessMsg by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Endorsement form states
    var targetMemberInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    val pageTitle = if (isBorrower) "Vouch for a borrower" else "Vouch for a member"

    fun loadVouches() {
        if (groupId.isBlank()) return
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val resp = NetworkClient.apiService.getMyVouches(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    vouchesGiven = data.vouchesGiven
                    vouchesReceived = data.vouchesReceived
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load endorsement records."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadVouches()
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = pageTitle,
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isSubmitting) {
                        Box(modifier = Modifier.padding(end = 12.dp)) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            // Explanatory Social Underwriting Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MicrobondActiveGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Reputational Social Underwriting",
                            color = MicrobondTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Your endorsement guarantees this member's integrity. Collusion rings are algorithmically detected and blocked by the Microbond Rust Engine.",
                            color = MicrobondInactiveGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MicrobondDarkBackground,
                contentColor = MicrobondActiveGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MicrobondActiveGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Endorse", fontSize = 13.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Given (${vouchesGiven.size})", fontSize = 13.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Received (${vouchesReceived.size})", fontSize = 13.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Endorsement Form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Submit Member Endorsement",
                            color = MicrobondTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Enter the username or member ID of the person you want to back.",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = targetMemberInput,
                            onValueChange = { targetMemberInput = it },
                            placeholder = { Text("Member phone or ID (e.g. +256... or usr_123)", color = MicrobondInactiveGray) },
                            leadingIcon = {
                                Icon(Icons.Default.Group, contentDescription = null, tint = MicrobondInactiveGray)
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MicrobondActiveGreen,
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                cursorColor = MicrobondActiveGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = noteInput,
                            onValueChange = { noteInput = it },
                            placeholder = { Text("Reason for endorsement (optional)", color = MicrobondInactiveGray) },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MicrobondActiveGreen,
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedTextColor = MicrobondTextPrimary,
                                unfocusedTextColor = MicrobondTextPrimary,
                                cursorColor = MicrobondActiveGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        submitSuccessMsg?.let { msg ->
                            Text(
                                text = "✓ $msg",
                                color = MicrobondActiveGreen,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        errorMessage?.let { err ->
                            Text(
                                text = "⚠ $err",
                                color = MicrobondDestructiveRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (targetMemberInput.isNotBlank() && groupId.isNotBlank()) {
                                    scope.launch {
                                        isSubmitting = true
                                        errorMessage = null
                                        submitSuccessMsg = null
                                        try {
                                            val resp = NetworkClient.apiService.submitVouch(
                                                groupId,
                                                SubmitVouchRequest(targetMemberId = targetMemberInput.trim(), note = noteInput.ifBlank { null })
                                            )
                                            if (resp.isSuccessful) {
                                                submitSuccessMsg = "Endorsement recorded successfully!"
                                                targetMemberInput = ""
                                                noteInput = ""
                                                loadVouches()
                                            } else {
                                                errorMessage = resp.errorBody()?.string() ?: "Failed to submit endorsement."
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = e.localizedMessage ?: "Network error submitting vouch."
                                        } finally {
                                            isSubmitting = false
                                        }
                                    }
                                }
                            },
                            enabled = targetMemberInput.isNotBlank() && !isSubmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MicrobondActiveGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm & Vouch For Member", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                1 -> {
                    // Vouches Given
                    if (vouchesGiven.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "You haven't endorsed any members in this group yet.",
                                color = MicrobondInactiveGray,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(vouchesGiven) { record ->
                                VouchRecordRow(record, isGiven = true)
                                WaSettingsDivider()
                            }
                        }
                    }
                }
                2 -> {
                    // Vouches Received
                    if (vouchesReceived.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No members have endorsed you yet.\nRequest a trusted contact to vouch for your membership.",
                                color = MicrobondInactiveGray,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(vouchesReceived) { record ->
                                VouchRecordRow(record, isGiven = false)
                                WaSettingsDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VouchRecordRow(record: VouchRecordDto, isGiven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MicrobondActiveGreen,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isGiven) {
                    record.targetMemberName.ifBlank { record.targetMemberId }
                } else {
                    record.voucherMemberName.ifBlank { record.voucherMemberId }
                },
                color = MicrobondTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (!record.note.isNullOrBlank()) {
                Text(
                    text = "\"${record.note}\"",
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Text(
            text = record.status,
            color = MicrobondActiveGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
