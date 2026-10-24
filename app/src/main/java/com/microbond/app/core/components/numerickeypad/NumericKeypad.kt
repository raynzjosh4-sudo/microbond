package com.microbond.app.core.components.numerickeypad

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.microbond.app.core.components.keypadbutton.KeypadButton
import com.microbond.app.core.ui.theme.MicrobondInactiveGray

/**
 * Circular numeric keypad modeled after standard banking and security inputs.
 */
@Composable
fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    isBackspaceEnabled: Boolean,
    modifier: Modifier = Modifier,
    keySpacing: Dp = 12.dp,
    buttonSize: Dp = 72.dp,
    leftAction: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(keySpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1: 1, 2, 3
        Row(horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            KeypadButton(digit = "1", onClick = { onDigitClick("1") }, buttonSize = buttonSize)
            KeypadButton(digit = "2", onClick = { onDigitClick("2") }, buttonSize = buttonSize)
            KeypadButton(digit = "3", onClick = { onDigitClick("3") }, buttonSize = buttonSize)
        }

        // Row 2: 4, 5, 6
        Row(horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            KeypadButton(digit = "4", onClick = { onDigitClick("4") }, buttonSize = buttonSize)
            KeypadButton(digit = "5", onClick = { onDigitClick("5") }, buttonSize = buttonSize)
            KeypadButton(digit = "6", onClick = { onDigitClick("6") }, buttonSize = buttonSize)
        }

        // Row 3: 7, 8, 9
        Row(horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            KeypadButton(digit = "7", onClick = { onDigitClick("7") }, buttonSize = buttonSize)
            KeypadButton(digit = "8", onClick = { onDigitClick("8") }, buttonSize = buttonSize)
            KeypadButton(digit = "9", onClick = { onDigitClick("9") }, buttonSize = buttonSize)
        }

        // Row 4: Left action (e.g. Biometrics), 0 in center, Backspace on right
        Row(horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            Box(
                modifier = Modifier.size(buttonSize),
                contentAlignment = Alignment.Center
            ) {
                leftAction?.invoke()
            }

            // 0 button
            KeypadButton(digit = "0", onClick = { onDigitClick("0") }, buttonSize = buttonSize)

            // Backspace button with tactile press animation
            val backspaceSource = remember { MutableInteractionSource() }
            val isBackspacePressed by backspaceSource.collectIsPressedAsState()
            val backspaceScale by animateFloatAsState(
                targetValue = if (isBackspacePressed && isBackspaceEnabled) 0.88f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "BackspaceScale"
            )

            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .graphicsLayer {
                        scaleX = backspaceScale
                        scaleY = backspaceScale
                    }
                    .clip(CircleShape)
                    .clickable(
                        enabled = isBackspaceEnabled,
                        interactionSource = backspaceSource,
                        indication = if (isBackspaceEnabled) ripple(bounded = true, color = Color.White) else null,
                        onClick = onBackspaceClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = "Delete digit",
                    tint = if (isBackspaceEnabled) MicrobondInactiveGray else MicrobondInactiveGray.copy(alpha = 0.25f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun NumericKeypadPreview() {
    NumericKeypad(
        onDigitClick = {},
        onBackspaceClick = {},
        isBackspaceEnabled = true
    )
}
