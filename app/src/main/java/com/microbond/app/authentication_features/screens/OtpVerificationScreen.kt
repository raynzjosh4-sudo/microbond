package com.microbond.app.authentication_features.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.components.otpinput.OtpInputField
import com.microbond.app.core.components.otpinput.SmsAutoFillObserver
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue
import kotlinx.coroutines.delay

/**
 * Microbond OTP Verification Screen ("Verifying your number").
 * Features real-time SMS auto-detection, a 6-digit WhatsApp-style split OTP field,
 * dynamic status cards ("Requesting an SMS...", "Verifying code..."),
 * resend timer, and full zero-hardcoded color conformance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    isRequestingSms: Boolean = false,
    isVerifyingCode: Boolean = false,
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
    onVerifyOtp: (code: String) -> Unit = {},
    onResendOtp: () -> Unit = {},
    onWrongNumberClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    var otpValue by remember { mutableStateOf("") }
    var resendSeconds by remember { mutableIntStateOf(60) }
    var showResendSheet by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    BackHandler {
        if (showResendSheet) {
            showResendSheet = false
        } else {
            onNavigateBack()
        }
    }

    // Resend countdown timer
    LaunchedEffect(resendSeconds) {
        if (resendSeconds > 0) {
            delay(1000L)
            resendSeconds--
        }
    }

    // Auto-focus on entry once SMS request is completed
    LaunchedEffect(isRequestingSms) {
        if (!isRequestingSms) {
            delay(150L)
            try {
                focusRequester.requestFocus()
                keyboardController?.show()
            } catch (_: Exception) {
            }
        }
    }

    // Automatic SMS detection listener
    SmsAutoFillObserver(enabled = !isVerifyingCode) { incomingCode ->
        otpValue = incomingCode
        onVerifyOtp(incomingCode)
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MicrobondTextPrimary
                    )
                }

                MicrobondOverflowMenu(
                    actions = listOf(
                        MicrobondMenuAction(
                            title = "Help",
                            onClick = onHelpClick
                        )
                    )
                )
            }
        }
    ) { paddingValues ->
        val subtitleText = buildAnnotatedString {
            append("Waiting to automatically detect an SMS sent to ")
            withStyle(
                SpanStyle(
                    color = MicrobondTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(phoneNumber)
            }
            append(". ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "WRONG_NUMBER",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = microBondCheckMarkBlue,
                            fontWeight = FontWeight.Medium
                        )
                    ),
                    linkInteractionListener = { onWrongNumberClick() }
                )
            ) {
                append("Wrong number?")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Screen Title
            Text(
                text = "Verifying your number",
                style = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle with formatted number and clickable "Wrong number?"
            Text(
                text = subtitleText,
                style = TextStyle(
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // "Didn't receive code?" Action
            Text(
                text = "Didn't receive code?",
                style = TextStyle(
                    color = MicrobondActiveGreen,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clickable { showResendSheet = true }
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Status Card: "Requesting an SMS..." / "Verifying code..."
            AnimatedVisibility(
                visible = isRequestingSms || isVerifyingCode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val statusText = if (isRequestingSms) "Requesting an SMS..." else "Verifying code..."

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(
                            color = MicrobondOverlayWidgetDark,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = MicrobondActiveGreen,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(18.dp))

                        Text(
                            text = statusText,
                            style = TextStyle(
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }

            // OTP 6-digit Input Field
            if (!isRequestingSms) {
                Spacer(modifier = Modifier.height(20.dp))

                OtpInputField(
                    otpValue = otpValue,
                    onOtpChange = { newOtp ->
                        otpValue = newOtp
                        if (!errorMessage.isNullOrBlank()) {
                            onClearError()
                        }
                    },
                    enabled = !isVerifyingCode,
                    focusRequester = focusRequester,
                    isError = !errorMessage.isNullOrBlank(),
                    onOtpComplete = { completedCode ->
                        onVerifyOtp(completedCode)
                    }
                )

                // Error message display if verification failed
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = errorMessage,
                        style = TextStyle(
                            color = MicrobondErrorColor,
                            fontSize = 13.5.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                // Resend Timer indicator
                Spacer(modifier = Modifier.height(28.dp))
                if (resendSeconds > 0) {
                    val minutes = resendSeconds / 60
                    val secs = resendSeconds % 60
                    val formattedTime = String.format("%02d:%02d", minutes, secs)
                    Text(
                        text = "Resend SMS in $formattedTime",
                        style = TextStyle(
                            color = MicrobondInactiveGray,
                            fontSize = 13.sp
                        )
                    )
                } else {
                    Text(
                        text = "Resend SMS",
                        style = TextStyle(
                            color = MicrobondActiveGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .clickable {
                                resendSeconds = 60
                                onResendOtp()
                            }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    // "Didn't receive code?" Bottom Sheet
    if (showResendSheet) {
        ModalBottomSheet(
            onDismissRequest = { showResendSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MicrobondOverlayWidgetDark,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Didn't receive code?",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)

                // Option: Resend SMS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = resendSeconds == 0) {
                            showResendSheet = false
                            resendSeconds = 60
                            onResendOtp()
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = if (resendSeconds == 0) MicrobondActiveGreen else MicrobondInactiveGray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Resend SMS",
                            style = TextStyle(
                                color = if (resendSeconds == 0) MicrobondTextPrimary else MicrobondInactiveGray,
                                fontSize = 15.sp
                            )
                        )
                    }

                    if (resendSeconds > 0) {
                        val minutes = resendSeconds / 60
                        val secs = resendSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, secs),
                            style = TextStyle(
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)

                // Option: Edit phone number
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showResendSheet = false
                            onWrongNumberClick()
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit phone number",
                        style = TextStyle(
                            color = MicrobondActiveGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun OtpVerificationScreenPreview() {
    OtpVerificationScreen(phoneNumber = "+256 704 903741")
}
