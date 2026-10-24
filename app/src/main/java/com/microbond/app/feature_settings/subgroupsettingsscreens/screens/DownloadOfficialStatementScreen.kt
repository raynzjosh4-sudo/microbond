package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

@Composable
fun DownloadOfficialStatementScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedPeriod by remember { mutableStateOf("Last 30 Days") }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Download statement",
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
            WaSettingsSectionHeader("Generate official record")

            WaSettingsValueRow(
                icon = Icons.Default.DateRange,
                title = "Statement period",
                value = selectedPeriod,
                onClick = { /* Open selection dialog */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.PictureAsPdf,
                title = "Format",
                subtitle = "PDF Document",
                onClick = {}
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Download,
                title = "Download now",
                subtitle = "Save a stamped copy of your group contributions to your device.",
                onClick = { /* Trigger download */ }
            )
        }
    }
}
