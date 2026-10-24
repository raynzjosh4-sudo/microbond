package com.microbond.app.main.widgets

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.TabItem
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark

@Composable
fun MicroBondBottomBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    unreadChartCount: Int = 9,
    hasStatusUpdates: Boolean = true,
    unreadNotificationCount: Int = 0,
    missedCallCount: Int = 0,
    @DrawableRes userAvatarRes: Int? = null,
) {
    val tabs = listOf(
        TabItem(
            title = "Charts",
            selectedIcon = Icons.Filled.Forum,
            unselectedIcon = Icons.Outlined.Forum,
            badgeCount = unreadChartCount
        ),
        TabItem(
            title = "Updates",
            selectedIcon = Icons.Filled.DonutLarge,
            unselectedIcon = Icons.Outlined.DonutLarge,
            hasUnreadIndicator = hasStatusUpdates
        ),
        TabItem(
            title = "Services",
            selectedIcon = Icons.Filled.Handyman,
            unselectedIcon = Icons.Outlined.Handyman,
            badgeCount = if (missedCallCount > 0) missedCallCount else null
        ),
        TabItem(
            title = "Notif",
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.NotificationsNone,
            badgeCount = if (unreadNotificationCount > 0) unreadNotificationCount else null
        ),
        TabItem(
            title = "You",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            isProfileTab = true,
            profileRes = userAvatarRes
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MicrobondDarkBackground)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = MicrobondDividerDark)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index

                // Custom slow animation configuration (800ms) for green selection pill
                val indicatorScale by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                    label = "IndicatorScale"
                )
                val indicatorAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = tween(durationMillis = 600),
                    label = "IndicatorAlpha"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, // Removes default ripple for a clean look
                            onClick = { onTabSelected(index) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(56.dp)
                            .height(32.dp)
                    ) {
                        // The slow-animated background pill widget
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(32.dp)
                                .scale(indicatorScale)
                                .alpha(indicatorAlpha)
                                .clip(CircleShape)
                                .background(MicrobondActivePillDark)
                        )

                        BadgedBox(
                            badge = {
                                if (tab.badgeCount != null && tab.badgeCount > 0) {
                                    Badge(
                                        containerColor = MicrobondActiveGreen,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = if (tab.badgeCount > 99) "99+" else tab.badgeCount.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                } else if (tab.hasUnreadIndicator) {
                                    Badge(containerColor = MicrobondActiveGreen)
                                }
                            }
                        ) {
                            if (tab.isProfileTab && tab.profileRes != null) {
                                Image(
                                    painter = painterResource(id = tab.profileRes),
                                    contentDescription = tab.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) MicrobondDarkBackground else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                            } else {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
 