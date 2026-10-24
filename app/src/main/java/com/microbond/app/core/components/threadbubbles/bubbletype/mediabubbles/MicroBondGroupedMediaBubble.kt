package com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondGroupedMediaBubble(count: Int, caption: String?, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(
        meta,
        modifier
    ) {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) {
                    Box(
                        Modifier.weight(
                            1f
                        ).height(76.dp).background(Color(0xFF3D4A47), RoundedCornerShape(5.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("+${count - it}", color = MicroBondInk) }
                }
            }; caption?.let { Text(it, color = MicroBondInk) }
        }
    }
}
