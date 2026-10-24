package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet

@Composable
fun MinimumTrustScoreScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var minimumScore by remember { mutableStateOf("70 / 100") }
    var showScorePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Minimum trust score",
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
            WaSettingsSectionHeader("Eligibility requirements")

            WaSettingsValueRow(
                icon = Icons.Default.StarHalf,
                title = "Score required for loans",
                value = minimumScore,
                onClick = { showScorePicker = true }
            )

            WaSettingsDivider()

            Text(
                text = "Members whose internal credit score falls below this threshold will automatically be blocked from requesting new loans until they rebuild their standing.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showScorePicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select minimum score",
            options = listOf("50 / 100", "60 / 100", "70 / 100", "80 / 100", "90 / 100"),
            currentSelection = minimumScore,
            onSelect = { 
                minimumScore = it 
                showScorePicker = false 
            },
            onDismiss = { showScorePicker = false }
        )
    }
}
