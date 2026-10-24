package com.microbond.app.feature_you.ui.storage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow

@Composable
fun StorageSettingsScreen(
    onNavigateToSetting: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var keepOfflineRecords by remember { mutableStateOf(true) }
    var useLessDataSync by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Storage and data",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                WaSettingsRow(
                    icon = Icons.Default.Folder,
                    title = "Manage storage",
                    subtitle = "34.5 MB",
                    onClick = { onNavigateToSetting("Manage storage") }
                )
                WaSettingsRow(
                    icon = Icons.Default.DataUsage,
                    title = "Network usage",
                    subtitle = "150 MB sent • 120 MB received",
                    onClick = { onNavigateToSetting("Network usage") }
                )
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Use less data for syncing",
                    subtitle = "Delay non-critical background data sync to save mobile data",
                    checked = useLessDataSync,
                    onCheckedChange = { useLessDataSync = it }
                )
                WaSettingsDivider()
            }

            // Financial Records
            item {
                WaSettingsSectionHeader("Financial Records & Database")
                WaSettingsSwitchRow(
                    icon = null,
                    title = "Keep financial records offline",
                    subtitle = "Store a local encrypted copy of all group cycles, transactions, and ledgers for fast offline access and record keeping.",
                    checked = keepOfflineRecords,
                    onCheckedChange = { keepOfflineRecords = it }
                )
                WaSettingsRow(
                    icon = null,
                    title = "Export transaction history",
                    subtitle = "Download all your historical records as CSV or PDF",
                    onClick = { /* Export */ }
                )
                WaSettingsDivider()
            }

            // Media auto-download
            item {
                WaSettingsSectionHeader("Media auto-download")
                WaSettingsRow(
                    icon = null,
                    title = "When using mobile data",
                    subtitle = "Photos",
                    onClick = { /* Settings */ }
                )
                WaSettingsRow(
                    icon = null,
                    title = "When connected on Wi-Fi",
                    subtitle = "All media",
                    onClick = { /* Settings */ }
                )
                WaSettingsRow(
                    icon = null,
                    title = "When roaming",
                    subtitle = "No media",
                    onClick = { /* Settings */ }
                )
                WaSettingsDivider()
            }

            // Media upload quality
            item {
                WaSettingsSectionHeader("Media upload quality")
                WaSettingsValueRow(
                    icon = null,
                    title = "Document and photo upload quality",
                    subtitle = "Choose the quality of transaction receipts and photos sent in groups",
                    value = "Auto (recommended)",
                    onClick = { /* Open quality picker */ }
                )
            }
        }
    }
}
