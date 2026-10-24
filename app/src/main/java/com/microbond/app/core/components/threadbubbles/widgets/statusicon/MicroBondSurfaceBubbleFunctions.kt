package com.microbond.app.core.components.threadbubbles.widgets.statusicon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondDatePill
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.MicroBondReplyBg
import com.microbond.app.core.components.threadbubbles.MicroBondReplyBgOutgoing
import com.microbond.app.core.components.threadbubbles.MicroBondTickBlue
import com.microbond.app.core.components.threadbubbles.model.MicroBondReply
import com.microbond.app.core.components.threadbubbles.model.MicroBondStatus


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
        modifier = Modifier.size(14.5.dp)
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

@Composable
fun MicroBondQuotedMessage(reply: MicroBondReply, outgoing: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (outgoing) MicroBondReplyBgOutgoing else MicroBondReplyBg)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        reply.accent,
                        RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                    )
                    .padding(start = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (outgoing) MicroBondReplyBgOutgoing else MicroBondReplyBg,
                            RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = reply.author,
                        color = reply.accent,
                        fontSize = 12.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = reply.preview,
                        color = MicroBondMuted,
                        fontSize = 12.5.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
