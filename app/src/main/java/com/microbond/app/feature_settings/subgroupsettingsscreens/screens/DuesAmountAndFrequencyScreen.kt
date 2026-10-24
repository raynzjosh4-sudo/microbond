package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

@Composable
fun DuesAmountAndFrequencyScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var amount by remember { mutableStateOf("50,000 UGX") }
    var showAmountPicker by remember { mutableStateOf(false) }

    var selectedFrequency by remember { mutableStateOf("Monthly") }
    var showFrequencyPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Dues amount and frequency",
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
            WaSettingsSectionHeader("Contribution settings")
            
            WaSettingsValueRow(
                icon = Icons.Default.Payments,
                title = "Contribution Amount",
                value = amount,
                onClick = { showAmountPicker = true }
            )
            
            WaSettingsDivider()
            
            WaSettingsValueRow(
                icon = Icons.Default.EventRepeat,
                title = "Frequency",
                value = selectedFrequency,
                onClick = { showFrequencyPicker = true }
            )
            
            Text(
                text = "Set the standard contribution amount and how often members are expected to pay.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showAmountPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select contribution amount",
            options = listOf("10,000 UGX", "20,000 UGX", "50,000 UGX", "100,000 UGX", "500,000 UGX"),
            currentSelection = amount,
            onSelect = { 
                amount = it 
                showAmountPicker = false 
            },
            onDismiss = { showAmountPicker = false }
        )
    }

    if (showFrequencyPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select frequency",
            options = listOf("Weekly", "Bi-weekly", "Monthly", "Quarterly", "Annually"),
            currentSelection = selectedFrequency,
            onSelect = { 
                selectedFrequency = it 
                showFrequencyPicker = false 
            },
            onDismiss = { showFrequencyPicker = false }
        )
    }
}
