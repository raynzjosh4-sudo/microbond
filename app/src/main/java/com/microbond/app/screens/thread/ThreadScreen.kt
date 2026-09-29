package com.microbond.app.screens.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.microbond.app.R
import com.microbond.app.core.components.thread.ThreadInputBar
import com.microbond.app.core.components.thread.ThreadMessageBubble
import com.microbond.app.core.components.thread.ThreadTopBar
import com.microbond.app.core.components.thread.ThreadWallpaper
import com.microbond.app.core.components.thread.MicroBondDateDivider
import com.microbond.app.core.components.thread.MicroBondMeta
import com.microbond.app.core.components.thread.MicroBondReply
import com.microbond.app.core.components.thread.MicroBondReplyBubble
import com.microbond.app.core.components.thread.MicroBondStatus
import com.microbond.app.core.components.thread.MicroBondVoiceBubble
import com.microbond.app.core.components.thread.MicroBondImageBubble
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.dummydata.dummyThreadHistory

@Composable
fun ThreadScreen(
    entityName: String,
    onNavigateBack: () -> Unit
) {
    var inputValue by remember { mutableStateOf("") }
    val memberAvatar = painterResource(R.drawable.ic_account_box)

    Scaffold(
        topBar = {
            ThreadTopBar(
                title = entityName,
                subtitle = "Group · 4 members",
                onBackClick = onNavigateBack,
                onVideoCallClick = { /* Handle video */ },
                onAudioCallClick = { /* Handle audio */ },
                onMoreClick = { /* Handle options */ },
                avatarPainter = memberAvatar
            )
        },
        bottomBar = {
            ThreadInputBar(
                inputValue = inputValue,
                onValueChange = { inputValue = it },
                onSubmitClick = { inputValue = "" },
                onMicClick = { /* Handle voice note */ }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues)
        ) {
            ThreadWallpaper(modifier = Modifier.fillMaxSize())
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout = true
            ) {
                items(dummyThreadHistory.asReversed(), key = { it.id }) { event ->
                    val senderAvatarPainter = event.senderAvatarRes?.let { painterResource(it) }
                    val imagePainter = event.imageRes?.let { painterResource(it) }
                    
                    val meta = event.toBubbleMeta(senderAvatarPainter)
                    when (event.type) {
                        ThreadBubbleType.TEXT -> ThreadMessageBubble(
                            text = event.text,
                            timestamp = event.time,
                            isCurrentUser = event.isCurrentUser,
                            status = event.status,
                            senderName = event.senderName,
                            senderPhone = event.senderPhone,
                            senderInitials = event.senderInitials,
                            senderColor = event.senderColor,
                            senderAvatar = senderAvatarPainter
                        )
                        ThreadBubbleType.REPLY -> MicroBondReplyBubble(
                            reply = MicroBondReply(
                                author = event.quotedSender.orEmpty(),
                                preview = event.quotedText.orEmpty(),
                                accent = event.senderColor
                            ),
                            text = event.text,
                            meta = meta
                        )
                        ThreadBubbleType.VOICE -> MicroBondVoiceBubble(
                            duration = event.text,
                            meta = meta,
                            avatar = senderAvatarPainter,
                            avatarInitials = event.senderInitials
                        )
                        ThreadBubbleType.IMAGE -> MicroBondImageBubble(
                            painter = imagePainter,
                            caption = event.text.takeIf { it.isNotBlank() },
                            meta = meta
                        )
                    }
                }
                item { MicroBondDateDivider("Today") }
            }
        }
    }
}

private fun ThreadEvent.toBubbleMeta(avatarPainter: Painter?) = MicroBondMeta(
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
    senderAvatar = avatarPainter
)
