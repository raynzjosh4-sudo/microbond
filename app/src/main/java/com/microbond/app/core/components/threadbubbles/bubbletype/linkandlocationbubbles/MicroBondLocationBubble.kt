package com.microbond.app.core.components.threadbubbles.bubbletype.linkandlocationbubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
fun MicroBondLocationBubble(
    place: String,
    meta: MicroBondMeta,
    modifier: Modifier = Modifier
) {
    MicroBondBubbleSurface(meta, modifier) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFF1B2B33), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MicroBondAccent,
                    modifier = Modifier.size(42.dp)
                )
            }
            Text(place, color = MicroBondInk, fontSize = 14.sp)
        }
    }
}
