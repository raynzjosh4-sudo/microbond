package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote

@Composable
fun ApprovalProcessScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var showStrategyPicker by remember { mutableStateOf(false) }
    var selectedStrategy by remember { mutableStateOf("Majority Vote") }
    val strategies = listOf("Admin Only", "Majority Vote", "Unanimous Vote", "Committee Approval")

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Approval process",
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
            WaSettingsSectionHeader("Consensus rules")
            
            WaSettingsValueRow(
                icon = Icons.Default.HowToVote,
                title = "Approval strategy",
                value = selectedStrategy,
                onClick = { showStrategyPicker = true }
            )

            WaSettingsDivider()

            Text(
                text = when(selectedStrategy) {
                    "Admin Only" -> "Only designated group administrators can approve requests. Fast, but centralized."
                    "Majority Vote" -> "More than 50% of the active voting members must approve the request."
                    "Unanimous Vote" -> "Every single voting member must approve. Slowest, but ensures complete agreement."
                    "Committee Approval" -> "A randomly or specifically selected subset of members must approve."
                    else -> ""
                },
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showStrategyPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select approval strategy",
            options = strategies,
            currentSelection = selectedStrategy,
            onSelect = { 
                selectedStrategy = it 
                showStrategyPicker = false 
            },
            onDismiss = { showStrategyPicker = false }
        )
    }
}
