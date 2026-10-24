package com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondExpiredBubble(meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(
        meta,
        modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.TimerOff,
                null,
                tint = MicroBondMuted
            ); Text(" Media expired", color = MicroBondMuted)
        }
    }
}
