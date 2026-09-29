package com.microbond.app.core.components.groupIconImage.groupListTile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.microbond.app.core.components.groupIconImage.MicrobondGroupAvatar
import com.microbond.app.core.components.groupIconImage.widgets.MessageStatusTicks
import com.microbond.app.core.components.groupIconImage.widgets.UnreadCountBadge
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.core.data_classes_and_enums.GroupSavingsType
import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MicrobondGroupListTile(
    chart: ChartPreview,
    memberStatuses: List<MemberSaveStatus>,
    groupType: GroupSavingsType = GroupSavingsType.LONG_TERM,
    showStatusRing: Boolean = true,
    messageType: String? = null,
    showBadge: Boolean = true,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onAvatarClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (groupType) {
        GroupSavingsType.LONG_TERM -> MicrobondActiveGreen
        GroupSavingsType.SHORT_TERM -> Color(0xFFC87A2A)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .background(if (isSelected) Color(0xFF1D3430) else MicrobondDarkBackground)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                MicrobondGroupAvatar(
                    statuses = memberStatuses,
                    avatarSize = 52.dp,
                    ringThickness = 3.dp,
                    showStatusRing = showStatusRing,
                    onClick = { onAvatarClick?.invoke() ?: onClick() },
                    onLongClick = onLongClick
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MicrobondActiveGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MicrobondDarkBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chart.name,
                        modifier = Modifier.weight(1f),
                        color = MicrobondTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = chart.time,
                        color = if (chart.unreadCount > 0) accentColor else MicrobondInactiveGray,
                        fontSize = 12.sp,
                        fontWeight = if (chart.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (messageType == null) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (chart.isOutgoingMessage) {
                                MessageStatusTicks(
                                    status = chart.messageStatus,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = chart.message,
                                modifier = Modifier.weight(1f),
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = messageType,
                            modifier = Modifier.weight(1f),
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (showBadge) {
                        UnreadCountBadge(
                            count = chart.unreadCount,
                            backgroundColor = accentColor
                        )
                    }
                }
            }
        }
    }
}

