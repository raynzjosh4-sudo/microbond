package com.microbond.app.core.components.thread

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import kotlin.random.Random

/**
 * WhatsApp-style voice message bubble with play button,
 * waveform visualization, duration, and optional avatar overlay.
 */
@Composable
fun MicroBondVoiceBubble(
    duration: String,
    meta: MicroBondMeta,
    avatar: Painter? = null,
    avatarInitials: String = "A",
    onPlayClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // Generate stable random waveform amplitudes
    val amplitudes = remember {
        val rng = Random(duration.hashCode())
        List(40) { 0.15f + rng.nextFloat() * 0.75f }
    }

    MicroBondBubbleSurface(meta, modifier, showTimestamp = false) {
        Column {
            Row(
                modifier = Modifier.widthIn(min = 200.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Play button
                IconButton(onClick = onPlayClick, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play voice message",
                        tint = MicroBondInk,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Waveform visualization
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                ) {
                    val gap = 1.5f.dp.toPx()
                    val barWidth = 2.dp.toPx()
                    val centerY = size.height / 2f
                    val totalSlots = ((size.width + gap) / (barWidth + gap)).toInt()
                        .coerceAtMost(amplitudes.size)

                    for (i in 0 until totalSlots) {
                        val amplitude = amplitudes[i]
                        val barHeight = size.height * amplitude * 0.9f
                        val x = i * (barWidth + gap)
                        drawRoundRect(
                            color = if (i < (totalSlots * 0.35f).toInt())
                                MicroBondAccent else MicroBondMuted.copy(alpha = 0.55f),
                            topLeft = Offset(x, centerY - barHeight / 2f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Avatar overlay (WhatsApp shows small avatar on voice notes)
                MicroBondAvatar(
                    painter = avatar,
                    name = meta.senderName ?: avatarInitials,
                    modifier = Modifier.size(40.dp),
                    backgroundColor = meta.senderColor
                )
            }

            // Bottom row: duration left, timestamp far right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 200.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(duration, color = MicroBondMuted, fontSize = 11.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(meta.time, color = MicroBondMuted, fontSize = 11.sp)
                    if (meta.outgoing) MicroBondStatusIcon(meta.status)
                }
            }
        }
    }
}

