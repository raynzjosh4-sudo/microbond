package com.microbond.app.feature_you.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.you.MicrobondSettingsListTile
import com.microbond.app.core.components.you.ProfileHeaderTile
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark

@Composable
fun YouScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToQr: () -> Unit = {},
    onNavigateToSetting: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val displayName by AuthSessionManager.displayName.collectAsState()
    val avatarUrl by AuthSessionManager.avatarUrl.collectAsState()
    val aboutStatus by AuthSessionManager.aboutStatus.collectAsState()
    val phoneNumber by AuthSessionManager.phoneNumber.collectAsState()

    LaunchedEffect(Unit) {
        AuthSessionManager.load(context)
        try {
            val response = NetworkClient.apiService.getCurrentUserProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                val profile = response.body()!!.data!!
                AuthSessionManager.updateProfileDetails(
                    context = context,
                    name = profile.fullName,
                    avatarUrl = profile.profilePictureUrl,
                    about = profile.aboutStatus,
                    phone = profile.phoneNumber
                )
            }
        } catch (_: Exception) {
            // Keep local cached session data if offline
        }
    }

    val resolvedName = remember(displayName, phoneNumber) {
        if (!displayName.isNullOrBlank()) displayName!!
        else if (!phoneNumber.isNullOrBlank()) phoneNumber!!
        else "Justin Mason"
    }

    val resolvedAbout = remember(aboutStatus) {
        if (!aboutStatus.isNullOrBlank()) aboutStatus!!
        else "Saving for the future - Available"
    }

    val initials = remember(resolvedName) {
        val parts = resolvedName.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "MB"
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "You",
                showMoreIcon = false
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                ProfileHeaderTile(
                    name = resolvedName,
                    about = resolvedAbout,
                    initials = initials,
                    avatarUrl = avatarUrl,
                    onProfileClick = onNavigateToProfile,
                    onQrCodeClick = onNavigateToQr
                )
                HorizontalDivider(
                    color = MicrobondDividerDark,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                MicrobondSettingsListTile(
                    title = "Account",
                    subtitle = "Security notifications, change PIN",
                    icon = Icons.Default.Key,
                    onClick = { onNavigateToSetting("Account") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Privacy",
                    subtitle = "Blocked contacts, profile visibility",
                    icon = Icons.Default.Lock,
                    onClick = { onNavigateToSetting("Privacy") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Linked Accounts",
                    subtitle = "Mobile money, bank accounts and limits",
                    icon = Icons.Default.AccountBalance,
                    onClick = { onNavigateToSetting("Linked Accounts") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Threads",
                    subtitle = "Theme, wallpapers and thread history",
                    icon = Icons.Default.Chat,
                    onClick = { onNavigateToSetting("Threads") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Notifications",
                    subtitle = "Message, group and transaction alerts",
                    icon = Icons.Default.Notifications,
                    onClick = { onNavigateToSetting("Notifications") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Storage and data",
                    subtitle = "Network usage and downloads",
                    icon = Icons.Default.DataUsage,
                    onClick = { onNavigateToSetting("Storage") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Help",
                    subtitle = "Help center and contact support",
                    icon = Icons.Default.HelpOutline,
                    onClick = { onNavigateToSetting("Help") }
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Invite a friend",
                    subtitle = "",
                    icon = Icons.Default.PeopleAlt,
                    onClick = { onNavigateToSetting("Invite") }
                )
            }
        }
    }
}