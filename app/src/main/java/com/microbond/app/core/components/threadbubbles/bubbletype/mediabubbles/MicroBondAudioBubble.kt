package com.microbond.app.core.components.threadbubbles.chartbubbles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondAccent
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.MicroBondMuted

@Composable
fun MicroBondAudioBubble(title: String, duration: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.MusicNote, null, tint = MicroBondAccent); Column {
            Text(
                title,
                color = MicroBondInk
            ); Text(duration, color = MicroBondMuted, fontSize = 11.sp)
        }
        }
    }
}
