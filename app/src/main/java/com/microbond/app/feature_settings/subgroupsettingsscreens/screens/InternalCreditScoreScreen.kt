package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.StarRate
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
fun InternalCreditScoreScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableScoring by remember { mutableStateOf(true) }
    var visibility by remember { mutableStateOf("Admins Only") }
    var showVisibilityPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Internal credit score",
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
            WaSettingsSectionHeader("Member Trust System")

            WaSettingsSwitchRow(
                icon = Icons.Default.StarRate,
                title = "Track member credit scores",
                subtitle = "The system will continuously rate members based on prompt payments and meeting obligations.",
                checked = enableScoring,
                onCheckedChange = { enableScoring = it }
            )

            if (enableScoring) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.BarChart,
                    title = "Score visibility",
                    value = visibility,
                    onClick = { showVisibilityPicker = true }
                )
            }
        }
    }

    if (showVisibilityPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Score visibility",
            options = listOf("Admins Only", "Self (Member + Admins)", "Public (All group members)"),
            currentSelection = visibility,
            onSelect = { 
                visibility = it 
                showVisibilityPicker = false 
            },
            onDismiss = { showVisibilityPicker = false }
        )
    }
}
