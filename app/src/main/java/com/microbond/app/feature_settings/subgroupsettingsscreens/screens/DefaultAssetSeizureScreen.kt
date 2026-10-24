package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader

@Composable
fun DefaultAssetSeizureScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var enableSeizure by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Asset seizure policy",
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
            WaSettingsSectionHeader("Default terms")

            WaSettingsSwitchRow(
                icon = Icons.Default.Gavel,
                title = "Automatic asset liquidation",
                subtitle = "Allow the system to automatically liquidate a defaulter's equity shares to cover unpaid loans.",
                checked = enableSeizure,
                onCheckedChange = { enableSeizure = it }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Policy,
                title = "Legal framework",
                subtitle = "Standard localized legal terms applying to physical assets placed as collateral.",
                onClick = { /* Open legal document */ }
            )
        }
    }
}
