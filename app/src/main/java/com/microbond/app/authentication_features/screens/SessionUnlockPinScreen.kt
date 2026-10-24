package com.microbond.app.authentication_features.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

/**
 * Microbond "Session Unlock PIN" Screen.
 *
 * Used for:
 * - Returning users unlocking their existing session.
 * - App coming back from background or idle session timeout.
 * - Step-up authentication before sensitive financial transactions.
 *
 * Verifies against POST /pin/verify.
 */
@Composable
fun SessionUnlockPinScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isBiometricsEnabled: Boolean = false,
    onPinEntered: (String) -> Unit = {},
    onBiometricClick: () -> Unit = {},
    onForgotPinClick: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var currentPinInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val shakeAnim = remember { Animatable(0f) }
    var isErrorShaking by remember { mutableStateOf(false) }

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

    // When an error message arrives from outside (e.g. invalid PIN API response), shake
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            triggerErrorHaptic()
            isErrorShaking = true
            currentPinInput = ""
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
        }
    }

    BackHandler(enabled = onNavigateBack != null) {
        onNavigateBack?.invoke()
    }

    fun handleDigitPress(digit: String) {
        if (isLoading || isErrorShaking || currentPinInput.length >= PIN_LENGTH) return
        triggerKeyHaptic()
        localError = null

        val updated = currentPinInput + digit
        currentPinInput = updated

        if (updated.length == PIN_LENGTH) {
            onPinEntered(updated)
        }
    }

    fun handleBackspace() {
        if (isLoading || isErrorShaking || currentPinInput.isEmpty()) return
        triggerKeyHaptic()
        localError = null
        currentPinInput = currentPinInput.dropLast(1)
    }

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
                showBackButton = onNavigateBack != null,
                showMoreIcon = false,
                onBackClick = { onNavigateBack?.invoke() }
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

            // Animated Padlock Icon
            MicrobondPadlockIcon(
                modifier = Modifier
                    .size(width = 54.dp, height = 66.dp)
                    .graphicsLayer {
                        scaleX = padlockScale
                        scaleY = padlockScale
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Enter your security PIN",
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your 4-digit PIN to unlock your account session",
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // 4 PIN Dots Row with spring pop and horizontal shake on error
            PinDotsRow(
                totalDigits = PIN_LENGTH,
                enteredDigits = currentPinInput.length,
                isError = isErrorShaking || !errorMessage.isNullOrBlank(),
                shakeOffset = shakeAnim.value,
                dotSize = 16.dp,
                spacing = 20.dp
            )

            val activeError = localError ?: errorMessage
            if (!activeError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = activeError,
                    style = TextStyle(
                        color = MicrobondErrorColor,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
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

            // Numeric Keypad
            NumericKeypad(
                onDigitClick = ::handleDigitPress,
                onBackspaceClick = ::handleBackspace,
                isBackspaceEnabled = currentPinInput.isNotEmpty() && !isLoading && !isErrorShaking,
                keySpacing = 16.dp,
                buttonSize = 75.dp,
                leftAction = if (isBiometricsEnabled) {
                    {
                        IconButton(onClick = onBiometricClick) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Unlock with Biometrics",
                                tint = MicrobondActiveGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(18.dp))

            // "Forgot PIN?" Button
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
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun SessionUnlockPinScreenPreview() {
    SessionUnlockPinScreen()
}
