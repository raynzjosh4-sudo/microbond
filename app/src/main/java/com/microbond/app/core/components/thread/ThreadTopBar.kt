package com.microbond.app.core.components.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.components.avatar.MicroBondAvatar

@Composable
fun ThreadTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onAudioCallClick: () -> Unit,
    onMoreClick: () -> Unit,
    avatarPainter: Painter? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate Back",
                tint = MicrobondTextPrimary
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2329)),
                contentAlignment = Alignment.Center
            ) {
                MicroBondAvatar(
                    painter = avatarPainter,
                    name = title,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }

        IconButton(onClick = onVideoCallClick) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Video Call",
                tint = MicrobondTextPrimary
            )
        }
        IconButton(onClick = onAudioCallClick) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Audio Call",
                tint = MicrobondTextPrimary
            )
        }
        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = MicrobondTextPrimary
            )
        }
    }
}
