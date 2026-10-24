package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.WarningAmber
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
fun LateFeeScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableLateFees by remember { mutableStateOf(true) }
    var feeAmount by remember { mutableStateOf("10,000 UGX") }
    var showFeePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Late fee",
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
            WaSettingsSectionHeader("Default penalty")

            WaSettingsSwitchRow(
                icon = Icons.Default.WarningAmber,
                title = "Enforce late fees",
                subtitle = "Automatically penalize members who miss their contribution or repayment deadline.",
                checked = enableLateFees,
                onCheckedChange = { enableLateFees = it }
            )

            if (enableLateFees) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.MoneyOff,
                    title = "Flat fee amount",
                    value = feeAmount,
                    onClick = { showFeePicker = true }
                )
            }
        }
    }

    if (showFeePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select flat fee amount",
            options = listOf("2,000 UGX", "5,000 UGX", "10,000 UGX", "20,000 UGX", "50,000 UGX"),
            currentSelection = feeAmount,
            onSelect = { 
                feeAmount = it 
                showFeePicker = false 
            },
            onDismiss = { showFeePicker = false }
        )
    }
}
