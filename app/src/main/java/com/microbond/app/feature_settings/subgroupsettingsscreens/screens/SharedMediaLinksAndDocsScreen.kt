package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow

@Composable
fun SharedMediaLinksAndDocsScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Media, links, and docs",
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
            Spacer(modifier = Modifier.height(16.dp))

            WaSettingsRow(
                icon = Icons.Default.Image,
                title = "Media",
                subtitle = "24 Photos, 3 Videos",
                onClick = { /* Open media tab */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Description,
                title = "Docs",
                subtitle = "12 PDFs, 4 Spreadsheets",
                onClick = { /* Open docs tab */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.Link,
                title = "Links",
                subtitle = "15 Shared Links",
                onClick = { /* Open links tab */ }
            )
            
            WaSettingsDivider()
        }
    }
}
