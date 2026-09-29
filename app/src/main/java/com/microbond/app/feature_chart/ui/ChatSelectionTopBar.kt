package com.microbond.app.feature_chart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.menu.MicrobondMenuAction
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

@Composable
fun ChatSelectionTopBar(
    selectedCount: Int,
    isPinned: Boolean,
    isMuted: Boolean,
    menuActions: List<MicrobondMenuAction>,
    onClose: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onMute: () -> Unit,
    onArchive: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MicrobondDarkBackground)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Clear selection", tint = Color.White)
        }
        Text(
            text = selectedCount.toString(),
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onPin, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Default.PushPin,
                contentDescription = if (isPinned) "Unpin chats" else "Pin chats",
                tint = Color.White
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete chats", tint = Color.White)
        }
        IconButton(onClick = onMute, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = if (isMuted) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                contentDescription = if (isMuted) "Unmute chats" else "Mute chats",
                tint = Color.White
            )
        }
        IconButton(onClick = onArchive, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.Archive, contentDescription = "Archive chats", tint = Color.White)
        }
        MicrobondOverflowMenu(
            actions = menuActions,
            modifier = Modifier.size(40.dp),
            contentDescription = "More chat actions"
        )
    }
}