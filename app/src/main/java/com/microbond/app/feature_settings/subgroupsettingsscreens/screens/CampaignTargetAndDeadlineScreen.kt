package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

@Composable
fun CampaignTargetAndDeadlineScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var targetAmount by remember { mutableStateOf("1,000,000 UGX") }
    var showAmountPicker by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("Oct 30, 2026") }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Campaign target and deadline",
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
            WaSettingsSectionHeader("Fundraising goals")
            
            WaSettingsValueRow(
                icon = Icons.Default.Flag,
                title = "Target amount",
                value = targetAmount,
                onClick = { showAmountPicker = true }
            )
            
            WaSettingsDivider()
            
            WaSettingsValueRow(
                icon = Icons.Default.CalendarMonth,
                title = "Deadline",
                value = selectedDate,
                onClick = { showDatePicker = true }
            )
            
            Text(
                text = "Set the fundraising target amount and the deadline date for contributions.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showAmountPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select target amount",
            options = listOf("500,000 UGX", "1,000,000 UGX", "5,000,000 UGX", "10,000,000 UGX", "No Target"),
            currentSelection = targetAmount,
            onSelect = { 
                targetAmount = it 
                showAmountPicker = false 
            },
            onDismiss = { showAmountPicker = false }
        )
    }

    if (showDatePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select deadline",
            options = listOf("End of this month", "End of next month", "End of year", "Oct 30, 2026", "No deadline"),
            currentSelection = selectedDate,
            onSelect = { 
                selectedDate = it 
                showDatePicker = false 
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
