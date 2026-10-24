package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
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
fun MaximumPayoutLimitScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var limitActive by remember { mutableStateOf(true) }
    var maxPayout by remember { mutableStateOf("10,000,000 UGX") }
    var showLimitPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Maximum payout limit",
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
            WaSettingsSectionHeader("Fund extraction limits")

            WaSettingsSwitchRow(
                icon = Icons.Default.Security,
                title = "Cap individual payouts",
                subtitle = "Prevent any single member from withdrawing an unsafe amount of the group's pooled treasury at one time.",
                checked = limitActive,
                onCheckedChange = { limitActive = it }
            )

            if (limitActive) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.AccountBalance,
                    title = "Maximum allowed payout",
                    value = maxPayout,
                    onClick = { showLimitPicker = true }
                )
            }
        }
    }

    if (showLimitPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select payout limit",
            options = listOf("1,000,000 UGX", "5,000,000 UGX", "10,000,000 UGX", "20,000,000 UGX", "Unlimited"),
            currentSelection = maxPayout,
            onSelect = { 
                maxPayout = it 
                showLimitPicker = false 
            },
            onDismiss = { showLimitPicker = false }
        )
    }
}
