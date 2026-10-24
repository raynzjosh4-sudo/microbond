package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

@Composable
fun ArrearsPolicyScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var showPolicyPicker by remember { mutableStateOf(false) }
    var selectedPolicy by remember { mutableStateOf("Deduct from next payout") }
    val policies = listOf(
        "Deduct from next payout",
        "Block further loans",
        "Liquidate equity shares",
        "No penalty (Informal tracking)"
    )

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Arrears policy",
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
            WaSettingsSectionHeader("Consequences")

            WaSettingsValueRow(
                icon = Icons.Default.Warning,
                title = "Primary action",
                value = selectedPolicy,
                onClick = { showPolicyPicker = true }
            )

            Text(
                text = "Define the consequence when a member falls behind on their scheduled contributions.",
                color = androidx.compose.ui.graphics.Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)
            )
        }
    }

    if (showPolicyPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select arrears policy",
            options = policies,
            currentSelection = selectedPolicy,
            onSelect = { 
                selectedPolicy = it 
                showPolicyPicker = false 
            },
            onDismiss = { showPolicyPicker = false }
        )
    }
}
