package com.microbond.app.feature_you.ui.privacy.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRadioRow

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.microbond.app.feature_you.ui.privacy.UserPrivacyManager
import kotlinx.coroutines.launch

@Composable
fun ProfilePhotoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentSetting by UserPrivacyManager.profilePhotoVisibility.collectAsState()
    val selectedLabel = UserPrivacyManager.toDisplayLabel(currentSetting)

    BackHandler(onBack = onBack)
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Profile photo",
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
            WaSettingsSectionHeader("Who can see my profile photo")
            WaSettingsRadioRow(
                title = "Everyone",
                selected = selectedLabel == "Everyone",
                onClick = {
                    coroutineScope.launch {
                        UserPrivacyManager.updateProfilePhoto(context, "EVERYONE")
                    }
                }
            )
            WaSettingsRadioRow(
                title = "My contacts",
                selected = selectedLabel == "My contacts",
                onClick = {
                    coroutineScope.launch {
                        UserPrivacyManager.updateProfilePhoto(context, "MY_CONTACTS")
                    }
                }
            )
            WaSettingsRadioRow(
                title = "Nobody",
                selected = selectedLabel == "Nobody",
                onClick = {
                    coroutineScope.launch {
                        UserPrivacyManager.updateProfilePhoto(context, "NOBODY")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "If you don't share your profile photo, you won't be able to see other people's profile photos.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

