package com.microbond.app.core.components.threadbubbles.bubbletype.systembubbles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondSystemBg

@Composable
fun MicroBondSystemBubble(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFE9EDEF), // Slightly brighter for readability
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .background(MicroBondSystemBg, RoundedCornerShape(8.dp))
                .then(
                    if (onClick != null) {
                        Modifier.clickable { onClick() }
                    } else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
