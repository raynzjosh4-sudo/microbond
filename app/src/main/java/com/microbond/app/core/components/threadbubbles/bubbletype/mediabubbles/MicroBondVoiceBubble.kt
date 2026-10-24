package com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles

import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.components.threadbubbles.MicroBondAccent
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondStatusIcon
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
    audioUri: String? = null,
    onPlayClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var player by remember(audioUri) { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember(audioUri) { mutableStateOf(false) }

    DisposableEffect(audioUri) {
        onDispose {
            player?.release()
        }
    }

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
                IconButton(
                    onClick = {
                        if (audioUri == null) {
                            onPlayClick()
                        } else if (isPlaying) {
                            player?.pause()
                            isPlaying = false
                        } else if (player != null) {
                            player?.start()
                            isPlaying = true
                        } else {
                            try {
                                val mediaPlayer = MediaPlayer()
                                mediaPlayer.setDataSource(context, Uri.parse(audioUri))
                                mediaPlayer.setOnCompletionListener {
                                    isPlaying = false
                                    it.release()
                                    player = null
                                }
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                                player = mediaPlayer
                                isPlaying = true
                            } catch (error: Exception) {
                                player?.release()
                                player = null
                                isPlaying = false
                                Toast.makeText(
                                    context,
                                    "Could not play this voice message: ${error.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause voice message" else "Play voice message",
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
