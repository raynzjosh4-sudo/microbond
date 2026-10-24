package com.microbond.app.authentication_features.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.otpinput.OtpInputField
import com.microbond.app.core.components.otpinput.SmsAutoFillObserver
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.RequestForgotPinOTPRequest
import com.microbond.app.core.network.VerifyForgotPinOTPRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ForgotPinScreenPhase {
    REQUEST_CODE,
    VERIFY_CODE
}

/**
 * Full screen for recovering a forgotten PIN via registered phone OTP.
 *
 * Flow:
 * 1. REQUEST_CODE: Confirms user's registered phone and dispatches an OTP via POST /pin/forgot/request-otp.
 * 2. VERIFY_CODE: User enters the 6-digit code via OtpInputField (or SMS auto-detection).
 * 3. POST /pin/forgot/verify-otp validates the code and yields a recovery token.
 * 4. Passes recovery token back to set up a brand new 6-digit PIN.
 */
@Composable
fun ForgotPinRecoveryScreen(
    onNavigateBack: () -> Unit,
    onPinResetReady: (recoveryToken: String?) -> Unit,
    initialPhoneNumber: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val phoneFromSession = AuthSessionManager.phoneNumber.value.orEmpty()
    val registeredPhone = initialPhoneNumber.ifBlank { phoneFromSession }

    var phase by remember { mutableStateOf(ForgotPinScreenPhase.REQUEST_CODE) }
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendSeconds by remember { mutableIntStateOf(60) }

    // Intercept back navigation when on verification step to go back to request step
    BackHandler {
        if (phase == ForgotPinScreenPhase.VERIFY_CODE) {
            phase = ForgotPinScreenPhase.REQUEST_CODE
            errorMessage = null
            otpCode = ""
        } else {
            onNavigateBack()
        }
    }

    // Resend countdown timer
    LaunchedEffect(phase) {
        if (phase == ForgotPinScreenPhase.VERIFY_CODE) {
            resendSeconds = 60
            while (resendSeconds > 0) {
                delay(1000)
                resendSeconds--
            }
        }
    }

    // Auto-focus OTP field when entering verification phase
    LaunchedEffect(phase) {
        if (phase == ForgotPinScreenPhase.VERIFY_CODE) {
            delay(200)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {
            }
        }
    }

    fun sendOtp() {
        if (registeredPhone.isBlank()) {
            errorMessage = "No registered phone number found. Please log in again."
            return
        }
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.requestForgotPinOtp(
                    RequestForgotPinOTPRequest(phoneNumber = registeredPhone)
                )
                if (response.isSuccessful) {
                    phase = ForgotPinScreenPhase.VERIFY_CODE
                } else {
                    val err = response.errorBody()?.string()
                    errorMessage = err ?: "Failed to dispatch reset code."
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage ?: "Unable to connect"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyOtp(code: String) {
        if (code.length != 6) return
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.verifyForgotPinOtp(
                    VerifyForgotPinOTPRequest(phoneNumber = registeredPhone, code = code)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.recoveryToken
                    onPinResetReady(token)
                } else {
                    errorMessage = "Invalid verification code. Please check and try again."
                }
            } catch (e: Exception) {
                errorMessage = "Verification error: ${e.localizedMessage ?: "Unable to verify"}"
            } finally {
                isLoading = false
            }
        }
    }

    // Automatic SMS detection listener while in VERIFY_CODE phase
    if (phase == ForgotPinScreenPhase.VERIFY_CODE) {
        SmsAutoFillObserver(enabled = !isLoading) { incomingCode ->
            if (incomingCode.length == 6) {
                otpCode = incomingCode
                verifyOtp(incomingCode)
            }
        }
    }

    fun maskPhoneNumber(phone: String): String {
        if (phone.isBlank()) return "Mobile Number Not Found"
        val digits = phone.filter { it.isDigit() }
        if (digits.length <= 3) return phone
        val last3 = digits.takeLast(3)
        val prefix = if (phone.startsWith("+") && phone.length >= 4) {
            phone.substring(0, 4)
        } else {
            ""
        }
        return if (prefix.isNotEmpty()) {
            "$prefix ••• ••• $last3"
        } else {
            "•••• ••• $last3"
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Reset PIN",
                showBackButton = true,
                showMoreIcon = false,
                onBackClick = {
                    if (phase == ForgotPinScreenPhase.VERIFY_CODE) {
                        phase = ForgotPinScreenPhase.REQUEST_CODE
                        errorMessage = null
                        otpCode = ""
                    } else {
                        onNavigateBack()
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Lock Reset Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MicrobondActiveGreen.copy(alpha = 0.12f))
                    .border(1.5.dp, MicrobondActiveGreen.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LockReset,
                    contentDescription = "Reset PIN",
                    tint = MicrobondActiveGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (phase == ForgotPinScreenPhase.REQUEST_CODE) {
                    "Reset your security PIN"
                } else {
                    "Enter verification code"
                },
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedContent(
                targetState = phase,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "ForgotPinPhaseContent"
            ) { currentPhase ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentPhase == ForgotPinScreenPhase.REQUEST_CODE) {
                        Text(
                            text = "To protect your funds and financial data, we will send an SMS verification code to your registered mobile number.",
                            style = TextStyle(
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Registered Phone Display Card (Masked with only last 3 figures)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MicrobondOverlayWidgetDark)
                                .border(1.dp, MicrobondActiveGreen.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MicrobondDarkBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PhoneIphone,
                                        contentDescription = "Registered Device",
                                        tint = MicrobondActiveGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .padding(start = 14.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "Registered Mobile Number",
                                        style = TextStyle(
                                            color = MicrobondInactiveGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = maskPhoneNumber(registeredPhone),
                                        style = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 16.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Security explanation hint
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = "Security Note",
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Only authorized device owners can reset their security PIN.",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }

                        if (!errorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage!!,
                                style = TextStyle(
                                    color = MicrobondErrorColor,
                                    fontSize = 13.5.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        // Pushes button all the way down near the phone navigation buttons
                        Spacer(modifier = Modifier.weight(1f))

                        ReusableMicroButtonBig(
                            text = "Send SMS Code",
                            onClick = ::sendOtp,
                            isLoading = isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    } else {
                        // VERIFY_CODE phase
                        Text(
                            text = if (registeredPhone.isNotBlank()) {
                                "Enter the 6-digit code sent to ${maskPhoneNumber(registeredPhone)}"
                            } else {
                                "Enter the 6-digit code sent to your phone"
                            },
                            style = TextStyle(
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // OTP 6-digit split input field
                        OtpInputField(
                            otpValue = otpCode,
                            onOtpChange = { input ->
                                if (input.length <= 6) {
                                    otpCode = input
                                    if (input.length == 6) {
                                        verifyOtp(input)
                                    }
                                }
                            },
                            focusRequester = focusRequester,
                            isError = !errorMessage.isNullOrBlank(),
                            onOtpComplete = ::verifyOtp
                        )

                        if (!errorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = errorMessage!!,
                                style = TextStyle(
                                    color = MicrobondErrorColor,
                                    fontSize = 13.5.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Resend SMS code prompt
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (resendSeconds > 0) {
                                Text(
                                    text = "Resend code in ${resendSeconds}s",
                                    style = TextStyle(
                                        color = MicrobondInactiveGray,
                                        fontSize = 13.5.sp
                                    )
                                )
                            } else {
                                Text(
                                    text = "Resend SMS Code",
                                    style = TextStyle(
                                        color = microBondCheckMarkBlue,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.clickable {
                                        sendOtp()
                                    }
                                )
                            }
                        }

                        // Pushes button all the way down near the phone navigation buttons
                        Spacer(modifier = Modifier.weight(1f))

                        ReusableMicroButtonBig(
                            text = "Verify & Reset PIN",
                            onClick = { verifyOtp(otpCode) },
                            enabled = otpCode.length == 6 && !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun ForgotPinRecoveryScreenPreview() {
    ForgotPinRecoveryScreen(
        onNavigateBack = {},
        onPinResetReady = {}
    )
}
