package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Visibility
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

@Composable
fun NotificationsScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var muteNotifications by remember { mutableStateOf(false) }
    var showPreviews by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Notifications",
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
            WaSettingsSectionHeader("Messages")

            WaSettingsSwitchRow(
                icon = Icons.Default.NotificationsOff,
                title = "Mute notifications",
                subtitle = "Turn off all sound and vibration alerts for this group's chat and activities.",
                checked = muteNotifications,
                onCheckedChange = { muteNotifications = it }
            )

            WaSettingsDivider()

            WaSettingsValueRow(
                icon = Icons.Default.MusicNote,
                title = "Notification tone",
                value = "Default (Chime)",
                onClick = { /* Open tone picker */ }
            )
            
            WaSettingsDivider()

            WaSettingsSwitchRow(
                icon = Icons.Default.Visibility,
                title = "Show previews",
                subtitle = "Preview message text inside new message notifications.",
                checked = showPreviews,
                onCheckedChange = { showPreviews = it }
            )
        }
    }
}
