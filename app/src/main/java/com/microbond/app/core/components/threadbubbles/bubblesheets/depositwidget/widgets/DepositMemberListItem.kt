package com.microbond.app.core.components.threadbubbles.bubblesheets.depositwidget.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.network.api.deposits.DepositMemberItem
import com.microbond.app.core.ui.theme.AvatarColorHelper
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBorderDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDepositFullGreen
import com.microbond.app.core.ui.theme.MicrobondDepositHalfYellow
import com.microbond.app.core.ui.theme.MicrobondDepositNotPaidRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondNudgeButtonBg
import com.microbond.app.core.ui.theme.MicrobondNudgeButtonText
import com.microbond.app.core.ui.theme.MicrobondNudgeDisabledBg
import com.microbond.app.core.ui.theme.MicrobondNudgeDisabledText
import com.microbond.app.core.ui.theme.MicrobondOnlineGreen
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import java.text.NumberFormat
import java.util.Locale

/**
 * Member row displaying drill-down deposit progress, online presence, and nudge action.
 */
@Composable
fun DepositMemberListItem(
    member: DepositMemberItem,
    isNudged: Boolean,
    onNudgeClick: (DepositMemberItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarBg = rememberAvatarColor(member.avatarColor, member.fullName)
    val formattedPaid = formatCurrency(member.amountPaid)
    val formattedRequired = formatCurrency(member.amountRequired)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar with Online dot
        Box(contentAlignment = Alignment.BottomEnd) {
            MicroBondAvatar(
                avatarUrl = member.profilePictureUrl,
                name = member.fullName,
                backgroundColor = avatarBg,
                modifier = Modifier.size(44.dp)
            )
            if (member.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MicrobondOnlineGreen)
                        .border(1.5.dp, MicrobondDarkBackground, CircleShape)
                )
            }
        }

        // Details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = member.fullName,
                    color = MicrobondPureWhite,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (member.role.equals("ADMIN", ignoreCase = true)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MicrobondActiveGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Admin",
                            color = MicrobondActiveGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Subtitle: phone or last seen
            val subText = if (member.isOnline) {
                "Online"
            } else if (member.phoneNumber.isNotBlank()) {
                member.phoneNumber
            } else {
                "Active recently"
            }
            Text(
                text = subText,
                color = if (member.isOnline) MicrobondActiveGreen else MicrobondInactiveGray,
                fontSize = 12.sp,
                maxLines = 1
            )

            // Amount breakdown & Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "UGX $formattedPaid / $formattedRequired",
                    color = MicrobondPureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "•",
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp
                )
                Text(
                    text = getStatusLabel(member.status),
                    color = getStatusColor(member.status),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Nudge Action Button
        if (member.canNudge) {
            if (isNudged) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MicrobondNudgeDisabledBg)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Reminded",
                        tint = MicrobondNudgeDisabledText,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Nudged",
                        color = MicrobondNudgeDisabledText,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MicrobondNudgeButtonBg)
                        .border(1.dp, MicrobondActiveGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { onNudgeClick(member) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Remind",
                        tint = MicrobondNudgeButtonText,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Remind",
                        color = MicrobondNudgeButtonText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun formatCurrency(amount: Long): String =
    NumberFormat.getNumberInstance(Locale.US).format(amount)

private fun getStatusLabel(status: String): String = when (status.uppercase(Locale.US)) {
    "FULL" -> "Completed"
    "PARTIAL", "HALF" -> "Partial"
    else -> "Unpaid"
}

private fun getStatusColor(status: String): Color = when (status.uppercase(Locale.US)) {
    "FULL" -> MicrobondDepositFullGreen
    "PARTIAL", "HALF" -> MicrobondDepositHalfYellow
    else -> MicrobondDepositNotPaidRed
}

@Composable
private fun rememberAvatarColor(hex: String?, seed: String?): Color {
    if (!hex.isNullOrBlank()) {
        try {
            return Color(android.graphics.Color.parseColor(hex))
        } catch (_: Exception) {}
    }
    return AvatarColorHelper.getColor(seed)
}
