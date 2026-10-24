package com.microbond.app.feature_you.ui.storage.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Folder
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStorageScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Manage storage",
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
                        text = "34.5 MB",
                        color = MicrobondActiveGreen,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Used",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = { 0.1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MicrobondActiveGreen,
                        trackColor = Color.DarkGray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Microbond Media", color = MicrobondInactiveGray, fontSize = 12.sp)
                        Text("Free up space", color = MicrobondInactiveGray, fontSize = 12.sp)
                    }
                }
                WaSettingsDivider()
            }

            item {
                WaSettingsRow(
                    icon = Icons.Default.Folder,
                    title = "Group Ledgers & Files",
                    subtitle = "12.1 MB",
                    onClick = { /* Open detailed list */ }
                )
                WaSettingsRow(
                    icon = Icons.Default.Storage,
                    title = "Local Encrypted DB",
                    subtitle = "15.0 MB",
                    onClick = { /* Open detailed list */ }
                )
                WaSettingsRow(
                    icon = Icons.Default.Delete,
                    title = "Clear Cache",
                    subtitle = "Frees up 7.4 MB. Won't delete your transaction records.",
                    onClick = { /* Clear cache */ }
                )
            }
        }
    }
}
