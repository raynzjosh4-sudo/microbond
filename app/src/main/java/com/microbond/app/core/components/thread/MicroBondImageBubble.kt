package com.microbond.app.core.components.thread

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MicroBondImageBubble(
    painter: Painter?,
    caption: String?,
    meta: MicroBondMeta,
    modifier: Modifier = Modifier
) {
    MicroBondBubbleSurface(
        meta = meta, 
        modifier = modifier,
        showTimestamp = false,
        contentPadding = PaddingValues(
            start = if (meta.outgoing) 2.dp else 10.dp,
            end = if (meta.outgoing) 10.dp else 2.dp,
            top = 2.dp,
            bottom = 2.dp
        )
    ) {
        Column {
            if (painter != null) {
                Box {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // If no caption, overlay the timestamp on the image
                    if (caption.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(meta.time, color = Color.White, fontSize = 11.sp)
                            if (meta.outgoing) MicroBondStatusIcon(meta.status)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFF1B2B33), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, null, tint = MicroBondMuted)
                }
            }

            if (!caption.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = caption, 
                        color = MicroBondInk, 
                        fontSize = 15.sp,
                        // Leave space for the floating timestamp
                        modifier = Modifier.padding(bottom = 8.dp, end = 40.dp)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(meta.time, color = MicroBondMuted, fontSize = 11.sp)
                        if (meta.outgoing) MicroBondStatusIcon(meta.status)
                    }
                }
            }
        }
    }
}

