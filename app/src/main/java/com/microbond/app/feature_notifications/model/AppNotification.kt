package com.microbond.app.feature_notifications.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationCategory(val label: String) {
    GROUPS("Groups"),
    PAYMENTS("Payments"),
    UPDATES("Updates")
}

data class AppNotification(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val time: String,
    val section: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isRead: Boolean = false
)