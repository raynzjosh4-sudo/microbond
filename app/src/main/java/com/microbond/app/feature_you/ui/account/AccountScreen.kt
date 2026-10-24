package com.microbond.app.feature_you.ui.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.you.MicrobondSettingsListTile
import com.microbond.app.core.network.LogoutRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    onNavigateToSetting: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val localPhone by AuthSessionManager.phoneNumber.collectAsState()
    var displayedPhoneNumber by remember { mutableStateOf(localPhone.orEmpty()) }
    var displayedBackupEmail by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var logoutAllDevices by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }

    LaunchedEffect(localPhone) {
        if (!localPhone.isNullOrBlank()) {
            displayedPhoneNumber = localPhone.orEmpty()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val response = NetworkClient.apiService.getCurrentUserProfile()
            if (response.isSuccessful) {
                val profile = response.body()?.data
                val phone = profile?.phoneNumber
                if (!phone.isNullOrBlank()) {
                    displayedPhoneNumber = phone
                    AuthSessionManager.setPhoneNumber(context, phone)
                }
                val email = profile?.backupEmail
                if (!email.isNullOrBlank()) {
                    displayedBackupEmail = email
                }
            }
        } catch (_: Exception) {
            // Keep local cached session data if network call fails
        }
    }
    BackHandler(onBack = onBack)
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Account",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize().background(MicrobondDarkBackground).padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                MicrobondSettingsListTile(
                    title = "Security notifications",
                    subtitle = "",
                    icon = Icons.Default.Security,
                    onClick = { onNavigateToSetting("Security notifications") },
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Change PIN",
                    subtitle = "Update your 6-digit security PIN",
                    icon = Icons.Default.VpnKey,
                    onClick = { onNavigateToSetting("Change PIN") },
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Backup email",
                    subtitle =
                        if (displayedBackupEmail.isNotBlank()) displayedBackupEmail
                        else "Disaster recovery email",
                    icon = Icons.Default.Email,
                    onClick = { onNavigateToSetting("Backup email") },
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Phone number",
                    subtitle =
                        if (displayedPhoneNumber.isNotBlank()) displayedPhoneNumber
                        else "Tap to view",
                    icon = Icons.Default.PhoneIphone,
                    onClick = { onNavigateToSetting("Phone number") },
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Change number",
                    subtitle = "Migrate your account to a new SIM",
                    icon = Icons.Default.PhoneIphone,
                    onClick = { onNavigateToSetting("Change number") },
                )
            }
            item {
                HorizontalDivider(
                    color = MicrobondDividerDark,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Log out",
                    subtitle = "Sign out from this device",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    titleColor = MicrobondDestructiveRed,
                    iconTint = MicrobondDestructiveRed,
                    onClick = { showLogoutDialog = true },
                )
            }
            item {
                MicrobondSettingsListTile(
                    title = "Delete account",
                    subtitle = "",
                    icon = Icons.Default.Delete,
                    titleColor = MicrobondDestructiveRed,
                    iconTint = MicrobondDestructiveRed,
                    onClick = { onNavigateToSetting("Delete account") },
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoggingOut) showLogoutDialog = false },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Log out of Microbond?",
                    style =
                        TextStyle(
                            color = MicrobondTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                )
            },
            text = {
                Column {
                    Text(
                        text =
                            "Are you sure you want to log out? You will need to verify your phone number and enter your PIN to sign back in.",
                        style =
                            TextStyle(
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            ),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier.fillMaxWidth().clickable(enabled = !isLoggingOut) {
                                logoutAllDevices = !logoutAllDevices
                            },
                    ) {
                        Checkbox(
                            checked = logoutAllDevices,
                            onCheckedChange = { if (!isLoggingOut) logoutAllDevices = it },
                            colors =
                                CheckboxDefaults.colors(
                                    checkedColor = MicrobondActiveGreen,
                                    uncheckedColor = MicrobondInactiveGray,
                                    checkmarkColor = MicrobondDarkBackground,
                                ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log out from all devices",
                            color = MicrobondTextPrimary,
                            fontSize = 14.sp,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isLoggingOut = true
                        coroutineScope.launch {
                            try {
                                NetworkClient.apiService.logout(
                                    LogoutRequest(allDevices = logoutAllDevices)
                                )
                            } catch (_: Exception) {
                                // Proceed with local wipe even if network call fails
                            } finally {
                                isLoggingOut = false
                                showLogoutDialog = false
                                AuthSessionManager.clearSession(context)
                                onLogout()
                            }
                        }
                    },
                    enabled = !isLoggingOut,
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            color = MicrobondDestructiveRed,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "Log out",
                            color = MicrobondDestructiveRed,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }, enabled = !isLoggingOut) {
                    Text(text = "Cancel", color = MicrobondInactiveGray)
                }
            },
        )
    }
}
