package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Receipt

@Composable
fun ChatHistoryCleanupScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var showTimeframePicker by remember { mutableStateOf(false) }
    var selectedTimeframe by remember { mutableStateOf("90 Days") }
    val timeframes = listOf("30 Days", "90 Days", "6 Months", "1 Year", "Never")
    
    var keepFinancials by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Chat history cleanup",
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
            WaSettingsSectionHeader("Storage management")
            
            WaSettingsValueRow(
                icon = Icons.Default.DeleteSweep,
                title = "Auto-delete older than",
                value = selectedTimeframe,
                onClick = { showTimeframePicker = true }
            )
            
            Text(
                text = "Automatically delete old chat messages and media to save device storage.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )

            WaSettingsDivider()

            WaSettingsSwitchRow(
                icon = Icons.Default.Receipt,
                title = "Preserve financial records",
                subtitle = "Always keep transaction receipts, loan requests, and system alerts regardless of age.",
                checked = keepFinancials,
                onCheckedChange = { keepFinancials = it }
            )
        }
    }

    if (showTimeframePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Delete messages older than",
            options = timeframes,
            currentSelection = selectedTimeframe,
            onSelect = { 
                selectedTimeframe = it 
                showTimeframePicker = false 
            },
            onDismiss = { showTimeframePicker = false }
        )
    }
}
