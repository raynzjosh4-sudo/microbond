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
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

@Composable
fun CoolingoffPeriodForNewMembersScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var showPeriodPicker by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("1 month") }
    val options = listOf(
        "None",
        "1 week",
        "2 weeks",
        "1 month",
        "3 months"
    )

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Cooling-off period",
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
            WaSettingsSectionHeader("Wait time for new members")

            WaSettingsValueRow(
                icon = Icons.Default.HourglassBottom,
                title = "Wait duration",
                value = selectedOption,
                onClick = { showPeriodPicker = true }
            )

            Text(
                text = "Time required before a new member can request a loan or payout from the group pool.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showPeriodPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select cooling-off duration",
            options = options,
            currentSelection = selectedOption,
            onSelect = { 
                selectedOption = it 
                showPeriodPicker = false 
            },
            onDismiss = { showPeriodPicker = false }
        )
    }
}
