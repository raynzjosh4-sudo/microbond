package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader

@Composable
fun MyContributionHistoryScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Contribution history",
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
            WaSettingsSectionHeader("Your personal ledger")

            WaSettingsRow(
                icon = Icons.Default.ListAlt,
                title = "View detailed ledger",
                subtitle = "Browse your entire transaction history directly in the app.",
                onClick = { /* Open detailed list view */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Download,
                title = "Download statement",
                subtitle = "Save a secure PDF or CSV copy to your device storage.",
                onClick = { /* Trigger download */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Email,
                title = "Email my records",
                subtitle = "Send your complete contribution history to your registered email address.",
                onClick = { /* Trigger email */ }
            )
        }
    }
}
