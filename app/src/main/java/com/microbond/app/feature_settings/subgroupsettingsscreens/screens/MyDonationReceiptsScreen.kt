package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
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
fun MyDonationReceiptsScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "My donation receipts",
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
            WaSettingsSectionHeader("Charity and Causes")

            WaSettingsRow(
                icon = Icons.Default.CardGiftcard,
                title = "View past donations",
                subtitle = "Browse your certificates of appreciation and contribution records.",
                onClick = { /* Open list view */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Download,
                title = "Download tax summary",
                subtitle = "Get a consolidated PDF receipt for tax filing purposes.",
                onClick = { /* Trigger download */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Email,
                title = "Email to accountant",
                subtitle = "Send your donation receipts directly to a third party.",
                onClick = { /* Trigger email flow */ }
            )
        }
    }
}
