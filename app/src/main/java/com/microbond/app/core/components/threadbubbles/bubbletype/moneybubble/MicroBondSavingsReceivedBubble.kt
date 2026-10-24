package com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.SavingsReceivedData

private val SrAccent     = Color(0xFFF96684)
private val SrMuted      = Color(0xFF8696A0)
private val SrGreen      = Color(0xFF4ADE80)
private val SrCardBorder = Color.White.copy(alpha = 0.12f)
private val SrInnerBg    = Color.Black.copy(alpha = 0.15f)

@Composable
fun MicroBondSavingsReceivedBubble(
    meta: MicroBondMeta,
    data: SavingsReceivedData,
    modifier: Modifier = Modifier
) {
    // USER author type → gets avatar, tail, and the standard incoming bubble background
    _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = _root_ide_package_.com.microbond.app.core.components.threadbubbles.BubbleAuthorType.USER,
        showTimestamp = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // ── Header icon card ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SrCardBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wallet + check badge
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Wallet,
                        contentDescription = null,
                        tint = SrAccent,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                    )
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = SrAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = data.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.subtitle,
                        color = SrMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Details rows ──────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SrInnerBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        tint = SrAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recipient: ", color = SrMuted, fontSize = 13.sp)
                    Text(
                        data.recipientName,
                        color = SrAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = SrGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Status: ", color = SrMuted, fontSize = 13.sp)
                    Text(
                        data.status,
                        color = SrGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = SrCardBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // ── Footer ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = data.footerText, color = SrMuted, fontSize = 11.sp)
                Text(text = meta.time, color = SrMuted, fontSize = 11.sp)
            }
        }
    }
}
