package com.microbond.app.core.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

/**
 * A circular user avatar component. 
 * If a painter (image) is provided, it crops it to a circle.
 * If no image is provided, it extracts the first letter of the user's name.
 */
@Composable
fun MicroBondAvatar(
    painter: Painter? = null,
    name: String? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF247A70),
) {
    val initial = name?.take(1)?.uppercase() ?: "A"
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "User Avatar",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
