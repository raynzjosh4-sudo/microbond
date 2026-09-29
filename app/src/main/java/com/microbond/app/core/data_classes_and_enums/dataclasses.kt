package com.microbond.app.core.data_classes_and_enums

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val isProfileTab: Boolean = false,
    @DrawableRes val profileRes: Int? = null,
    val badgeCount: Int? = null,
    val hasUnreadIndicator: Boolean = false
)

public data class ChartPreview(
    val name: String,
    val initials: String,
    val message: String,
    val time: String,
    val isOutgoingMessage: Boolean = true,
    val unreadCount: Int = 0,
    val isFavorite: Boolean = false,
    val isGroup: Boolean = false,
    val avatarColor: Color,
    val messageStatus: MessageStatus,
    val memberStatuses: List<MemberSaveStatus> = emptyList()
)

enum class ThreadBubbleType { TEXT, REPLY, VOICE, IMAGE }

data class ThreadEvent(
    val id: Int,
    val text: String,
    val time: String,
    val isCurrentUser: Boolean,
    val status: MessageStatus? = null,
    val type: ThreadBubbleType = ThreadBubbleType.TEXT,
    val senderName: String? = null,
    val senderPhone: String? = null,
    val senderInitials: String = senderName?.take(1)?.uppercase() ?: "?",
    val senderColor: Color = Color(0xFF7ED7C4),
    @DrawableRes val senderAvatarRes: Int? = null,
    val quotedSender: String? = null,
    val quotedText: String? = null,
    @DrawableRes val imageRes: Int? = null
)
