package com.microbond.app.core.components.threadbubbles.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/** Shared visual contract for all MicroBond bubble variants. */
data class MicroBondMeta(
    val time: String,
    val outgoing: Boolean = false,
    val status: MicroBondStatus = MicroBondStatus.Read,
    val senderName: String? = null,
    val senderPhone: String? = null,
    val senderColor: Color = Color(0xFF7ED7C4),
    val senderInitials: String = senderName?.take(1)?.uppercase() ?: "?",
    val senderAvatar: Painter? = null,
    val senderAvatarUrl: String? = null,
    val onSenderClick: (() -> Unit)? = null,
    val reply: MicroBondReply? = null,
    val replyTargetId: Int? = null,
    val onReplyClick: (() -> Unit)? = null,
)

enum class MicroBondStatus { Sending, Sent, Delivered, Read, Failed }

data class MicroBondReply(
    val author: String,
    val preview: String,
    val accent: Color = Color(0xFF71D7C2),
)
