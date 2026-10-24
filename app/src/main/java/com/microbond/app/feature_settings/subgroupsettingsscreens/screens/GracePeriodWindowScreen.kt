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
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

@Composable
fun GracePeriodWindowScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var duration by remember { mutableStateOf("24 Hours") }
    var showDurationPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Grace period window",
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
            WaSettingsSectionHeader("Buffer window")
            
            WaSettingsValueRow(
                icon = Icons.Default.HourglassTop,
                title = "Duration",
                value = duration,
                onClick = { showDurationPicker = true }
            )
            
            Text(
                text = "How long members have after the deadline before late fees are applied.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showDurationPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select duration",
            options = listOf("12 Hours", "24 Hours", "48 Hours", "3 Days", "7 Days"),
            currentSelection = duration,
            onSelect = { 
                duration = it 
                showDurationPicker = false 
            },
            onDismiss = { showDurationPicker = false }
        )
    }
}
