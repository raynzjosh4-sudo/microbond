package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.TrendingUp
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
fun InterestRatesScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var baseRate by remember { mutableStateOf("12% Annually") }
    var dynamicRates by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Interest rates",
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
            WaSettingsSectionHeader("Loan terms")

            WaSettingsValueRow(
                icon = Icons.Default.AccountBalanceWallet,
                title = "Base interest rate",
                value = baseRate,
                onClick = { /* Open rate picker dialog */ }
            )

            WaSettingsDivider()

            WaSettingsSwitchRow(
                icon = Icons.Default.TrendingUp,
                title = "Dynamic interest scaling",
                subtitle = "Automatically lower interest rates for members with high trust scores to reward good behavior.",
                checked = dynamicRates,
                onCheckedChange = { dynamicRates = it }
            )
        }
    }
}
