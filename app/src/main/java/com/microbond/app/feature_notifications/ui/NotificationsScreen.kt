package com.microbond.app.feature_notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.menu.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_notifications.components.NotificationFilterRow
import com.microbond.app.feature_notifications.components.NotificationListItem
import com.microbond.app.feature_notifications.model.AppNotification
import com.microbond.app.feature_notifications.model.NotificationCategory

@Composable
fun NotificationsScreen() {
    var notifications by remember { mutableStateOf(sampleNotifications) }
    var selectedCategory by remember { mutableStateOf<NotificationCategory?>(null) }

    val visibleNotifications = notifications.filter { notification ->
        selectedCategory == null || notification.category == selectedCategory
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Notifications",
                showSearchIcon = false,
                showMoreIcon = true,
                menuActions = listOf(
                    MicrobondMenuAction("Mark all as read", onClick = {
                        notifications = notifications.map { it.copy(isRead = true) }
                    })
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                NotificationFilterRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (visibleNotifications.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MicrobondInactiveGray
                        )
                        Text(
                            text = "You're all caught up",
                            color = MicrobondTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "New activity will show up here.",
                            color = MicrobondInactiveGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                visibleNotifications.groupBy { it.section }.forEach { (section, sectionItems) ->
                    item(key = "section-$section") {
                        Text(
                            text = section,
                            color = MicrobondInactiveGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                    items(sectionItems, key = { it.id }) { notification ->
                        NotificationListItem(notification = notification, onClick = {
                            notifications = notifications.map {
                                if (it.id == notification.id) it.copy(isRead = true) else it
                            }
                        })
                    }
                }
            }
        }
    }
}

private val sampleNotifications = listOf(
    AppNotification(
        id = "group-contribution",
        category = NotificationCategory.GROUPS,
        title = "Sunrise Vault",
        message = "A contribution update was shared with your group.",
        time = "9:41 AM",
        section = "Today",
        icon = Icons.Default.Group,
        accentColor = Color(0xFF25D366)
    ),
    AppNotification(
        id = "payment-received",
        category = NotificationCategory.PAYMENTS,
        title = "Contribution received",
        message = "Asha added UGX 50,000 to Mtaa Payout Circle.",
        time = "8:22 AM",
        section = "Today",
        icon = Icons.Default.AccountBalanceWallet,
        accentColor = Color(0xFFE6A84A)
    ),
    AppNotification(
        id = "group-payout",
        category = NotificationCategory.GROUPS,
        title = "Mtaa Payout Circle",
        message = "Your payout turn is coming up this Friday.",
        time = "Yesterday",
        section = "Yesterday",
        icon = Icons.Default.Group,
        accentColor = Color(0xFF25D366)
    ),
    AppNotification(
        id = "weekly-summary",
        category = NotificationCategory.UPDATES,
        title = "Your weekly summary is ready",
        message = "See how your savings progressed this week.",
        time = "Yesterday",
        section = "Yesterday",
        icon = Icons.Default.Campaign,
        accentColor = Color(0xFF53BDEB),
        isRead = true
    )
)