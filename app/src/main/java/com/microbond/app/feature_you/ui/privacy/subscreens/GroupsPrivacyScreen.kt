package com.microbond.app.feature_you.ui.privacy.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRadioRow

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_you.ui.privacy.UserPrivacyManager
import kotlinx.coroutines.launch

@Composable
fun GroupsPrivacyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentSetting by UserPrivacyManager.groupAddPermission.collectAsState()
    val selectedLabel = UserPrivacyManager.toDisplayLabel(currentSetting)

    BackHandler(onBack = onBack)
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Groups",
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
            WaSettingsSectionHeader("Who can add me to groups")
            WaSettingsRadioRow(
                title = "Everyone",
                selected = selectedLabel == "Everyone",
                onClick = {
                    coroutineScope.launch {
                        UserPrivacyManager.updateGroupAddPermission(context, "EVERYONE")
                    }
                }
            )
            WaSettingsRadioRow(
                title = "My contacts",
                selected = selectedLabel == "My contacts",
                onClick = {
                    coroutineScope.launch {
                        UserPrivacyManager.updateGroupAddPermission(context, "MY_CONTACTS")
                    }
                }
            )
            WaSettingsRadioRow(
                title = "Nobody",
                selected = selectedLabel == "Nobody",
                onClick = {
                    coroutineScope.launch {
                        UserPrivacyManager.updateGroupAddPermission(context, "NOBODY")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Admins who can't add you to a saving circle or group will have the option of inviting you privately instead.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

