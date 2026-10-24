package com.microbond.app.core.components.threadbubbles.bubbletype.replybubble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.MicroBondReplyBg
import com.microbond.app.core.components.threadbubbles.MicroBondReplyBgOutgoing
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.model.MicroBondReply

/**
 * WhatsApp-style reply bubble: colored left border strip, dark tinted
 * background for the quoted section, then the reply text below.
 */
@Composable
fun MicroBondReplyBubble(
    reply: MicroBondReply,
    text: String,
    meta: MicroBondMeta,
    modifier: Modifier = Modifier
) {
    MicroBondBubbleSurface(meta, modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Quoted section with colored left border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (meta.outgoing) MicroBondReplyBgOutgoing else MicroBondReplyBg)
            ) {
                // Colored accent bar on the left
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
                                    if (meta.outgoing) MicroBondReplyBgOutgoing else MicroBondReplyBg,
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
            // Reply message text
            Text(text, color = MicroBondInk, fontSize = 15.sp)
        }
    }
}
