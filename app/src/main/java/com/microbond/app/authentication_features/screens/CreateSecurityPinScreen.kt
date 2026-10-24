package com.microbond.app.authentication_features.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.numerickeypad.NumericKeypad
import com.microbond.app.core.components.padlockicon.MicrobondPadlockIcon
import com.microbond.app.core.components.pindots.PinDotsRow
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch

private const val PIN_LENGTH = 4

private fun isSimpleSequence(pin: String): Boolean {
    if (pin.length != PIN_LENGTH) return false
    if (pin.all { it == pin[0] }) return true
    val isAscending = pin.zipWithNext().all { (a, b) -> b - a == 1 }
    val isDescending = pin.zipWithNext().all { (a, b) -> a - b == 1 }
    return isAscending || isDescending
}

/**
 * Screen state representation for the PIN creation flow.
 */
enum class PinSetupStep {
    CREATE,
    CONFIRM
}

/**
 * Microbond "Create a security PIN" Screen.
 *
 * Features:
 * - Tactile vibration feedback on every numeric key press & backspace.
 * - Double-buzz haptic pattern and horizontal error shake animation on PIN mismatch.
 * - Fluid scale & spring animations for dots, padlock icon, and step transitions.
 * - MicrobondTopAppBar for clean navigation.
 */
