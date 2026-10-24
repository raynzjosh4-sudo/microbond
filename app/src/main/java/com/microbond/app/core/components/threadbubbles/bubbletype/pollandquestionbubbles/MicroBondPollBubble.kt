package com.microbond.app.core.components.threadbubbles.bubbletype.pollandquestionbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.microbond.app.core.components.threadbubbles.BubbleAuthorType
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

private val PollAccent = Color(0xFFF96684)
private val PollTrack = Color(0xFF333E43)
private val PollMuted = Color(0xFF8696A0)

@Composable
fun MicroBondPollBubble(
    question: String,
    options: List<String>,
    meta: MicroBondMeta,
    allowMultipleAnswers: Boolean = false,
    questionSubtitle: String = "",
    footerText: String = "No votes yet",
    modifier: Modifier = Modifier
) {
    PollCard(
        question = question,
        options = options.map { label ->
            PollOptionResult(
                label = label,
                percentage = 0,
                isSelected = false
            )
        },
        meta = meta,
        subtitle = questionSubtitle.ifBlank {
            if (allowMultipleAnswers) "Select all that apply" else "Choose one option"
        },
        footerText = footerText,
        modifier = modifier
    )
}

private data class PollOptionResult(
    val label: String,
    val percentage: Int,
    val isSelected: Boolean
)

@Composable
private fun PollCard(
    question: String,
    options: List<PollOptionResult>,
    meta: MicroBondMeta,
    subtitle: String,
    footerText: String,
    modifier: Modifier
) {
    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = BubbleAuthorType.USER,
        showTimestamp = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
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
                Text(question, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, color = PollMuted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                options.forEach { option ->
                    PollOptionRow(
                        label = option.label,
                        percentage = option.percentage,
                        selected = option.isSelected
                    )
                    Spacer(Modifier.height(12.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(footerText, color = PollMuted, fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(meta.time, color = PollMuted, fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = PollMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PollOptionRow(label: String, percentage: Int, selected: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (selected) PollAccent else Color.Transparent)
                .border(
                    2.dp,
                    if (selected) PollAccent else PollMuted.copy(alpha = 0.5f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = Color.White, fontSize = 15.sp)
                Text("$percentage%", color = PollMuted, fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(PollTrack)
            ) {
                if (percentage > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((percentage.coerceIn(0, 100)) / 100f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(PollAccent)
                    )
                }
            }
        }
    }
}
