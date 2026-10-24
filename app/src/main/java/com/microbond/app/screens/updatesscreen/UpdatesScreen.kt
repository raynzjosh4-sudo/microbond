package com.microbond.app.screens.updates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.updates.MyUpdateTile
import com.microbond.app.core.components.updates.UpdateListTile
import com.microbond.app.core.components.updates.UpdatesTopBar
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

data class UpdateItem(
    val id: Int,
    val name: String,
    val time: String,
    val hasUnviewedUpdates: Boolean
)

@Composable
fun UpdatesScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToTextUpdate: () -> Unit
) {
    val recentUpdates = listOf(
        UpdateItem(1, "Denes", "48 minutes ago", true),
        UpdateItem(2, "Justin", "11:50 AM", true),
        UpdateItem(3, "Dijah", "Yesterday", false)
    )

    Scaffold(
        topBar = {
            UpdatesTopBar(
                onSearchClick = { },
                onCameraClick = onNavigateToCamera,
                onMoreClick = { }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FloatingActionButton(
                    onClick = onNavigateToTextUpdate,
                    containerColor = Color(0xFF2B343B),
                    contentColor = MicrobondTextPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Text Update",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = onNavigateToCamera,
                    containerColor = MicrobondActiveGreen,
                    contentColor = MicrobondDarkBackground,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Update"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Status",
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    MyUpdateTile(onClick = onNavigateToCamera)
                }

                item {
                    Text(
                        text = "Recent updates",
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(recentUpdates) { update ->
                    UpdateListTile(
                        name = update.name,
                        time = update.time,
                        hasUnviewedUpdates = update.hasUnviewedUpdates,
                        onClick = { }
                    )
                }
            }
        }
    }
}
