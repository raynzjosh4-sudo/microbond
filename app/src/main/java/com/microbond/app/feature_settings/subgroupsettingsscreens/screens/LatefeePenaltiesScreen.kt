package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

@Composable
fun LatefeePenaltiesScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var feeAmount by remember { mutableStateOf("10,000 UGX") }
    var showFeePicker by remember { mutableStateOf(false) }

    var selectedFrequency by remember { mutableStateOf("Per day") }
    var showFrequencyPicker by remember { mutableStateOf(false) }
    val frequencies = listOf("Per day", "Per week", "Per month", "One time")

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Late-fee penalties",
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
            WaSettingsSectionHeader("Penalty rules")

            WaSettingsValueRow(
                icon = Icons.Default.Payments,
                title = "Penalty Amount",
                value = feeAmount,
                onClick = { showFeePicker = true }
            )

            WaSettingsDivider()

            WaSettingsValueRow(
                icon = Icons.Default.Update,
                title = "Frequency",
                value = selectedFrequency,
                onClick = { showFrequencyPicker = true }
            )

            Text(
                text = "Set the penalty amount charged when a member misses their contribution deadline.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showFeePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select penalty amount",
            options = listOf("1,000 UGX", "5,000 UGX", "10,000 UGX", "20,000 UGX", "50,000 UGX"),
            currentSelection = feeAmount,
            onSelect = { 
                feeAmount = it 
                showFeePicker = false 
            },
            onDismiss = { showFeePicker = false }
        )
    }

    if (showFrequencyPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select frequency",
            options = frequencies,
            currentSelection = selectedFrequency,
            onSelect = { 
                selectedFrequency = it 
                showFrequencyPicker = false 
            },
            onDismiss = { showFrequencyPicker = false }
        )
    }
}
