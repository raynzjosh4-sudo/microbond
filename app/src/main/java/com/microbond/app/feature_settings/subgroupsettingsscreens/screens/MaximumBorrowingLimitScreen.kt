package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.TrendingUp
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
fun MaximumBorrowingLimitScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var borrowingMultiplier by remember { mutableStateOf("3x Current Savings") }
    var enforceHardCap by remember { mutableStateOf(false) }
    var absoluteLimit by remember { mutableStateOf("5,000,000 UGX") }

    var showMultiplierPicker by remember { mutableStateOf(false) }
    var showLimitPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Borrowing limit",
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
            WaSettingsSectionHeader("Loan restrictions")

            WaSettingsValueRow(
                icon = Icons.Default.TrendingUp,
                title = "Borrowing multiplier",
                value = borrowingMultiplier,
                onClick = { showMultiplierPicker = true }
            )

            WaSettingsDivider()

            WaSettingsSwitchRow(
                icon = Icons.Default.Block,
                title = "Enforce absolute hard cap",
                subtitle = "Set a strict monetary ceiling on loans, regardless of a member's total savings.",
                checked = enforceHardCap,
                onCheckedChange = { enforceHardCap = it }
            )
            
            if (enforceHardCap) {
                WaSettingsDivider()
                WaSettingsValueRow(
                    icon = Icons.Default.Block, // Reuse icon for spacing
                    title = "Absolute limit amount",
                    value = absoluteLimit,
                    onClick = { showLimitPicker = true }
                )
            }
        }
    }

    if (showMultiplierPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select borrowing multiplier",
            options = listOf("1x Current Savings", "2x Current Savings", "3x Current Savings", "4x Current Savings", "5x Current Savings"),
            currentSelection = borrowingMultiplier,
            onSelect = { 
                borrowingMultiplier = it 
                showMultiplierPicker = false 
            },
            onDismiss = { showMultiplierPicker = false }
        )
    }

    if (showLimitPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select hard cap limit",
            options = listOf("1,000,000 UGX", "2,000,000 UGX", "5,000,000 UGX", "10,000,000 UGX", "20,000,000 UGX"),
            currentSelection = absoluteLimit,
            onSelect = { 
                absoluteLimit = it 
                showLimitPicker = false 
            },
            onDismiss = { showLimitPicker = false }
        )
    }
}
