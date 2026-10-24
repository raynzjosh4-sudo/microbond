package com.microbond.app

import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.screens.threadscreen.threadscreenwidgets.toBubbleMeta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun threadEvent_meta_keeps_sender_click_handler() {
        val event = ThreadEvent(
            id = 9,
            text = "Hi",
            time = "9:41 AM",
            isCurrentUser = false,
            status = MessageStatus.READ,
            senderName = "Hellen",
            senderPhone = "+256 700 000000",
            senderColor = MicrobondActiveGreen,
            type = ThreadBubbleType.TEXT
        )

        val meta = event.toBubbleMeta(
            avatarPainter = null,
            onUserTap = {
                // callback should be retained
            }
        )

        assertEquals("Hellen", meta.senderName)
        assertEquals("+256 700 000000", meta.senderPhone)
        assertNotNull(meta.onSenderClick)
    }
}