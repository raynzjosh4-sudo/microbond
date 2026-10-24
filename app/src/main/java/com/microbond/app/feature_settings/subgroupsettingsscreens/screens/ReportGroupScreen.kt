package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.ReportGroupRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsActionConfirmationDialog
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import kotlinx.coroutines.launch

private val reportReasons = listOf(
    "Fraud or scam",
    "Harassment or abuse",
    "Misleading information",
    "Violating terms of service",
    "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGroupScreen(
    groupName: String,
    groupId: String = "",
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val scope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf("Fraud or scam") }
    var description by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccess by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showReasonPicker by remember { mutableStateOf(false) }

    fun submitReport() {
        if (groupId.isBlank()) return
        scope.launch {
            isSubmitting = true
            errorMsg = null
            try {
                val resp = NetworkClient.apiService.reportGroup(
                    groupId,
                    ReportGroupRequest(
                        reason = selectedReason,
                        description = description.ifBlank { null }
                    )
                )
                if (resp.isSuccessful) {
                    submitSuccess = true
                } else {
                    errorMsg = "Submission failed (HTTP ${resp.code()}). Please try again."
                }
            } catch (e: Exception) {
                errorMsg = "Network error: ${e.localizedMessage}"
            }
            isSubmitting = false
            showConfirm = false
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Report group",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isSubmitting) {
                        Box(modifier = Modifier.padding(end = 12.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color(0xFFFF4F4F),
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Success state
            if (submitSuccess) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MicrobondActiveGreen, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Report submitted", color = MicrobondTextPrimary, fontSize = 16.sp)
                        Text(
                            "Thank you. Our team will review your report within 24–48 hours.",
                            color = MicrobondInactiveGray,
                            fontSize = 13.sp
                        )
                    }
                }
                return@Column
            }

            Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color(0xFF3498DB),
                    modifier = Modifier.padding(end = 16.dp).size(28.dp)
                )
                Column {
                    Text("Why report this group?", color = MicrobondTextPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "If you believe this group is engaging in fraudulent activities, scams, or violating our terms of service, please report it. A copy of recent messages and transactions will be securely forwarded to Microbond support for review.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Flag,
                title = "Reason",
                subtitle = selectedReason,
                onClick = { showReasonPicker = true }
            )

            WaSettingsDivider()

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Additional details (optional)", color = MicrobondInactiveGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    focusedBorderColor = Color(0xFFFF4F4F),
                    unfocusedBorderColor = MicrobondInactiveGray,
                    cursorColor = Color(0xFFFF4F4F)
                )
            )

            errorMsg?.let { msg ->
                Text("⚠ $msg", color = Color(0xFFFF4F4F), style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }

            WaSettingsRow(
                icon = Icons.Default.Flag,
                title = "Report $groupName",
                titleColor = Color(0xFFFF4F4F),
                iconTint = Color(0xFFFF4F4F),
                onClick = { showConfirm = true }
            )

            WaSettingsDivider()
        }
    }

    if (showReasonPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select a reason",
            options = reportReasons,
            currentSelection = selectedReason,
            onSelect = { selectedReason = it; showReasonPicker = false },
            onDismiss = { showReasonPicker = false }
        )
    }

    if (showConfirm) {
        WaSettingsActionConfirmationDialog(
            title = "Report $groupName?",
            text = "Are you sure you want to report this group for \"$selectedReason\"? The last 50 messages will be forwarded to Microbond for analysis.",
            confirmText = "Report",
            dismissText = "Cancel",
            isDestructive = true,
            onConfirm = { submitReport() },
            onDismiss = { showConfirm = false }
        )
    }
}
