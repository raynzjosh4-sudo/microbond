package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Security
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
fun SpendingApprovalThresholdScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var requireApproval by remember { mutableStateOf(true) }
    var thresholdAmount by remember { mutableStateOf("500,000 UGX") }
    var showAmountPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Approval threshold",
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
            WaSettingsSectionHeader("Large expenses")

            WaSettingsSwitchRow(
                icon = Icons.Default.Security,
                title = "Require committee approval",
                subtitle = "Force all transactions above a certain size to go through the formal voting process.",
                checked = requireApproval,
                onCheckedChange = { requireApproval = it }
            )

            if (requireApproval) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Approval threshold amount",
                    value = thresholdAmount,
                    onClick = { showAmountPicker = true }
                )
            }
        }
    }

    if (showAmountPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select approval threshold",
            options = listOf("50,000 UGX", "100,000 UGX", "250,000 UGX", "500,000 UGX", "1,000,000 UGX"),
            currentSelection = thresholdAmount,
            onSelect = { 
                thresholdAmount = it 
                showAmountPicker = false 
            },
            onDismiss = { showAmountPicker = false }
        )
    }
}
