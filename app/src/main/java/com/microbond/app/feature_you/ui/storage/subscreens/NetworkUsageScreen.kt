package com.microbond.app.feature_you.ui.storage.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkUsageScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Network usage",
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Usage",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "150 MB sent • 120 MB received",
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                WaSettingsDivider()
            }

            item {
                UsageRow(icon = Icons.Default.Sync, title = "Group Sync & Transactions", sent = "50 MB", received = "60 MB")
                UsageRow(icon = Icons.Default.Message, title = "Messages", sent = "20 MB", received = "15 MB")
                UsageRow(icon = Icons.Default.Photo, title = "Media", sent = "70 MB", received = "40 MB")
                UsageRow(icon = Icons.Default.Call, title = "Calls", sent = "10 MB", received = "5 MB")
                WaSettingsDivider()
            }

            item {
                WaSettingsRow(
                    icon = Icons.Default.Delete,
                    title = "Reset statistics",
                    subtitle = "Last reset: Never",
                    onClick = { /* Reset stats */ }
                )
            }
        }
    }
}

@Composable
private fun UsageRow(icon: ImageVector, title: String, sent: String, received: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MicrobondInactiveGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MicrobondTextPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                Text("Sent: $sent", color = MicrobondInactiveGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Received: $received", color = MicrobondInactiveGray, fontSize = 13.sp)
            }
        }
    }
}
