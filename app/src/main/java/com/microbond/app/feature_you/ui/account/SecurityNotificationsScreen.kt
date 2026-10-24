package com.microbond.app.feature_you.ui.account

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow

/**
 * Local device preferences manager for security alerts and notifications.
 */
object SecurityNotificationPreferences {
    private const val PREFS_NAME = "microbond_security_prefs"
    private const val KEY_SECURITY_NOTIFICATIONS_ENABLED = "security_notifications_enabled"

    fun isSecurityNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SECURITY_NOTIFICATIONS_ENABLED, true)
    }

    fun setSecurityNotificationsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SECURITY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
}

@Composable
fun SecurityNotificationsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var showNotifications by remember {
        mutableStateOf(SecurityNotificationPreferences.isSecurityNotificationsEnabled(context))
    }

    val openWebPage: (String) -> Unit = remember(context) {
        { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Security & Account Protection",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Hero Security Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MicrobondActiveGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Account Protection",
                        tint = MicrobondActiveGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Top Headline
            Text(
                text = "Your account and financial transactions are protected",
                color = MicrobondTextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Description Body
            Text(
                text = "Microbond uses bank-grade 256-bit encryption and hardware-backed device security to protect your account, group savings, and financial activity. This safeguards your:",
                color = MicrobondInactiveGray,
                fontSize = 14.5.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bullet items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                SecurityFeatureBulletItem("Group contributions and rotation payouts")
                SecurityFeatureBulletItem("Mobile money and linked payout destinations")
                SecurityFeatureBulletItem("Personal financial statements and savings history")
                SecurityFeatureBulletItem("Account recovery and identity details")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clickable Learn More
            Text(
                text = "Learn more about Microbond account security",
                color = MicrobondActiveGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable { openWebPage("https://microbond.app/help/security") }
            )

            Spacer(modifier = Modifier.height(28.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MicrobondDividerDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Device Alert Toggle persisted locally
            WaSettingsSwitchRow(
                icon = Icons.Default.Security,
                title = "Security alerts on this device",
                subtitle = "Receive instant alerts when a login occurs from a new device, or when critical security settings (like your PIN or backup email) are changed.",
                checked = showNotifications,
                onCheckedChange = { isChecked ->
                    showNotifications = isChecked
                    SecurityNotificationPreferences.setSecurityNotificationsEnabled(context, isChecked)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SecurityFeatureBulletItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp, end = 12.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MicrobondActiveGreen)
        )
        Text(
            text = text,
            color = MicrobondTextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
