package com.microbond.app.feature_you.ui.privacy

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.microbond.app.authentication_features.data.AuthSessionManager

@Composable
fun PrivacyScreen(
    onNavigateToSetting: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profilePhoto by UserPrivacyManager.profilePhotoVisibility.collectAsState()
    val about by UserPrivacyManager.aboutVisibility.collectAsState()
    val groupAdd by UserPrivacyManager.groupAddPermission.collectAsState()
    val isBiometricsEnabled by AuthSessionManager.isBiometricsEnabled.collectAsState()

    LaunchedEffect(Unit) {
        UserPrivacyManager.load(context)
        UserPrivacyManager.refreshPrivacySettings(context)
    }

    BackHandler(onBack = onBack)
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Privacy",
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
                WaSettingsSectionHeader("Who can see my personal info")
                WaSettingsValueRow(
                    icon = Icons.Default.AccountCircle,
                    title = "Profile photo",
                    value = UserPrivacyManager.toDisplayLabel(profilePhoto),
                    onClick = { onNavigateToSetting("Profile photo") }
                )
                WaSettingsValueRow(
                    icon = Icons.Default.Info,
                    title = "About",
                    value = UserPrivacyManager.toDisplayLabel(about),
                    onClick = { onNavigateToSetting("About privacy") }
                )
            }
            
            item {
                WaSettingsDivider()
                WaSettingsSectionHeader("Connections")
                WaSettingsValueRow(
                    icon = Icons.Default.Group,
                    title = "Groups",
                    value = UserPrivacyManager.toDisplayLabel(groupAdd),
                    onClick = { onNavigateToSetting("Groups privacy") }
                )
            }
            
            item {
                WaSettingsDivider()
                WaSettingsSectionHeader("Security")
                WaSettingsValueRow(
                    icon = Icons.Default.Lock,
                    title = "App lock",
                    value = if (isBiometricsEnabled) "Enabled" else "Disabled",
                    onClick = { onNavigateToSetting("App lock") }
                )
            }
        }
    }
}
