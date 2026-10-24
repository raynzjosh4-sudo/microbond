package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondWarningAmber
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsActionConfirmationDialog
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import kotlinx.coroutines.launch

@Composable
fun ExitGroupScreen(
    groupName: String,
    groupId: String = "",
    onBack: () -> Unit,
    onExited: () -> Unit = onBack
) {
    BackHandler(onBack = onBack)

    val scope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }
    var isLeaving by remember { mutableStateOf(false) }
    var blockReason by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun doLeave() {
        if (groupId.isBlank()) return
        scope.launch {
            isLeaving = true
            errorMsg = null
            blockReason = null
            try {
                val resp = NetworkClient.apiService.leaveGroup(groupId)
                val body = resp.body()
                if (resp.isSuccessful && body?.canLeave != false) {
                    onExited()
                } else {
                    blockReason = body?.blockReason ?: "You cannot leave this group right now."
                }
            } catch (e: Exception) {
                errorMsg = "Network error: ${e.localizedMessage}"
            }
            isLeaving = false
            showConfirm = false
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Exit group",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLeaving) {
                        Box(modifier = Modifier.padding(end = 12.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = MicrobondDestructiveRed,
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
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MicrobondWarningAmber,
                    modifier = Modifier.padding(end = 16.dp).size(28.dp)
                )
                Column {
                    Text(
                        text = "Are you sure you want to exit this group?",
                        color = MicrobondTextPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Exiting the group will remove your ability to send or receive messages. Any unsettled financial obligations or outstanding loans will remain visible to the group admins.",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            WaSettingsDivider()

            blockReason?.let { reason ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MicrobondWarningAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(reason, color = MicrobondWarningAmber, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            errorMsg?.let { msg ->
                Text("⚠ $msg", color = MicrobondDestructiveRed, style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            }

            WaSettingsRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = if (isLeaving) "Leaving…" else "Exit $groupName",
                titleColor = MicrobondDestructiveRed,
                iconTint = MicrobondDestructiveRed,
                onClick = { if (!isLeaving) showConfirm = true }
            )

            WaSettingsDivider()
        }
    }

    if (showConfirm) {
        WaSettingsActionConfirmationDialog(
            title = "Exit $groupName?",
            text = "You will lose access to this group's chat and financial activities. This action cannot be undone without re-joining.",
            confirmText = "Exit group",
            dismissText = "Stay",
            isDestructive = true,
            onConfirm = { doLeave() },
            onDismiss = { showConfirm = false }
        )
    }
}
