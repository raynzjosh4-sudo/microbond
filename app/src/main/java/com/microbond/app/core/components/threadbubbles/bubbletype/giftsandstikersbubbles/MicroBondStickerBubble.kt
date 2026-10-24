package com.microbond.app.core.components.threadbubbles.bubbletype.giftsandstikersbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondStickerBubble(label: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier, maxWidthFraction = 0.42f) {
        Box(
            Modifier.size(110.dp).background(Color(0xFF34413F), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(label, color = MicroBondInk, fontSize = 22.sp) }
    }
}
