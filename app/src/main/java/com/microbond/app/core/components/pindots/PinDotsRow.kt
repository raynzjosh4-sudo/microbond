package com.microbond.app.core.components.pindots

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondErrorColor

/**
 * Animated PIN status indicator row:
 * - Fluid spring pop and color transitions for entered digits.
 * - Horizontal shake translation and error tint when invalid PIN is entered.
 */
@Composable
fun PinDotsRow(
    totalDigits: Int,
    enteredDigits: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 14.dp,
    dotSize: Dp = 13.5.dp,
    activeColor: Color = MicrobondActiveGreen,
    inactiveBorderColor: Color = Color(0xFF3F4E56),
    isError: Boolean = false,
    shakeOffset: Float = 0f
) {
    Row(
        modifier = modifier.graphicsLayer {
            translationX = shakeOffset
        },
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalDigits) {
            val isEntered = i < enteredDigits

            // Fluid scale pop when digit is entered
            val dotScale by animateFloatAsState(
                targetValue = if (isEntered) 1.0f else 0.88f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "PinDotScale_$i"
            )

            // Animated color transition (Green normally, Red on mismatch error)
            val targetColor = when {
                isError && isEntered -> MicrobondErrorColor
                isEntered -> activeColor
                else -> Color.Transparent
            }

            val animatedDotColor by animateColorAsState(
                targetValue = targetColor,
                label = "PinDotColor_$i"
            )

            val currentBorderColor = if (isError) {
                MicrobondErrorColor.copy(alpha = 0.6f)
            } else {
                inactiveBorderColor
            }

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        scaleX = dotScale
                        scaleY = dotScale
                    }
                    .clip(CircleShape)
                    .then(
                        if (isEntered) {
                            Modifier.background(animatedDotColor)
                        } else {
                            Modifier
                                .background(Color(0xFF1E282E))
                                .border(1.5.dp, currentBorderColor, CircleShape)
                        }
                    )
            )
        }
    }
}

@Preview
@Composable
private fun PinDotsRowPreview() {
    PinDotsRow(totalDigits = 6, enteredDigits = 4)
}

@Preview
@Composable
private fun PinDotsRowErrorPreview() {
    PinDotsRow(totalDigits = 6, enteredDigits = 6, isError = true)
}
