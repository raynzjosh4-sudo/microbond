package com.microbond.app.core.components.thread

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.Modifier
import com.microbond.app.core.data_classes_and_enums.MessageStatus

@Composable
fun ThreadMessageBubble(
    text: String,
    timestamp: String,
    isCurrentUser: Boolean,
    status: MessageStatus?,
    senderName: String? = null,
    senderPhone: String? = null,
    senderInitials: String = senderName?.take(1)?.uppercase() ?: "?",
    senderColor: Color = Color(0xFF7ED7C4),
    senderAvatar: Painter? = null,
    modifier: Modifier = Modifier
) {
    val bubbleStatus = when (status) {
        MessageStatus.PENDING -> MicroBondStatus.Sending
        MessageStatus.SENT -> MicroBondStatus.Sent
        MessageStatus.DELIVERED -> MicroBondStatus.Delivered
        MessageStatus.READ -> MicroBondStatus.Read
        MessageStatus.FAILED -> MicroBondStatus.Failed
        null -> MicroBondStatus.Sent
    }

    MicroBondTextBubble(
        text = text,
        meta = MicroBondMeta(
            time = timestamp,
            outgoing = isCurrentUser,
            status = bubbleStatus,
            senderName = senderName,
            senderPhone = senderPhone,
            senderInitials = senderInitials,
            senderColor = senderColor,
            senderAvatar = senderAvatar
        ),
        modifier = modifier
    )
}
