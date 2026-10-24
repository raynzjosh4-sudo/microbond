package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet

@Composable
fun WeeklyReportDayScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var reportDay by remember { mutableStateOf("Friday") }
    var showDayPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Weekly report day",
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
            WaSettingsSectionHeader("Automated summaries")

            WaSettingsValueRow(
                icon = Icons.Default.DateRange,
                title = "Delivery day",
                value = reportDay,
                onClick = { showDayPicker = true }
            )

            WaSettingsDivider()

            WaSettingsValueRow(
                icon = Icons.Default.EditNote,
                title = "Time of day",
                value = "17:00",
                onClick = { /* Open time picker */ }
            )
            
            Text(
                text = "A full summary of the week's deposits, approved loans, and upcoming deadlines will be sent to the group chat.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showDayPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select delivery day",
            options = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            currentSelection = reportDay,
            onSelect = { 
                reportDay = it 
                showDayPicker = false 
            },
            onDismiss = { showDayPicker = false }
        )
    }
}
