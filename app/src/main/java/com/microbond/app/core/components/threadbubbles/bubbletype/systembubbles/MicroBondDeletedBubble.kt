package com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondDeletedBubble(meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(
        meta,
        modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Block,
                null,
                tint = MicroBondMuted
            ); Text(
            " This message was deleted",
            color = MicroBondMuted,
            fontStyle = FontStyle.Italic
        )
        }
    }
}
