package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
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
fun GracePeriodScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableGracePeriod by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf("24 Hours") }
    var showDurationPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Grace period",
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
            WaSettingsSectionHeader("Late Payment Rules")

            WaSettingsSwitchRow(
                icon = Icons.Default.Timer,
                title = "Allow grace periods",
                subtitle = "Give members a small buffer window after the deadline before late fees apply.",
                checked = enableGracePeriod,
                onCheckedChange = { enableGracePeriod = it }
            )

            if (enableGracePeriod) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.Timer, // Or a similar icon, but reusing is fine in Wa settings
                    title = "Buffer window",
                    value = duration,
                    onClick = { showDurationPicker = true }
                )
            }
        }
    }

    if (showDurationPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select buffer window",
            options = listOf("12 Hours", "24 Hours", "48 Hours", "3 Days", "1 Week"),
            currentSelection = duration,
            onSelect = { 
                duration = it 
                showDurationPicker = false 
            },
            onDismiss = { showDurationPicker = false }
        )
    }
}
