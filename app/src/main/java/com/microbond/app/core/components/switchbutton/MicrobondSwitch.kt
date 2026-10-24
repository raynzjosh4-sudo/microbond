package com.microbond.app.core.components.switchbutton

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen

/**
 * WhatsApp-accurate animated toggle switch.
 *
 * - Checked  → green pill with white thumb + green check icon inside thumb
 * - Unchecked → dark pill with grey thumb (no icon)
 * - Thumb slides with a 200 ms tween
 *
 * Sizing matches WhatsApp exactly: 51 × 31 dp track, 25 dp thumb.
 *
 * @param checked         Current state
 * @param onCheckedChange Called when user taps the switch
 * @param enabled         When false the switch is dimmed and non-interactive
 */
@Composable
fun MicrobondSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val trackWidth  = 51.dp
    val trackHeight = 31.dp
    val thumbSize   = 25.dp
    val thumbPad    = 3.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPad else thumbPad,
        animationSpec = tween(200),
        label = "thumbOffset"
    )

    val trackColor  = if (checked) MicrobondActiveGreen else Color(0xFF3A4147)
    val thumbColor  = Color.White
    val alpha       = if (enabled) 1f else 0.4f

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(trackHeight / 2))
            .background(trackColor.copy(alpha = alpha))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor.copy(alpha = alpha)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MicrobondActiveGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
