package com.microbond.app.feature_you.ui.privacy

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.microbond.app.authentication_features.data.AuthSessionManager

@Composable
fun AppLockScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val isBiometricsEnabled by AuthSessionManager.isBiometricsEnabled.collectAsState()

    BackHandler(onBack = onBack)
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "App lock",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WaSettingsSwitchRow(
                icon = Icons.Default.Fingerprint,
                title = "Require biometric unlock",
                subtitle = "When enabled, you'll need to use your fingerprint or face to open Microbond.",
                checked = isBiometricsEnabled,
                onCheckedChange = { AuthSessionManager.setBiometricsEnabled(context, it) }
            )
        }
    }
}
