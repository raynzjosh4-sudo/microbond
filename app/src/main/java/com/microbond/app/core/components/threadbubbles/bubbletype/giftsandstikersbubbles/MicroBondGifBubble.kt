package com.microbond.app.core.components.threadbubbles.bubbletype.giftsandstikersbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun MicroBondGifBubble(title: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) {
        Box(
            Modifier.fillMaxWidth().height(120.dp)
                .background(Color(0xFF3C3446), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text("GIF", color = Color.White, fontSize = 28.sp) }; Text(title, color = MicroBondInk)
    }
}
