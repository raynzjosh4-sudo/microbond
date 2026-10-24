package com.microbond.app.core.components.threadbubbles.bubbletype.rulesbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Shield
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
import com.microbond.app.core.data_classes_and_enums.SecurityRuleUpdateData
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

private val RuMuted = MicrobondInactiveGray
private val RuChipBg = MicrobondActivePillDark
private val RuChipText = MicrobondActiveGreen

@Composable
fun MicroBondSecurityRuleUpdateBubble(
    meta: MicroBondMeta,
    data: SecurityRuleUpdateData,
    modifier: Modifier = Modifier
) {
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
            // ── Header: shield icon + title ───────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.title,
                    color = MicrobondTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ── Rule text ─────────────────────────────────────────────
            Text(
                text = data.ruleText,
                color = MicrobondTextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Status chip ───────────────────────────────────────────
            Text(
                text = data.statusLabel,
                color = RuChipText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(RuChipBg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(5.dp))

            // ── Footer ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${data.approvalNote} \u00b7 ${data.approvalDate}",
                    color = RuMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = meta.time,
                    color = RuMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
