package com.microbond.app.feature_you.ui.account

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import android.widget.Toast
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.numerickeypad.NumericKeypad
import com.microbond.app.core.components.padlockicon.MicrobondPadlockIcon
import com.microbond.app.core.components.pindots.PinDotsRow
import com.microbond.app.core.network.ChangeAppPinRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val PIN_LENGTH = 4

private fun isSimpleSequence(pin: String): Boolean {
    if (pin.length != PIN_LENGTH) return false
    if (pin.all { it == pin[0] }) return true
    val isAscending = pin.zipWithNext().all { (a, b) -> b - a == 1 }
    val isDescending = pin.zipWithNext().all { (a, b) -> a - b == 1 }
    return isAscending || isDescending
}

private enum class ChangePinPhase {
    ENTER_OLD_PIN,
    ENTER_NEW_PIN,
    CONFIRM_NEW_PIN
}

/**
 * Screen to allow an authenticated user to change their 6-digit security PIN.
 * Flow:
 * 1. ENTER_OLD_PIN: User inputs current PIN.
 * 2. ENTER_NEW_PIN: User enters new PIN.
 * 3. CONFIRM_NEW_PIN: User confirms new PIN.
 * Calls PUT /pin/change with ChangeAppPinRequest(old_pin, new_pin).
 */
@Composable
fun ChangePinScreen(
    onBack: () -> Unit,
    onForgotPinClick: () -> Unit = {}
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

    var phase by remember { mutableStateOf(ChangePinPhase.ENTER_OLD_PIN) }
    var oldPin by remember { mutableStateOf("") }
    var newPinDraft by remember { mutableStateOf("") }
    var currentPinInput by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 60, 50, 60, 50), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 50, 60, 50), -1)
            }
        } catch (_: Exception) {}
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) {}
    }

    fun triggerShake(msg: String) {
        errorMessage = msg
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

    BackHandler {
        when (phase) {
            ChangePinPhase.ENTER_OLD_PIN -> onBack()
            ChangePinPhase.ENTER_NEW_PIN -> {
                phase = ChangePinPhase.ENTER_OLD_PIN
                currentPinInput = oldPin
                errorMessage = null
            }
            ChangePinPhase.CONFIRM_NEW_PIN -> {
                phase = ChangePinPhase.ENTER_NEW_PIN
                currentPinInput = newPinDraft
                errorMessage = null
            }
        }
    }

    fun submitPinChange(confirmedNewPin: String) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.changePin(
                    ChangeAppPinRequest(
                        oldPin = oldPin,
                        newPin = confirmedNewPin
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(context, "App PIN has been changed successfully.", Toast.LENGTH_SHORT).show()
                    onBack()
                } else {
                    val rawError = response.errorBody()?.string()
                    val parsedMsg = if (!rawError.isNullOrBlank()) {
                        try {
                            val json = JSONObject(rawError)
                            json.optString("detail", json.optString("message", rawError))
                        } catch (_: Exception) {
                            rawError
                        }
                    } else {
                        when (response.code()) {
                            400 -> "The new PIN cannot be the same as the old PIN."
                            401 -> "The old PIN is incorrect."
                            429 -> "Too many attempts. Please try again in 1 minute."
                            else -> "Failed to change PIN. Please check and try again."
                        }
                    }

                    if (response.code() == 401) {
                        phase = ChangePinPhase.ENTER_OLD_PIN
                        oldPin = ""
                        newPinDraft = ""
                    } else if (response.code() == 400) {
                        phase = ChangePinPhase.ENTER_NEW_PIN
                        newPinDraft = ""
                    }
                    triggerShake(parsedMsg)
                }
            } catch (e: Exception) {
                triggerShake("Network error: ${e.localizedMessage ?: "Unable to change PIN"}")
            } finally {
                isLoading = false
            }
        }
    }

    fun handleDigitPress(digit: String) {
        if (isLoading || isErrorShaking || currentPinInput.length >= PIN_LENGTH) return
        triggerKeyHaptic()
        errorMessage = null

        val updated = currentPinInput + digit
        currentPinInput = updated

        if (updated.length == PIN_LENGTH) {
            when (phase) {
                ChangePinPhase.ENTER_OLD_PIN -> {
                    oldPin = updated
                    currentPinInput = ""
                    phase = ChangePinPhase.ENTER_NEW_PIN
                }
                ChangePinPhase.ENTER_NEW_PIN -> {
                    if (isSimpleSequence(updated)) {
                        triggerShake("PIN is too simple. Avoid sequences like 1234 or 0000.")
                    } else if (updated == oldPin) {
                        triggerShake("The new PIN cannot be the same as the old PIN.")
                    } else {
                        newPinDraft = updated
                        currentPinInput = ""
                        phase = ChangePinPhase.CONFIRM_NEW_PIN
                    }
                }
                ChangePinPhase.CONFIRM_NEW_PIN -> {
                    if (updated == newPinDraft) {
                        submitPinChange(updated)
                    } else {
                        triggerShake("New PINs do not match. Try again.")
                    }
                }
            }
        }
    }

    fun handleBackspace() {
        if (isLoading || isErrorShaking || currentPinInput.isEmpty()) return
        triggerKeyHaptic()
        errorMessage = null
        currentPinInput = currentPinInput.dropLast(1)
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Change PIN",
                showBackButton = true,
                showMoreIcon = false,
                onBackClick = {
                    when (phase) {
                        ChangePinPhase.ENTER_OLD_PIN -> onBack()
                        ChangePinPhase.ENTER_NEW_PIN -> {
                            phase = ChangePinPhase.ENTER_OLD_PIN
                            currentPinInput = oldPin
                            errorMessage = null
                        }
                        ChangePinPhase.CONFIRM_NEW_PIN -> {
                            phase = ChangePinPhase.ENTER_NEW_PIN
                            currentPinInput = newPinDraft
                            errorMessage = null
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            MicrobondPadlockIcon(modifier = Modifier.size(68.dp))

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = when (phase) {
                    ChangePinPhase.ENTER_OLD_PIN -> "Enter current PIN"
                    ChangePinPhase.ENTER_NEW_PIN -> "Create new PIN"
                    ChangePinPhase.CONFIRM_NEW_PIN -> "Confirm new PIN"
                },
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (phase) {
                    ChangePinPhase.ENTER_OLD_PIN -> "Enter your current 4-digit security PIN to authorize this change"
                    ChangePinPhase.ENTER_NEW_PIN -> "Enter a new 4-digit security PIN for your account"
                    ChangePinPhase.CONFIRM_NEW_PIN -> "Re-enter your new 4-digit PIN to confirm"
                },
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PinDotsRow(
                totalDigits = PIN_LENGTH,
                enteredDigits = currentPinInput.length,
                isError = isErrorShaking || !errorMessage.isNullOrBlank(),
                shakeOffset = shakeAnim.value
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = errorMessage!!,
                    style = TextStyle(
                        color = MicrobondErrorColor,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(14.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MicrobondActiveGreen,
                    strokeWidth = 2.5.dp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NumericKeypad(
                onDigitClick = ::handleDigitPress,
                onBackspaceClick = ::handleBackspace,
                isBackspaceEnabled = currentPinInput.isNotEmpty() && !isLoading && !isErrorShaking
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (phase == ChangePinPhase.ENTER_OLD_PIN) {
                Text(
                    text = "Forgot PIN?",
                    style = TextStyle(
                        color = MicrobondActiveGreen,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onForgotPinClick
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141A)
@Composable
private fun ChangePinScreenPreview() {
    ChangePinScreen(onBack = {})
}
