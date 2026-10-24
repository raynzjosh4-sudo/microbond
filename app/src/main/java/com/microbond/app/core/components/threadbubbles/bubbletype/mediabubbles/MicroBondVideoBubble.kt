package com.microbond.app.core.components.threadbubbles.bubbletype.mediabubbles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondVideoBubble(painter: Painter?, duration: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) {
        Box(
            Modifier.fillMaxWidth().height(160.dp)
                .background(Color(0xFF293331), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (painter != null) Image(
                painter,
                null,
                Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            ); Icon(
            Icons.Default.PlayCircle,
            null,
            tint = Color.White,
            modifier = Modifier.size(44.dp)
        )
        }
        Text(duration, color = MicroBondMuted, fontSize = 11.sp)
    }
}
