package com.microbond.app.feature_you.ui.help

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow

@Composable
fun HelpScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Help",
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                WaSettingsRow(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Help Center",
                    subtitle = "Get help, contact us",
                    onClick = { /* Open Help Center */ }
                )
            }
            item {
                WaSettingsRow(
                    icon = Icons.Outlined.People,
                    title = "Contact us",
                    subtitle = "Questions? Need help?",
                    onClick = { /* Open Contact Form */ }
                )
            }
            item {
                WaSettingsRow(
                    icon = Icons.Outlined.Description,
                    title = "Terms and Privacy Policy",
                    subtitle = "Read our terms of service and privacy policy",
                    onClick = { /* Open Terms */ }
                )
            }
            item {
                WaSettingsRow(
                    icon = Icons.Outlined.Info,
                    title = "App info",
                    subtitle = "Microbond Version 1.0.0",
                    onClick = { /* Open App Info */ }
                )
            }
        }
    }
}
