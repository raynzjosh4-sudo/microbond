package com.microbond.app.core.components.thread

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun MicroBondTextBubble(text: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) {
        Text(text, color = MicroBondInk, fontSize = 15.sp)
    }
}
