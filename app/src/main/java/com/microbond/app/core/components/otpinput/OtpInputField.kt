package com.microbond.app.core.components.otpinput

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * 6-digit WhatsApp-style OTP verification code input field.
 * Formatted as `[ - - - ] — [ - - - ]` with real-time slot animations,
 * slot-tracking blinking cursor, zero hardcoded colors, and auto-submit callback when all 6 digits are provided.
 */
@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onOtpComplete: ((String) -> Unit)? = null
) {
    val maxDigits = 6
    var lastSubmittedCode by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Synchronize TextFieldValue selection always to the end of digits
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = otpValue,
                selection = TextRange(otpValue.length)
            )
        )
    }

    androidx.compose.runtime.LaunchedEffect(otpValue) {
        if (textFieldValue.text != otpValue) {
            textFieldValue = TextFieldValue(
                text = otpValue,
                selection = TextRange(otpValue.length)
            )
        }
    }

    // Hidden BasicTextField that captures keystrokes, pastes, and auto-fills
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newTfv ->
                if (!enabled) return@BasicTextField
                val digitsOnly = newTfv.text.filter { it.isDigit() }.take(maxDigits)
                textFieldValue = TextFieldValue(
                    text = digitsOnly,
                    selection = TextRange(digitsOnly.length)
                )
                onOtpChange(digitsOnly)
                if (digitsOnly.length == maxDigits && digitsOnly != lastSubmittedCode) {
                    lastSubmittedCode = digitsOnly
                    onOtpComplete?.invoke(digitsOnly)
                } else if (digitsOnly.length < maxDigits) {
                    lastSubmittedCode = ""
                }
            },
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (otpValue.length == maxDigits && otpValue != lastSubmittedCode) {
                        lastSubmittedCode = otpValue
                        onOtpComplete?.invoke(otpValue)
                    }
                }
            ),
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(color = Color.Transparent),
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .height(52.dp)
        )

        // Visual 3 - 3 slot row
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
        ) {
            // First 3 digits
            for (i in 0 until 3) {
                OtpDigitCell(
                    digit = otpValue.getOrNull(i)?.toString() ?: "",
                    isCurrent = (otpValue.length == i),
                    isError = isError,
                    onCellClick = {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                )
                if (i < 2) Spacer(modifier = Modifier.width(8.dp))
            }

            // Central hyphen separator
            Text(
                text = "—",
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Last 3 digits
            for (i in 3 until maxDigits) {
                OtpDigitCell(
                    digit = otpValue.getOrNull(i)?.toString() ?: "",
                    isCurrent = (otpValue.length == i),
                    isError = isError,
                    onCellClick = {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                )
                if (i < 5) Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun OtpDigitCell(
    digit: String,
    isCurrent: Boolean,
    isError: Boolean,
    onCellClick: () -> Unit = {}
) {
    val lineColor by animateColorAsState(
        targetValue = when {
            isError -> MicrobondErrorColor
            isCurrent -> MicrobondActiveGreen
            digit.isNotEmpty() -> MicrobondActiveGreen
            else -> MicrobondDividerDark
        },
        label = "OtpLineColor"
    )

    // Smooth cursor blink animation for the currently active slot
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    val lineThickness = if (isCurrent || digit.isNotEmpty()) 2.dp else 1.2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(34.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCellClick
            )
    ) {
        Box(
            modifier = Modifier.height(38.dp),
            contentAlignment = Alignment.Center
        ) {
            if (digit.isNotEmpty()) {
                Text(
                    text = digit,
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            } else if (isCurrent) {
                // Centered, slot-tracking blinking cursor
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .graphicsLayer { alpha = cursorAlpha }
                        .background(MicrobondActiveGreen, RoundedCornerShape(1.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalDivider(
            color = lineColor,
            thickness = lineThickness,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun OtpInputFieldPreview() {
    OtpInputField(
        otpValue = "704",
        onOtpChange = {}
    )
}
