package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsActionConfirmationDialog

@Composable
fun NewShareIssuanceScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var requireVote by remember { mutableStateOf(true) }
    var shareVolume by remember { mutableStateOf("1,000 Shares") }
    var showVolumePicker by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "New share issuance",
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
            WaSettingsSectionHeader("Expand group treasury")

            WaSettingsValueRow(
                icon = Icons.Default.AddBusiness,
                title = "Proposed issuance volume",
                value = shareVolume,
                onClick = { showVolumePicker = true }
            )

            WaSettingsDivider()

            WaSettingsSwitchRow(
                icon = Icons.Default.Gavel,
                title = "Require member vote",
                subtitle = "Hold a mandatory vote before new shares are minted, preventing unauthorized dilution.",
                checked = requireVote,
                onCheckedChange = { requireVote = it }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.AddBusiness, // Spacer icon essentially
                title = "Initiate issuance process",
                subtitle = "Start the formal proposal to mint and distribute new shares.",
                onClick = { showConfirm = true }
            )
        }
    }

    if (showVolumePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select issuance volume",
            options = listOf("500 Shares", "1,000 Shares", "5,000 Shares", "10,000 Shares", "50,000 Shares"),
            currentSelection = shareVolume,
            onSelect = { 
                shareVolume = it 
                showVolumePicker = false 
            },
            onDismiss = { showVolumePicker = false }
        )
    }

    if (showConfirm) {
        WaSettingsActionConfirmationDialog(
            title = "Start Issuance Proposal",
            text = "Are you sure you want to propose minting $shareVolume? A formal vote will be created in the group chat.",
            confirmText = "Propose",
            dismissText = "Cancel",
            onConfirm = { showConfirm = false },
            onDismiss = { showConfirm = false }
        )
    }
}
