package com.microbond.app.core.components.threadbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.model.MicroBondReply
import com.microbond.app.core.components.threadbubbles.model.MicroBondStatus
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondQuotedMessage
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondStatusIcon

enum class BubbleAuthorType { USER, SYSTEM, DATA }

@Composable
fun MicroBondBubbleSurface(
    meta: MicroBondMeta,
    modifier: Modifier = Modifier,
    authorType: BubbleAuthorType = BubbleAuthorType.USER,
    maxWidthFraction: Float = 0.82f,
    showTimestamp: Boolean = true,
    contentPadding: PaddingValues? = null,
    customBackground: Color? = null,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val cornerRadius = with(density) { 8.dp.toPx() }
    val tailWidth = with(density) { 6.5.dp.toPx() }
    val tailHeight = with(density) { 11.5.dp.toPx() }

    // Pixel-perfect, smoothly-curved WhatsApp chat bubble shape matching official WhatsApp Android balloon drawables
    val shape = GenericShape { size, _ ->
        val left = if (meta.outgoing) 0f else tailWidth
        val right = if (meta.outgoing) size.width - tailWidth else size.width
        val r = cornerRadius.coerceAtMost(size.height / 2f)

        if (meta.outgoing) {
            // Top-left rounded corner
            moveTo(left + r, 0f)
            // Top edge to tail base
            lineTo(right, 0f)
            // Tail curves smoothly up to top-right tip
            cubicTo(
                right + tailWidth * 0.5f, 0f,
                size.width - 0.8f, 0.5f,
                size.width, 1.8f
            )
            // Tail curves gracefully back into the right vertical wall
            cubicTo(
                size.width - tailWidth * 0.25f, tailHeight * 0.35f,
                right + tailWidth * 0.15f, tailHeight * 0.75f,
                right, tailHeight
            )
            // Right edge down
            lineTo(right, size.height - r)
            // Bottom-right rounded corner
            quadraticTo(right, size.height, right - r, size.height)
            // Bottom edge
            lineTo(left + r, size.height)
            // Bottom-left rounded corner
            quadraticTo(left, size.height, left, size.height - r)
            // Left edge up
            lineTo(left, r)
            // Top-left corner closure
            quadraticTo(left, 0f, left + r, 0f)
        } else {
            // Start at the smooth top-left tail tip
            moveTo(0f, 1.8f)
            // Curves gracefully into the top wall
            cubicTo(
                0.8f, 0.5f,
                left - tailWidth * 0.5f, 0f,
                left, 0f
            )
            // Top edge
            lineTo(right - r, 0f)
            // Top-right rounded corner
            quadraticTo(right, 0f, right, r)
            // Right edge down
            lineTo(right, size.height - r)
            // Bottom-right rounded corner
            quadraticTo(right, size.height, right - r, size.height)
            // Bottom edge
            lineTo(left + r, size.height)
            // Bottom-left rounded corner
            quadraticTo(left, size.height, left, size.height - r)
            // Left edge up to tail junction
            lineTo(left, tailHeight)
            // Tail curves smoothly back to the tip at (0, 1.8)
            cubicTo(
                left + tailWidth * 0.15f, tailHeight * 0.75f,
                tailWidth * 0.25f, tailHeight * 0.35f,
                0f, 1.8f
            )
        }
        close()
    }
    
    val activeShape = if (authorType == BubbleAuthorType.DATA || authorType == BubbleAuthorType.SYSTEM) {
        RoundedCornerShape(8.dp)
    } else {
        shape
    }

    val hasSenderInfo = !meta.senderName.isNullOrBlank() || !meta.senderPhone.isNullOrBlank()
    val showSenderAvatar = authorType == BubbleAuthorType.USER && !meta.outgoing && hasSenderInfo
    val showSenderName = authorType == BubbleAuthorType.USER && !meta.outgoing && hasSenderInfo

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val avatarWidth: Dp = if (showSenderAvatar) 34.dp else 0.dp
    val dataFraction = if (authorType == BubbleAuthorType.DATA) 0.97f else maxWidthFraction
    val bubbleMaxWidth = (screenWidthDp * dataFraction - avatarWidth).coerceAtLeast(100.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (authorType == BubbleAuthorType.DATA) 0.dp else 8.dp,
                vertical = 0.5.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = when (authorType) {
                BubbleAuthorType.DATA, BubbleAuthorType.SYSTEM -> Arrangement.Center
                else -> if (meta.outgoing) Arrangement.End else Arrangement.Start
            },
            verticalAlignment = Alignment.Top
        ) {
            if (showSenderAvatar) {
                MicroBondAvatar(
                    painter = meta.senderAvatar,
                    avatarUrl = meta.senderAvatarUrl,
                    name = meta.senderName ?: meta.senderPhone,
                    modifier = Modifier
                        .size(28.dp)
                        .then(
                            if (meta.onSenderClick != null) {
                                Modifier.clickable { meta.onSenderClick.invoke() }
                            } else Modifier
                        ),
                    backgroundColor = meta.senderColor
                )
                Spacer(modifier = Modifier.size(width = 6.dp, height = 1.dp))
            }

            Column(modifier = modifier.widthIn(min = 40.dp, max = bubbleMaxWidth)) {
                Box(
                    modifier = Modifier
                        .background(
                            customBackground ?: if (meta.outgoing) MicroBondOutgoing else MicroBondIncoming,
                            activeShape
                        )
                        .padding(
                            contentPadding ?: PaddingValues(
                                start = if (meta.outgoing) 9.dp else 13.dp,
                                end = if (meta.outgoing) 13.dp else 9.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (showSenderName) {
                            val displayName = meta.senderName?.takeIf { it.isNotBlank() } ?: meta.senderPhone ?: "Member"
                            val displayPhone = if (!meta.senderName.isNullOrBlank() && !meta.senderPhone.isNullOrBlank() && meta.senderName != meta.senderPhone) {
                                meta.senderPhone
                            } else null

                            Row(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(bottom = 2.dp)
                                    .then(
                                        if (meta.onSenderClick != null) {
                                            Modifier.clickable { meta.onSenderClick.invoke() }
                                        } else Modifier
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName,
                                    color = meta.senderColor,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                    fontSize = 12.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (displayPhone != null) {
                                    Spacer(modifier = Modifier.size(width = 6.dp, height = 1.dp))
                                    Text(
                                        text = displayPhone,
                                        color = MicroBondMuted,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (meta.reply != null) {
                            MicroBondQuotedMessage(
                                reply = meta.reply,
                                outgoing = meta.outgoing,
                                modifier = if (meta.replyTargetId != null && meta.onReplyClick != null) {
                                    Modifier.clickable(onClick = meta.onReplyClick)
                                } else Modifier
                            )
                        }
                        content()
                        if (showTimestamp) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 2.dp),
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
