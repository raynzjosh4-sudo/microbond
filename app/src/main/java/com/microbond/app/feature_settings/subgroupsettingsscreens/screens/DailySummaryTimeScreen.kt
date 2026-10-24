package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryTimeScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var summaryTime by remember { mutableStateOf("18:00") }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(initialHour = 18, initialMinute = 0)

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Daily summary time",
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
            WaSettingsSectionHeader("Summary notification")

            WaSettingsValueRow(
                icon = Icons.Default.Schedule,
                title = "Delivery time",
                value = summaryTime,
                onClick = { showTimePicker = true }
            )
            
            Text(
                text = "Set the exact time when a daily digest of group transactions and pending approvals is sent out.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp) // Aligned with text
            )
        }
        
        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = { 
                        val formattedHour = timePickerState.hour.toString().padStart(2, '0')
                        val formattedMinute = timePickerState.minute.toString().padStart(2, '0')
                        summaryTime = "$formattedHour:$formattedMinute"
                        showTimePicker = false 
                    }) {
                        Text("OK", color = com.microbond.app.core.ui.theme.MicrobondActiveGreen)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel", color = androidx.compose.ui.graphics.Color.Gray)
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}
