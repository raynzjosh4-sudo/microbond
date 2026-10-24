package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateMembershipMeRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import kotlinx.coroutines.launch

@Composable
fun MuteCustomNotificationsScreen(
    groupName: String,
    groupId: String = "",
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val scope = rememberCoroutineScope()
    var muteNotifications by remember { mutableStateOf(false) }
    var notificationTone by remember { mutableStateOf("Default (Chime)") }
    var showTonePicker by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) return@LaunchedEffect
        try {
            val resp = NetworkClient.apiService.getMyMembership(groupId)
            resp.body()?.data?.let { data ->
                muteNotifications = data.muteNotifications
                notificationTone = data.customNotificationTone ?: "Default (Chime)"
            }
        } catch (_: Exception) {}
    }

    fun patchMembership(mute: Boolean? = null, tone: String? = null) {
        if (groupId.isBlank()) return
        scope.launch {
            isSaving = true
            try {
                NetworkClient.apiService.updateMyMembership(
                    groupId,
                    UpdateMembershipMeRequest(
                        muteNotifications = mute,
                        customNotificationTone = tone
                    )
                )
            } catch (_: Exception) {}
            isSaving = false
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Mute / Custom notifications",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isSaving) {
                        Box(modifier = Modifier.padding(end = 12.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            WaSettingsSectionHeader("Messages and alerts")

            WaSettingsSwitchRow(
                icon = Icons.Default.NotificationsOff,
                title = "Mute notifications",
                subtitle = "Turn off all sound and vibration alerts for this group's chat and activities.",
                checked = muteNotifications,
                onCheckedChange = { newVal ->
                    muteNotifications = newVal
                    patchMembership(mute = newVal)
                }
            )

            if (!muteNotifications) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.MusicNote,
                    title = "Custom notification tone",
                    value = notificationTone,
                    onClick = { showTonePicker = true }
                )
            }
        }
    }

    if (showTonePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select notification tone",
            options = listOf("Default (Chime)", "Cash Register", "Alert", "Silent", "Vibrate Only"),
            currentSelection = notificationTone,
            onSelect = { selected ->
                notificationTone = selected
                showTonePicker = false
                patchMembership(tone = selected)
            },
            onDismiss = { showTonePicker = false }
        )
    }
}
