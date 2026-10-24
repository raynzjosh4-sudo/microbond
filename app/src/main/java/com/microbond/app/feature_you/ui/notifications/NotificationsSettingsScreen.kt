package com.microbond.app.feature_you.ui.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

@Composable
fun NotificationsSettingsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    var conversationTones by remember { mutableStateOf(true) }
    var highPriorityMsgs by remember { mutableStateOf(true) }
    var reactionMsgs by remember { mutableStateOf(true) }
    
    var highPriorityGroups by remember { mutableStateOf(true) }
    var reactionGroups by remember { mutableStateOf(true) }
    
    var depositAlerts by remember { mutableStateOf(true) }
    var payoutAlerts by remember { mutableStateOf(true) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Notifications",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Conversation tones",
                    subtitle = "Play sounds for incoming and outgoing messages.",
                    checked = conversationTones,
                    onCheckedChange = { conversationTones = it }
                )
                WaSettingsDivider()
            }

            // Messages
            item {
                WaSettingsSectionHeader("Messages")
                WaSettingsValueRow(
                    icon = null,
                    title = "Notification tone",
                    value = "Default (ringtone)",
                    onClick = { /* Open sound picker */ }
                )
                WaSettingsValueRow(
                    icon = null,
                    title = "Vibrate",
                    value = "Default",
                    onClick = { /* Open vibrate picker */ }
                )
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Reaction notifications",
                    subtitle = "Show notifications for reactions to messages you send",
                    checked = reactionMsgs,
                    onCheckedChange = { reactionMsgs = it }
                )
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Use high priority notifications",
                    subtitle = "Show previews of notifications at the top of the screen",
                    checked = highPriorityMsgs,
                    onCheckedChange = { highPriorityMsgs = it }
                )
                WaSettingsDivider()
            }

            // Groups
            item {
                WaSettingsSectionHeader("Groups")
                WaSettingsValueRow(
                    icon = null,
                    title = "Notification tone",
                    value = "Default (ringtone)",
                    onClick = { /* Open sound picker */ }
                )
                WaSettingsValueRow(
                    icon = null,
                    title = "Vibrate",
                    value = "Default",
                    onClick = { /* Open vibrate picker */ }
                )
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Reaction notifications",
                    subtitle = "Show notifications for reactions to messages you send",
                    checked = reactionGroups,
                    onCheckedChange = { reactionGroups = it }
                )
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Use high priority notifications",
                    subtitle = "Show previews of notifications at the top of the screen",
                    checked = highPriorityGroups,
                    onCheckedChange = { highPriorityGroups = it }
                )
                WaSettingsDivider()
            }

            // Transactions & Microbond specific
            item {
                WaSettingsSectionHeader("Transactions & Cycles")
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Deposit reminders",
                    subtitle = "Get notified before group deposits are due",
                    checked = depositAlerts,
                    onCheckedChange = { depositAlerts = it }
                )
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Payout alerts",
                    subtitle = "Get instantly notified when you receive a payout",
                    checked = payoutAlerts,
                    onCheckedChange = { payoutAlerts = it }
                )
            }
        }
    }
}
