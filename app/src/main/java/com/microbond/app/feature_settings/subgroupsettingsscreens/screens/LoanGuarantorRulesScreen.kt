package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet

@Composable
fun LoanGuarantorRulesScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var requireGuarantors by remember { mutableStateOf(true) }
    var guarantorCount by remember { mutableStateOf("2 Members") }
    var showCountPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Guarantor rules",
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
            WaSettingsSectionHeader("Co-signers")

            WaSettingsSwitchRow(
                icon = Icons.Default.Handshake,
                title = "Require guarantors",
                subtitle = "Members must secure backing from other trusted members before a loan is disbursed.",
                checked = requireGuarantors,
                onCheckedChange = { requireGuarantors = it }
            )

            if (requireGuarantors) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.Group,
                    title = "Minimum guarantors",
                    value = guarantorCount,
                    onClick = { showCountPicker = true }
                )
            }
        }
    }

    if (showCountPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select minimum guarantors",
            options = listOf("1 Member", "2 Members", "3 Members", "4 Members", "5 Members"),
            currentSelection = guarantorCount,
            onSelect = { 
                guarantorCount = it 
                showCountPicker = false 
            },
            onDismiss = { showCountPicker = false }
        )
    }
}
