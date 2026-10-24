package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsActionConfirmationDialog

@Composable
fun MyDataRetentionScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var retentionPolicy by remember { mutableStateOf("Keep until I delete my account") }
    var showPolicyPicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Data retention",
                subtitle = groupName,
                onBack = onBack
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            WaSettingsSectionHeader("Privacy and Storage")

            WaSettingsValueRow(
                icon = Icons.Default.Storage,
                title = "Storage policy",
                value = retentionPolicy,
                onClick = { showPolicyPicker = true }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.DeleteForever,
                title = "Delete all my group data",
                titleColor = Color(0xFFFF4F4F),
                iconTint = Color(0xFFFF4F4F),
                subtitle = "Permanently erase your chat history and media from this group.",
                onClick = { showDeleteConfirm = true }
            )
        }
    }

    if (showPolicyPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select storage policy",
            options = listOf("Keep until I delete my account", "Delete after 1 year", "Delete after 2 years", "Delete after 5 years"),
            currentSelection = retentionPolicy,
            onSelect = { 
                retentionPolicy = it 
                showPolicyPicker = false 
            },
            onDismiss = { showPolicyPicker = false }
        )
    }

    if (showDeleteConfirm) {
        WaSettingsActionConfirmationDialog(
            title = "Delete all group data?",
            text = "This will permanently erase your entire chat history, media, and records for this group from our servers. This action cannot be undone.",
            confirmText = "Delete Data",
            isDestructive = true,
            onConfirm = { showDeleteConfirm = false },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}
