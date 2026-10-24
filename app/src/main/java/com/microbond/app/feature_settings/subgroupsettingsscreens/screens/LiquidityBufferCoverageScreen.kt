package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Shield
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
fun LiquidityBufferCoverageScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var requireBuffer by remember { mutableStateOf(true) }
    var bufferPercentage by remember { mutableStateOf("20%") }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Liquidity buffer",
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
            WaSettingsSectionHeader("Reserve funds")

            WaSettingsSwitchRow(
                icon = Icons.Default.Shield,
                title = "Maintain liquidity buffer",
                subtitle = "Prevent the group treasury from being completely drained. Useful for unexpected emergencies.",
                checked = requireBuffer,
                onCheckedChange = { requireBuffer = it }
            )

            if (requireBuffer) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Minimum reserve ratio",
                    value = bufferPercentage,
                    onClick = { /* Open percentage picker */ }
                )
            }
        }
    }
}
