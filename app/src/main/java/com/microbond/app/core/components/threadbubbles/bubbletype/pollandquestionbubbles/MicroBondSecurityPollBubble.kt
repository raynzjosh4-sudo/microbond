package com.microbond.app.core.components.threadbubbles.bubbletype.pollandquestionbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.PollOption
import com.microbond.app.core.data_classes_and_enums.SecurityPollData

private val PollAccent  = Color(0xFFF96684)
private val PollTrackBg = Color(0xFF333E43)
private val PollMuted   = Color(0xFF8696A0)

// ─────────────────────────────────────────────────────────────────────────────
// Poll Option Row (declared first so it is visible to MicroBondSecurityPollBubble)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PollOptionRow(
    option: PollOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (option.isSelected) PollAccent else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (option.isSelected) PollAccent else PollMuted.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (option.isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option.label, color = Color.White, fontSize = 15.sp)
                Text(text = "${option.percentage}%", color = PollMuted, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(PollTrackBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(option.percentage / 100f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(PollAccent)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MicroBondSecurityPollBubble(
    meta: MicroBondMeta,
    data: SecurityPollData,
    onOptionClick: ((optionId: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        // ── Poll card ─────────────────────────────────────────────────────────
        _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface(
            meta = meta,
            authorType = _root_ide_package_.com.microbond.app.core.components.threadbubbles.BubbleAuthorType.USER,
            showTimestamp = false
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Sender-coloured left accent line
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                        .background(meta.senderColor)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 4.dp)
                ) {
                    Text(
                        text = data.questionTitle,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.questionSubtitle,
                        color = PollMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    data.options.forEach { option ->
                        PollOptionRow(
                            option = option,
                            onClick = { onOptionClick?.invoke(option.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = data.footerText, color = PollMuted, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = meta.time, color = PollMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = PollMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Threaded replies ──────────────────────────────────────────────────
        if (data.replies.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(start = 36.dp, top = 1.dp)
            ) {
                data.replies.forEach { reply ->
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .padding(bottom = 2.dp)
                    ) {
                        // Vertical connector line
                        Box(
                            modifier = Modifier
                                .width(1.5.dp)
                                .fillMaxHeight()
                                .background(Color.Gray.copy(alpha = 0.25f))
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        // Reply card — wraps to content
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(_root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondIncoming)
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                com.microbond.app.core.components.avatar.MicroBondAvatar(
                                    painter = null,
                                    name = reply.senderName,
                                    backgroundColor = reply.senderColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = reply.senderName,
                                            color = reply.senderColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = reply.time,
                                            color = PollMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Text(
                                        text = reply.text,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