@Composable
fun CreateSecurityPinScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isResetFlow: Boolean = false,
    onPinCreated: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onForgotPinClick: () -> Unit = {},
    onNavigateToUnlock: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Initialize system vibrator across Android SDK versions
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var step by remember { mutableStateOf(PinSetupStep.CREATE) }
    var initialPin by remember { mutableStateOf("") }
    var currentPinInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    // Error shake animation controller
    val shakeAnim = remember { Animatable(0f) }
    var isErrorShaking by remember { mutableStateOf(false) }

    // Tactile vibration on every key press
    fun triggerKeyHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(32, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(32)
            }
        } catch (_: Exception) {}
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) {}
    }

    // Double-pulse error vibration on mismatch
    fun triggerErrorHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 50, 60, 50, 60, 50),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 50, 60, 50), -1)
            }
        } catch (_: Exception) {}
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) {}
    }

    // Intercept back: if in CONFIRM step, return to CREATE step
    BackHandler {
        if (step == PinSetupStep.CONFIRM) {
            step = PinSetupStep.CREATE
            currentPinInput = initialPin
            localError = null
        } else {
            onNavigateBack()
        }
    }

    val displayError = localError ?: errorMessage
    val isExistingPinError = displayError?.contains("already have a PIN", ignoreCase = true) == true

    fun handleDigitPress(digit: String) {
        if (isLoading || isErrorShaking || currentPinInput.length >= PIN_LENGTH) return
        triggerKeyHaptic()
        localError = null

        val updated = currentPinInput + digit
        currentPinInput = updated

        if (updated.length == PIN_LENGTH) {
            when (step) {
                PinSetupStep.CREATE -> {
                    if (isSimpleSequence(updated)) {
                        localError = "PIN is too simple. Avoid sequences like 1234 or 0000."
                        isErrorShaking = true
                        triggerErrorHaptic()
                        coroutineScope.launch {
                            shakeAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = keyframes {
                                    durationMillis = 420
                                    0f at 0
                                    (-20f) at 50
                                    20f at 100
                                    (-15f) at 160
                                    15f at 220
                                    (-10f) at 280
                                    10f at 340
                                    0f at 420
                                }
                            )
                            isErrorShaking = false
                            currentPinInput = ""
                        }
                    } else {
                        // PIN created: store and advance to confirmation
                        initialPin = updated
                        currentPinInput = ""
                        step = PinSetupStep.CONFIRM
                    }
                }
                PinSetupStep.CONFIRM -> {
                    if (updated == initialPin) {
                        // Success: both entries match
                        onPinCreated(updated)
                    } else {
                        // Mismatch: trigger error haptic and shake animation
                        localError = "PINs do not match. Try again."
                        isErrorShaking = true
                        triggerErrorHaptic()
                        coroutineScope.launch {
                            shakeAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = keyframes {
                                    durationMillis = 420
                                    0f at 0
                                    (-20f) at 50
                                    20f at 100
                                    (-15f) at 160
                                    15f at 220
                                    (-10f) at 280
                                    10f at 340
                                    0f at 420
                                }
                            )
                            isErrorShaking = false
                            currentPinInput = ""
                        }
                    }
                }
            }
        }
    }

    fun handleBackspace() {
        if (isLoading || isErrorShaking || currentPinInput.isEmpty()) return
        triggerKeyHaptic()
        localError = null
        currentPinInput = currentPinInput.dropLast(1)
    }

    // Subtle bounce on padlock icon as user types
    val padlockScale by animateFloatAsState(
        targetValue = if (currentPinInput.isNotEmpty()) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "PadlockScale"
    )

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "",
                showBackButton = true,
                showMoreIcon = false,
                onBackClick = {
                    if (step == PinSetupStep.CONFIRM) {
                        step = PinSetupStep.CREATE
                        currentPinInput = initialPin
                        localError = null
                    } else {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Animated Padlock Icon with keyhole cutout matching design
            MicrobondPadlockIcon(
                modifier = Modifier
                    .size(width = 54.dp, height = 66.dp)
                    .graphicsLayer {
                        scaleX = padlockScale
                        scaleY = padlockScale
                    },
                tint = if (isErrorShaking) MicrobondErrorColor else MicrobondActiveGreen,
                cutoutColor = MicrobondDarkBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Title and Subtitle with fluid slide and fade transitions
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState == PinSetupStep.CONFIRM) {
                        (slideInHorizontally { width -> width / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> -width / 3 } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> width / 3 } + fadeOut())
                    }
                },
                label = "PinStepTitle"
            ) { currentStep ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = if (currentStep == PinSetupStep.CREATE) {
                            if (isResetFlow) "Set your new 4-digit PIN" else "Create your 4-digit security PIN"
                        } else {
                            if (isResetFlow) "Confirm your new PIN" else "Confirm your PIN"
                        },
                        style = TextStyle(
                            color = MicrobondTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (currentStep == PinSetupStep.CREATE) {
                            if (isResetFlow) "Choose a new 4-digit PIN to protect your account" else "Use this PIN to authorize transactions and unlock the app"
                        } else {
                            if (isResetFlow) "Re-enter your new 4-digit PIN to finish setup" else "Re-enter your 4-digit PIN to confirm setup"
                        },
                        style = TextStyle(
                            color = MicrobondInactiveGray,
                            fontSize = 14.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 4-Digit Indicator Dots with spring pop and shake animation
            PinDotsRow(
                totalDigits = PIN_LENGTH,
                enteredDigits = currentPinInput.length,
                isError = isErrorShaking,
                shakeOffset = shakeAnim.value,
                dotSize = 16.dp,
                spacing = 20.dp
            )

            if (!displayError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                if (isExistingPinError) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "This account already has a security PIN set.\nSince you don't know it, you can reset it via SMS code.",
                            style = TextStyle(
                                color = MicrobondErrorColor,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Primary Reset Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MicrobondActiveGreen)
                                    .clickable(onClick = onForgotPinClick)
                                    .padding(horizontal = 16.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    text = "Reset Forgotten PIN",
                                    style = TextStyle(
                                        color = MicrobondDarkBackground,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // Secondary Unlock Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, MicrobondActiveGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .clickable(onClick = onNavigateToUnlock)
                                    .padding(horizontal = 16.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    text = "Unlock with PIN",
                                    style = TextStyle(
                                        color = MicrobondActiveGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = displayError,
                        style = TextStyle(
                            color = MicrobondErrorColor,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MicrobondActiveGreen,
                    strokeWidth = 2.5.dp
                )
            }

            // Anchors keypad comfortably down in the natural thumb zone
            Spacer(modifier = Modifier.weight(1f))

            // Numeric Keypad Grid (3x3 numbers + row with 0 and backspace)
            NumericKeypad(
                onDigitClick = ::handleDigitPress,
                onBackspaceClick = ::handleBackspace,
                isBackspaceEnabled = currentPinInput.isNotEmpty() && !isLoading && !isErrorShaking,
                keySpacing = 16.dp,
                buttonSize = 75.dp
            )

            if (!isResetFlow) {
                Spacer(modifier = Modifier.height(18.dp))

                // "Forgot PIN?" Link in signature green
                Text(
                    text = "Forgot PIN?",
                    style = TextStyle(
                        color = MicrobondActiveGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onForgotPinClick
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun CreateSecurityPinScreenPreview() {
    CreateSecurityPinScreen()
}
