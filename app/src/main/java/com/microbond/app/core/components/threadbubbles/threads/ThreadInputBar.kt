package com.microbond.app.core.components.threadbubbles.threads

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondReply
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

private val MicButtonOrange = Color(0xFFFF6500)

@Composable
fun ThreadInputBar(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onMicClick: () -> Unit,
    onRecordingStateChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    replyTo: MicroBondReply? = null,
    paymentAmountChip: String? = null,
    isInPaymentMode: Boolean = false,
    onPaymentClick: () -> Unit = {},
    onAttachClick: () -> Unit = {},
    onInputFocus: () -> Unit = {},
    onCancelReply: () -> Unit = {},
    isRecording: Boolean = false,
    isRecordingLocked: Boolean = false,
    recordingDuration: String = "0:00",
    onRecordingStart: () -> Unit = {},
    onRecordingFinish: (cancelled: Boolean) -> Unit = {},
    onRecordingLock: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = remember { FocusRequester() }
    var wasInPaymentMode = remember { false }
    val recordingPulse = rememberInfiniteTransition(label = "recording pulse")
    val recordingDotAlpha by recordingPulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "recording dot alpha"
    )

    LaunchedEffect(isInPaymentMode) {
        if (isInPaymentMode) {
            inputFocusRequester.requestFocus()
            keyboardController?.show()
        } else if (wasInPaymentMode) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
        wasInPaymentMode = isInPaymentMode
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Message pill field (Column so it can stack reply above input)
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1F2C34))
        ) {
            // Payment amount chip (animated in after amount is confirmed)
            AnimatedVisibility(
                visible = paymentAmountChip != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                if (paymentAmountChip != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MicrobondActiveGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = paymentAmountChip,
                                color = MicrobondActiveGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✓",
                            color = MicrobondActiveGreen,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Reply Preview Section
            if (replyTo != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF111B21)) // Darker background for the preview
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        // Left Accent Bar
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(4.dp)
                                .background(replyTo.accent)
                        )

                        // Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = replyTo.author,
                                color = replyTo.accent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = replyTo.preview,
                                color = MicrobondInactiveGray,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Close button
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel reply",
                            tint = MicrobondInactiveGray,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(18.dp)
                                .clickable { onCancelReply() }
                        )
                    }
                }
            }

            // Text Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Toll,
                    contentDescription = "Payment",
                    tint = if (isInPaymentMode) MicrobondActiveGreen else MicrobondInactiveGray,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (isInPaymentMode) {
                                onPaymentClick()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            } else {
                                onPaymentClick()
                                inputFocusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        }
                        .padding(2.dp)
                )

                if (isRecording) {
                    Row(
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "●",
                            color = MicButtonOrange,
                            modifier = Modifier.alpha(recordingDotAlpha),
                            fontSize = 15.sp
                        )
                        Text(
                            text = recordingDuration,
                            color = MicrobondTextPrimary,
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = if (isRecordingLocked) "Recording locked" else "‹  Slide to cancel",
                            color = MicrobondInactiveGray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    BasicTextField(
                        value = inputValue,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(inputFocusRequester)
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .onFocusChanged { if (it.isFocused) onInputFocus() },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isInPaymentMode) KeyboardType.Number else KeyboardType.Text,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { onSubmitClick() }
                        ),
                        textStyle = TextStyle(
                            color = MicrobondTextPrimary,
                            fontSize = 15.sp
                        ),
                        cursorBrush = SolidColor(MicrobondActiveGreen),
                        maxLines = 6,
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (inputValue.isEmpty()) {
                                    Text(
                                        text = when {
                                            isInPaymentMode && paymentAmountChip == null -> "Type amount..."
                                            isInPaymentMode && paymentAmountChip != null -> "Type number or press Send..."
                                            else -> "Ask the group..."
                                        },
                                        color = MicrobondInactiveGray,
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach",
                    tint = MicrobondInactiveGray,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onAttachClick() 
                        }
                )

                if (inputValue.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = MicrobondInactiveGray,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (isRecording || inputValue.isNotBlank() || paymentAmountChip != null) {
                        MicrobondActiveGreen
                    } else {
                        MicButtonOrange
                    }
                )
                .then(
                    if (inputValue.isBlank() && paymentAmountChip == null) {
                        Modifier.pointerInput(Unit) {
                            var cancelled = false
                            var locked = false
                            var dragX = 0f
                            var dragY = 0f
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    cancelled = false
                                    locked = false
                                    dragX = 0f
                                    dragY = 0f
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onRecordingStart()
                                    onRecordingStateChange(true)
                                },
                                onDragEnd = {
                                    onRecordingStateChange(false)
                                    if (!cancelled && !locked) onRecordingFinish(false)
                                },
                                onDragCancel = {
                                    onRecordingStateChange(false)
                                    if (!cancelled && !locked) onRecordingFinish(true)
                                },
                                onDrag = { change, dragAmount ->
                                    dragX += dragAmount.x
                                    dragY += dragAmount.y
                                    if (dragX < -100.dp.toPx() && !cancelled && !locked) {
                                        cancelled = true
                                        onRecordingFinish(true)
                                    } else if (dragY < -100.dp.toPx() && !cancelled && !locked) {
                                        locked = true
                                        onRecordingLock()
                                    }
                                    change.consume()
                                }
                            )
                        }
                    } else Modifier
                )
                .then(
                    if (isRecordingLocked) {
                        Modifier.clickable { onRecordingFinish(false) }
                    } else if (!isRecording && (inputValue.isNotBlank() || paymentAmountChip != null)) {
                        Modifier.clickable {
                            onSubmitClick()
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRecording) {
                    Icons.AutoMirrored.Filled.Send
                } else if (inputValue.isNotBlank() || paymentAmountChip != null) {
                    Icons.AutoMirrored.Filled.Send
                } else {
                    Icons.Default.Mic
                },
                contentDescription = when {
                    isRecording -> "Send voice message"
                    inputValue.isNotBlank() || paymentAmountChip != null -> "Send"
                    else -> "Hold to record voice message"
                },
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
