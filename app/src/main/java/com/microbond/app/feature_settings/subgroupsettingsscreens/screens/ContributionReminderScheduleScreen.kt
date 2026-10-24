package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Scaffold

@Composable
fun ContributionReminderScheduleScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var showSchedulePicker by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("3 days before deadline") }
    val options = listOf(
        "1 day before deadline",
        "2 days before deadline",
        "3 days before deadline",
        "1 week before deadline",
        "None (Off)"
    )

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Reminder schedule",
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
            WaSettingsSectionHeader("Notify members automatically")

            WaSettingsValueRow(
                icon = Icons.Default.NotificationsActive,
                title = "Send reminder",
                value = selectedOption,
                onClick = { showSchedulePicker = true }
            )
            
            WaSettingsDivider()
        }
    }

    if (showSchedulePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select reminder schedule",
            options = options,
            currentSelection = selectedOption,
            onSelect = { 
                selectedOption = it 
                showSchedulePicker = false 
            },
            onDismiss = { showSchedulePicker = false }
        )
    }
}
