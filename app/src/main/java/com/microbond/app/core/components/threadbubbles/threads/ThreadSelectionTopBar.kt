package com.microbond.app.core.components.threadbubbles.threads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

@Composable
fun ThreadSelectionTopBar(
    selectedCount: Int,
    onBackClick: () -> Unit,
    onReplyClick: () -> Unit,
    onStarClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit,
    onForwardClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MicrobondDarkBackground) // Matches screenshot
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        
        Text(
            text = selectedCount.toString(),
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (selectedCount == 1) {
            IconButton(onClick = onReplyClick) {
                Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = "Reply", tint = Color.White)
            }
        }
        
        IconButton(onClick = onStarClick) {
            Icon(Icons.Default.StarBorder, contentDescription = "Star", tint = Color.White)
        }
        
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
        }
        
        IconButton(onClick = onCopyClick) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
        }
        
        IconButton(onClick = onForwardClick) {
            Icon(Icons.AutoMirrored.Filled.Forward, contentDescription = "Forward", tint = Color.White)
        }
        
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
        }
    }
}
