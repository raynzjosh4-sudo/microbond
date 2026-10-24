package com.microbond.app.core.components.threadbubbles.bubbletype.linkbubbles

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondAccent
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

@Composable
fun MicroBondLinkPreviewBubble(title: String, url: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(72.dp)
                    .background(Color(0xFF3C4946), RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Link,
                    null,
                    tint = MicroBondAccent,
                    modifier = Modifier.size(34.dp)
                )
            }; Text(title, color = MicroBondInk); Text(
            url,
            color = Color(0xFF77C8FF),
            fontSize = 12.sp
        )
        }
    }
}
