package com.microbond.app.core.components.groupIconImage.groupListTile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlin.collections.getOrNull
import kotlin.collections.isNotEmpty

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
    groupIconUri: String? = chart.groupIconUri,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (groupType) {
        GroupSavingsType.LONG_TERM -> MicrobondActiveGreen
        GroupSavingsType.SHORT_TERM -> Color(0xFFC87A2A)
    }

    // Dynamic ticker: smoothly animates between recent chat message and action messages towards the edge of the phone
    var tickerIndex by remember(chart.name, chart.actionMessages.size) { mutableIntStateOf(0) }
    LaunchedEffect(chart.name, chart.actionMessages) {
        if (chart.actionMessages.isNotEmpty()) {
            val totalItems = 1 + chart.actionMessages.size
            while (true) {
                delay(4000L)
                tickerIndex = (tickerIndex + 1) % totalItems
            }
        } else {
            tickerIndex = 0
        }
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
                    imageUri = groupIconUri ?: chart.groupIconUri,
                    avatarSize = 52.dp,
                    ringThickness = 3.dp,
                    showStatusRing = showStatusRing && memberStatuses.isNotEmpty(),
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
                        AnimatedContent(
                            targetState = tickerIndex,
                            transitionSpec = {
                                (slideInHorizontally(
                                    initialOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                                ) + fadeIn(animationSpec = tween(durationMillis = 400)))
                                .togetherWith(
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(durationMillis = 400))
                                )
                            },
                            label = "MessageActionTicker",
                            modifier = Modifier.weight(1f)
                        ) { currentIndex ->
                            if (currentIndex == 0 || chart.actionMessages.isEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                val actionMsg = chart.actionMessages.getOrNull(currentIndex - 1)
                                    ?: chart.actionMessages.first()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (actionMsg.isAlert) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = "Alert",
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(end = 4.dp)
                                        )
                                    } else if (actionMsg.hasBlueTicks) {
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = "Status",
                                            tint = Color(0xFF40C4FF),
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(end = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = actionMsg.text,
                                        modifier = Modifier.weight(1f),
                                        color = if (actionMsg.isAlert) Color(0xFFFF8A80) else Color(0xFFB0BEC5),
                                        fontWeight = if (actionMsg.isAlert) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
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

                    if (showBadge && chart.unreadCount > 0) {
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

