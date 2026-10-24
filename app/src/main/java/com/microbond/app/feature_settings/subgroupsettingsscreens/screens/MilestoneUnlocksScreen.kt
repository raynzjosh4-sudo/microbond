package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow

@Composable
fun MilestoneUnlocksScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableMilestones by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Milestone unlocks",
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
            WaSettingsSectionHeader("Gamification and Goals")

            WaSettingsSwitchRow(
                icon = Icons.Default.WorkspacePremium,
                title = "Enable milestone rewards",
                subtitle = "Automatically unlock lower interest rates or higher borrowing limits for members when they hit specific saving streaks.",
                checked = enableMilestones,
                onCheckedChange = { enableMilestones = it }
            )

            if (enableMilestones) {
                WaSettingsDivider()

                WaSettingsRow(
                    icon = Icons.Default.EmojiEvents,
                    title = "Configure milestones",
                    subtitle = "Set up specific achievement tiers (e.g., 'Bronze Saver', 'Gold Investor').",
                    onClick = { /* Open milestones config */ }
                )
            }
        }
    }
}
