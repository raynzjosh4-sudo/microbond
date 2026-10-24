package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.VolunteerActivism
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
fun IssueAChallengeMatchScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableMatch by remember { mutableStateOf(false) }
    var matchAmount by remember { mutableStateOf("100,000 UGX") }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Challenge match",
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
            WaSettingsSectionHeader("Fundraising incentives")

            WaSettingsSwitchRow(
                icon = Icons.Default.Campaign,
                title = "Issue a challenge match",
                subtitle = "Pledge to match other members' contributions up to a specific limit.",
                checked = enableMatch,
                onCheckedChange = { enableMatch = it }
            )

            if (enableMatch) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.VolunteerActivism,
                    title = "Maximum match limit",
                    value = matchAmount,
                    onClick = { /* Open input dialog */ }
                )
            }
        }
    }
}
