package com.microbond.app.core.components.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar

import androidx.compose.foundation.layout.PaddingValues

/**
 * Core bubble surface that wraps all message types.
 * The tail appears at the TOP of the bubble (WhatsApp style) — pointing
 * right for outgoing and left for incoming messages.
 */
@Composable
fun MicroBondBubbleSurface(
    meta: MicroBondMeta,
    modifier: Modifier = Modifier,
    maxWidthFraction: Float = 0.82f,
    showTimestamp: Boolean = true,
    contentPadding: PaddingValues? = null,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val cornerRadius = with(density) { 16.dp.toPx() } // Increased for a more rounded look
    val tailWidth = with(density) { 8.dp.toPx() }
    val tailHeight = with(density) { 16.dp.toPx() }

    // WhatsApp-style shape: tail at top, smooth curve into the body
    val shape = GenericShape { size, _ ->
        val left = if (meta.outgoing) 0f else tailWidth
        val right = if (meta.outgoing) size.width - tailWidth else size.width
        val radius = cornerRadius.coerceAtMost(size.height / 2f)

        if (meta.outgoing) {
            // Start at top-left corner (rounded)
            moveTo(left + radius, 0f)
            // Top edge
            lineTo(right, 0f)
            // Tail: top-right, curves out smoothly
            quadraticTo(right + tailWidth * 0.8f, 0f, right + tailWidth, -2f)
            // Curves back smoothly into the right vertical wall
            quadraticTo(right, tailHeight * 0.2f, right, tailHeight)
            // Right edge down
            lineTo(right, size.height - radius)
            // Bottom-right corner
            quadraticTo(right, size.height, right - radius, size.height)
            // Bottom edge
            lineTo(left + radius, size.height)
            // Bottom-left corner
            quadraticTo(left, size.height, left, size.height - radius)
            // Left edge up
            lineTo(left, radius)
            // Top-left corner
            quadraticTo(left, 0f, left + radius, 0f)
        } else {
            // Start at tail point (top-left)
            moveTo(0f, -2f)
            // Curves smoothly into the top edge
            quadraticTo(left * 0.2f, 0f, left, 0f)
            // Top edge
            lineTo(right - radius, 0f)
            // Top-right corner
            quadraticTo(right, 0f, right, radius)
            // Right edge down
            lineTo(right, size.height - radius)
            // Bottom-right corner
            quadraticTo(right, size.height, right - radius, size.height)
            // Bottom edge
            lineTo(left + radius, size.height)
            // Bottom-left corner
            quadraticTo(left, size.height, left, size.height - radius)
            // Left edge up to tail junction
            lineTo(left, tailHeight)
            // Tail curving smoothly from vertical wall to tip
            quadraticTo(left, tailHeight * 0.2f, 0f, -2f)
        }
        close()
    }
    val showSenderAvatar = !meta.outgoing && meta.senderName != null

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        val avatarWidth = if (showSenderAvatar) 34.dp else 0.dp
        val bubbleMaxWidth = (maxWidth * maxWidthFraction - avatarWidth).coerceAtLeast(120.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (meta.outgoing) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (showSenderAvatar) {
                MicroBondAvatar(
                    painter = meta.senderAvatar,
                    name = meta.senderName,
                    modifier = Modifier.size(28.dp),
                    backgroundColor = meta.senderColor
                )
                Spacer(modifier = Modifier.size(width = 4.dp, height = 1.dp))
            }

            Column(modifier = modifier.widthIn(min = 80.dp, max = bubbleMaxWidth)) {
                Box(
                    modifier = Modifier
                        .background(
                            if (meta.outgoing) MicroBondOutgoing else MicroBondIncoming,
                            shape
                        )
                        .padding(
                            contentPadding ?: PaddingValues(
                                start = if (meta.outgoing) 8.dp else 12.dp,
                                end = if (meta.outgoing) 12.dp else 8.dp,
                                top = 2.dp,
                                bottom = 2.dp
                            )
                        )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (meta.senderName != null && !meta.outgoing) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = meta.senderName,
                                    color = meta.senderColor,
                                    fontSize = 12.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (meta.senderPhone != null) {
                                    Spacer(modifier = Modifier.size(width = 8.dp, height = 1.dp))
                                    Text(
                                        text = meta.senderPhone,
                                        color = MicroBondMuted,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        content()
                        if (showTimestamp) {
                            Row(
                                modifier = Modifier.align(Alignment.End),
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
        }
    }
}

@Composable
fun MicroBondStatusIcon(status: MicroBondStatus) {
    val tint = when (status) {
        MicroBondStatus.Read -> MicroBondTickBlue
        MicroBondStatus.Failed -> Color(0xFFFF746A)
        else -> MicroBondMuted
    }
    val image = when (status) {
        MicroBondStatus.Sending -> Icons.Default.Schedule
        MicroBondStatus.Sent -> Icons.Default.Done
        MicroBondStatus.Failed -> Icons.Default.ErrorOutline
        else -> Icons.Default.DoneAll
    }
    Icon(
        imageVector = image,
        contentDescription = status.name,
        tint = tint,
        modifier = Modifier.size(16.dp)
    )
}

@Composable
fun MicroBondDateDivider(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = MicroBondMuted,
            fontSize = 12.sp,
            modifier = Modifier
                .background(MicroBondDatePill, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
