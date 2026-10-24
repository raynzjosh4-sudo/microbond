package com.microbond.app.core.components.threadbubbles.threads

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThreadBubbleWrapper(
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    swipeToReplyEnabled: Boolean = true,
    onSwipeToReply: () -> Unit = {},
    highlightRequest: Int = 0,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(0f) }
    val replyHighlight = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current
    val hasVibrated = remember { mutableStateOf(false) }
    
    val maxDragPx = with(LocalDensity.current) { 60.dp.toPx() }
    val replyThresholdPx = with(LocalDensity.current) { 45.dp.toPx() }

    LaunchedEffect(highlightRequest) {
        if (highlightRequest > 0) {
            repeat(3) {
                replyHighlight.animateTo(
                    targetValue = 0.28f,
                    animationSpec = tween(durationMillis = 180)
                )
                replyHighlight.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 260)
                )
                if (it < 2) delay(80)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MicrobondActiveGreen.copy(
                    alpha = maxOf(if (isSelected) 0.2f else 0f, replyHighlight.value)
                )
            )
            .pointerInput(swipeToReplyEnabled) {
                if (swipeToReplyEnabled) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = dragOffset.value + dragAmount
                            if (newOffset > 0 && newOffset < maxDragPx) {
                                coroutineScope.launch {
                                    dragOffset.snapTo(newOffset)
                                }
                                if (newOffset > replyThresholdPx && !hasVibrated.value) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    hasVibrated.value = true
                                } else if (newOffset <= replyThresholdPx) {
                                    hasVibrated.value = false
                                }
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                if (dragOffset.value > replyThresholdPx) {
                                    onSwipeToReply()
                                }
                                dragOffset.animateTo(0f, animationSpec = tween(durationMillis = 200))
                                hasVibrated.value = false
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                dragOffset.animateTo(0f, animationSpec = tween(durationMillis = 200))
                                hasVibrated.value = false
                            }
                        }
                    )
                }
            }
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 1.dp)
    ) {
        // Reply Icon Background
        if (dragOffset.value > 0f) {
            val iconAlpha = (dragOffset.value / maxDragPx).coerceIn(0f, 1f)
            val iconSize = 28.dp
            
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(iconSize)
                    .alpha(iconAlpha)
                    .clip(CircleShape)
                    .background(Color(0xFF202C33)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Reply,
                    contentDescription = "Reply",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Box(modifier = Modifier.offset { IntOffset(dragOffset.value.roundToInt(), 0) }) {
            content()
        }
    }
}
