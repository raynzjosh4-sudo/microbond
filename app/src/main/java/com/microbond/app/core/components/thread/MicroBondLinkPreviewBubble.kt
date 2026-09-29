package com.microbond.app.core.components.thread

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MicroBondLinkPreviewBubble(title: String, url: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    MicroBondBubbleSurface(meta, modifier) { Column { Box(Modifier.fillMaxWidth().height(72.dp).background(Color(0xFF3C4946), RoundedCornerShape(7.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Link, null, tint = MicroBondAccent, modifier = Modifier.size(34.dp)) }; Text(title, color = MicroBondInk); Text(url, color = Color(0xFF77C8FF), fontSize = 12.sp) } }
}
