package com.microbond.app.core.components.thread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MicroBondBubbleDemo() {
    MicroBondBubbleTheme {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Bottom) {
            MicroBondDateDivider("Thursday")
            MicroBondTextBubble("When you reach 50 song I send you 5000,", MicroBondMeta("10:57 PM", true))
            MicroBondReplyBubble(MicroBondReply("You", "Yes pap"), "Plan kitty broda", MicroBondMeta("8:20 PM"))
            MicroBondImageBubble(null, "A beautiful day", MicroBondMeta("8:27 PM", true))
            MicroBondVoiceBubble("0:18", MicroBondMeta("8:29 PM", true))
            MicroBondDocumentBubble("MicroBond design notes.pdf", "2.4 MB", MicroBondMeta("8:30 PM"))
            MicroBondLocationBubble("Kampala, Uganda", MicroBondMeta("8:31 PM", true))
            MicroBondDateDivider("Friday")
            MicroBondPollBubble("Where should we meet?", listOf("City Mall", "Garden Café", "At home"), MicroBondMeta("10:02 AM"))
        }
    }
}
