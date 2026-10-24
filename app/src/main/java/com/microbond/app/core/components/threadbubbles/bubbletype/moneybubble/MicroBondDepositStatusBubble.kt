package com.microbond.app.core.components.threadbubbles.bubbletype.moneybubble

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
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
import com.microbond.app.core.network.api.deposits.DepositStatusFilter
import com.microbond.app.core.ui.theme.MicrobondDepositFullGreen
import com.microbond.app.core.ui.theme.MicrobondDepositHalfYellow
import com.microbond.app.core.ui.theme.MicrobondDepositHeaderIconBg
import com.microbond.app.core.ui.theme.MicrobondDepositIconBg
import com.microbond.app.core.ui.theme.MicrobondDepositNotPaidRed
import com.microbond.app.core.ui.theme.MicrobondDepositRowBg
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondPillTextDark
import com.microbond.app.core.ui.theme.MicrobondPureWhite

/**
 * Interactive Deposit Status Widget Bubble displaying real-time counters
 * for Full, Half/Partial, and Not Deposited members.
 * Tapping any row dispatches [onPillClick] with the selected [DepositStatusFilter].
 */
@Composable
fun MicroBondDepositStatusBubble(
    meta: MicroBondMeta,
    fullDepositCount: Int = 0,
    halfDepositCount: Int = 0,
    notDepositedCount: Int = 0,
    modifier: Modifier = Modifier,
    title: String = "Deposit status",
    dateSubtitle: String? = null,
    onPillClick: (DepositStatusFilter) -> Unit = {}
) {
    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = if (meta.outgoing) BubbleAuthorType.USER else BubbleAuthorType.SYSTEM,
        showTimestamp = true,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MicrobondDepositHeaderIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Deposit status icon",
                        tint = MicrobondPureWhite,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = MicrobondPureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!dateSubtitle.isNullOrBlank()) {
                        Text(
                            text = dateSubtitle,
                            color = MicrobondInactiveGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Full Deposit Row
            DepositStatusRow(
                label = "FULL DEPOSIT",
                count = fullDepositCount,
                pillColor = MicrobondDepositFullGreen,
                onClick = { onPillClick(DepositStatusFilter.FULL) }
            )

            // Half Deposit Row
            DepositStatusRow(
                label = "HALF DEPOSIT",
                count = halfDepositCount,
                pillColor = MicrobondDepositHalfYellow,
                onClick = { onPillClick(DepositStatusFilter.PARTIAL) }
            )

            // Not Deposited Row
            DepositStatusRow(
                label = "NOT DEPOSITED",
                count = notDepositedCount,
                pillColor = MicrobondDepositNotPaidRed,
                onClick = { onPillClick(DepositStatusFilter.NOT_DEPOSITED) }
            )
        }
    }
}

@Composable
private fun DepositStatusRow(
    label: String,
    count: Int,
    pillColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MicrobondDepositRowBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // User Icon circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MicrobondDepositIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MicrobondInactiveGray,
                modifier = Modifier.size(16.dp)
            )
        }

        // Pill
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(pillColor)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = MicrobondPillTextDark,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Count
        Box(
            modifier = Modifier.width(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = MicrobondPureWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
