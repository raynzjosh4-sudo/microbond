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
fun MicroBondSystemBubble(text: String, modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) { Text(text, color = MicroBondMuted, fontSize = 11.sp, modifier = modifier.background(Color(0x55343B3B), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) }
}
