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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.you.ProfileHeaderTile
import com.microbond.app.core.components.you.SettingsListTile
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark

@Composable
fun YouScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToQr: () -> Unit = {},
    onNavigateToSetting: (String) -> Unit = {}
) {
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
                    name = "Justin Mason",
                    about = "Saving for the future - Available",
                    initials = "JM",
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
                SettingsListTile(
                    title = "Account",
                    subtitle = "Security notifications, change PIN",
                    icon = Icons.Default.Key,
                    onClick = { onNavigateToSetting("Account") }
                )
            }
            item {
                SettingsListTile(
                    title = "Privacy",
                    subtitle = "Blocked contacts, profile visibility",
                    icon = Icons.Default.Lock,
                    onClick = { onNavigateToSetting("Privacy") }
                )
            }
            item {
                SettingsListTile(
                    title = "Linked Accounts",
                    subtitle = "Mobile money, bank accounts and limits",
                    icon = Icons.Default.AccountBalance,
                    onClick = { onNavigateToSetting("Linked Accounts") }
                )
            }
            item {
                SettingsListTile(
                    title = "Threads",
                    subtitle = "Theme, wallpapers and thread history",
                    icon = Icons.Default.Chat,
                    onClick = { onNavigateToSetting("Threads") }
                )
            }
            item {
                SettingsListTile(
                    title = "Notifications",
                    subtitle = "Message, group and transaction alerts",
                    icon = Icons.Default.Notifications,
                    onClick = { onNavigateToSetting("Notifications") }
                )
            }
            item {
                SettingsListTile(
                    title = "Storage and data",
                    subtitle = "Network usage and downloads",
                    icon = Icons.Default.DataUsage,
                    onClick = { onNavigateToSetting("Storage") }
                )
            }
            item {
                SettingsListTile(
                    title = "Help",
                    subtitle = "Help center and contact support",
                    icon = Icons.Default.HelpOutline,
                    onClick = { onNavigateToSetting("Help") }
                )
            }
            item {
                SettingsListTile(
                    title = "Invite a friend",
                    subtitle = "",
                    icon = Icons.Default.PeopleAlt,
                    onClick = { onNavigateToSetting("Invite") }
                )
            }
        }
    }
}