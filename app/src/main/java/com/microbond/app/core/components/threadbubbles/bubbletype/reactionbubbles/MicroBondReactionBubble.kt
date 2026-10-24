package com.microbond.app.core.components.threadbubbles.bubbletype.reactionbubbles

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondReactionBubble(emoji: String, count: Int, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier, maxWidthFraction = 0.34f) {
        Text(
            "$emoji  $count",
            color = MicroBondInk,
            fontSize = 18.sp
        )
    }
}
