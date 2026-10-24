package com.microbond.app.screens.threadscreen.threadscreenwidgets

import androidx.compose.ui.graphics.painter.Painter
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.model.MicroBondReply
import com.microbond.app.core.components.threadbubbles.model.MicroBondStatus
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import java.util.Locale



fun ThreadEvent.toBubbleMeta(
    avatarPainter: Painter?,
    onUserTap: (() -> Unit)? = null,
    onReplyClick: ((Int) -> Unit)? = null
) = MicroBondMeta(
    time = time,
    outgoing = isCurrentUser,
    status = when (status) {
        MessageStatus.PENDING -> MicroBondStatus.Sending
        MessageStatus.SENT -> MicroBondStatus.Sent
        MessageStatus.DELIVERED -> MicroBondStatus.Delivered
        MessageStatus.READ -> MicroBondStatus.Read
        MessageStatus.FAILED -> MicroBondStatus.Failed
        null -> MicroBondStatus.Sent
    },
    senderName = senderName,
    senderPhone = senderPhone,
    senderColor = senderColor,
    senderInitials = senderInitials,
    senderAvatar = avatarPainter,
    senderAvatarUrl = senderAvatarUrl,
    onSenderClick = if ((senderName != null || senderPhone != null || isCurrentUser) && onUserTap != null) onUserTap else null,
    reply = if (quotedSender != null && quotedText != null) {
        MicroBondReply(
            author = quotedSender,
            preview = quotedText,
            accent = quotedAccentColor ?: senderColor
        )
    } else null,
    replyTargetId = quotedEventId,
    onReplyClick = quotedEventId?.let { targetId -> { onReplyClick?.invoke(targetId) } }
)

fun ThreadEvent.replyPreviewText(): String = when (type) {
    ThreadBubbleType.TEXT, ThreadBubbleType.REPLY ->
        text.ifBlank { "Message" }
    ThreadBubbleType.VOICE -> "🎤 Voice message"
    ThreadBubbleType.IMAGE -> text.ifBlank { "📷 Photo" }
    ThreadBubbleType.VIDEO -> text.ifBlank { "🎥 Video" }
    ThreadBubbleType.DOCUMENT -> text.ifBlank { "📄 ${documentData?.fileName ?: "Document"}" }
    ThreadBubbleType.CONTACT -> contactData.firstOrNull()?.name
        ?.let { "Contact: $it" } ?: "Contact"
    ThreadBubbleType.DEPOSIT -> amount?.let { "💰 Deposit · $it" } ?: "💰 Deposit"
    ThreadBubbleType.DEPOSIT_STATUS -> "Deposit status"
    ThreadBubbleType.POLL, ThreadBubbleType.SECURITY_POLL ->
        pollData?.question ?: securityPoll?.questionTitle ?: "📊 Poll"
    ThreadBubbleType.STAT_CUSTOM_CHART -> statCustomChart?.title ?: text.ifBlank { "Chart" }
    ThreadBubbleType.WEEKLY_REPORT -> "Weekly saving report"
    ThreadBubbleType.SECURITY_RULES -> "Group rules"
    ThreadBubbleType.SECURITY_RULE_UPDATE -> securityRuleUpdate?.ruleText ?: "Group rule update"
    ThreadBubbleType.LOCATION -> locationPlace ?: "Location"
    ThreadBubbleType.STAT_GROUP_SNAPSHOT -> "Savings group snapshot"
    ThreadBubbleType.STAT_GROUP_PROGRESS -> "Group progress"
    ThreadBubbleType.GROUP_HEALTH -> "Group health"
    else -> text.ifBlank {
        type.name.replace("_", " ").lowercase(Locale.getDefault())
            .replaceFirstChar { it.uppercase(Locale.getDefault()) }
    }
}