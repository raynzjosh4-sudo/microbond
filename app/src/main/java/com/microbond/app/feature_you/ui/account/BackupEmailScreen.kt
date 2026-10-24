package com.microbond.app.feature_you.ui.account

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.otpinput.OtpInputField
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.network.LinkEmailRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.VerifyEmailRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

private enum class BackupEmailStep {
    LOADING,
    HAS_EMAIL,
    ENTER_EMAIL,
    VERIFY_CODE
}

/**
 * Settings screen for adding, viewing, and changing the backup recovery email.
 * Supports:
 * 1. Initial GET /email/backup to detect if user has an existing recovery email.
 * 2. If user has NO email -> Shows Add recovery email form matching design screenshot.
 * 3. If user HAS email -> Shows verified email with badge and "Change" button.
 * 4. Changing or adding email -> POST /email/link and POST /email/verify.
 */
@Composable
fun BackupEmailScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var step by remember { mutableStateOf(BackupEmailStep.LOADING) }
    var hasExistingEmail by remember { mutableStateOf(false) }
    var currentEmail by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendSeconds by remember { mutableIntStateOf(60) }

    val openWebPage: (String) -> Unit = remember(context) {
        { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    val handleBack: () -> Unit = {
        when (step) {
            BackupEmailStep.LOADING -> onBack()
            BackupEmailStep.HAS_EMAIL -> onBack()
            BackupEmailStep.ENTER_EMAIL -> {
                if (hasExistingEmail) {
                    step = BackupEmailStep.HAS_EMAIL
                    errorMessage = null
                } else {
                    onBack()
                }
            }
            BackupEmailStep.VERIFY_CODE -> {
                step = BackupEmailStep.ENTER_EMAIL
                otpCode = ""
                errorMessage = null
            }
        }
    }

    BackHandler(onBack = handleBack)

    // Countdown timer for OTP resend
    LaunchedEffect(step) {
        if (step == BackupEmailStep.VERIFY_CODE) {
            resendSeconds = 60
            while (resendSeconds > 0) {
                delay(1000)
                resendSeconds--
            }
        }
    }

    // Initial check: GET /email/backup
    LaunchedEffect(Unit) {
        try {
            val response = NetworkClient.apiService.getBackupEmail()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data
                if (body?.hasBackupEmail == true && data != null && !data.email.isNullOrBlank()) {
                    currentEmail = data.email
                    isVerified = data.isVerified
                    hasExistingEmail = true
                    step = BackupEmailStep.HAS_EMAIL
                } else {
                    hasExistingEmail = false
                    currentEmail = ""
                    step = BackupEmailStep.ENTER_EMAIL
                }
            } else {
                hasExistingEmail = false
                step = BackupEmailStep.ENTER_EMAIL
            }
        } catch (_: Exception) {
            hasExistingEmail = false
            step = BackupEmailStep.ENTER_EMAIL
        }
    }

    // Auto-focus OTP input field when entering verify step
    LaunchedEffect(step) {
        if (step == BackupEmailStep.VERIFY_CODE) {
            delay(200)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    fun submitLinkEmail() {
        val cleanEmail = emailInput.trim()
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorMessage = "Please enter a valid email address."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.linkBackupEmail(
                    LinkEmailRequest(email = cleanEmail)
                )

                if (response.isSuccessful) {
                    otpCode = ""
                    resendSeconds = 60
                    step = BackupEmailStep.VERIFY_CODE
                } else {
                    val rawError = response.errorBody()?.string()
                    errorMessage = if (!rawError.isNullOrBlank()) {
                        try {
                            JSONObject(rawError).optString("detail", rawError)
                        } catch (_: Exception) {
                            rawError
                        }
                    } else {
                        "Failed to link email. Please try again."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage ?: "Unable to connect"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitVerifyEmail(code: String) {
        if (code.length != 6) return
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.verifyBackupEmail(
                    VerifyEmailRequest(code = code)
                )

                if (response.isSuccessful) {
                    currentEmail = emailInput.trim()
                    hasExistingEmail = true
                    isVerified = true
                    Toast.makeText(context, "Backup email verified successfully.", Toast.LENGTH_SHORT).show()
                    step = BackupEmailStep.HAS_EMAIL
                } else {
                    val rawError = response.errorBody()?.string()
                    errorMessage = if (!rawError.isNullOrBlank()) {
                        try {
                            JSONObject(rawError).optString("detail", "Invalid verification code. Please check your inbox and try again.")
                        } catch (_: Exception) {
                            "Invalid verification code. Please check your inbox and try again."
                        }
                    } else {
                        "Invalid verification code. Please check your inbox and try again."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Verification error: ${e.localizedMessage ?: "Unable to verify"}"
            } finally {
                isLoading = false
            }
        }
    }

    val annotatedDescription = remember {
        buildAnnotatedString {
            append("Add an email address to receive a security code if you lose access to your phone or PIN. ")
            pushStringAnnotation(tag = "URL", annotation = "https://microbond.app/help/recovery-email")
            withStyle(
                style = SpanStyle(
                    color = MicrobondActiveGreen,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Learn more")
            }
            pop()
        }
    }

    val topBarTitle = when (step) {
        BackupEmailStep.HAS_EMAIL -> "Email address"
        BackupEmailStep.ENTER_EMAIL -> if (hasExistingEmail) "Change recovery email" else "Add recovery email"
        BackupEmailStep.VERIFY_CODE -> "Verify email address"
        BackupEmailStep.LOADING -> "Recovery email"
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = topBarTitle,
                showBackButton = true,
                showMoreIcon = true,
                menuActions = listOf(
                    MicrobondMenuAction(
                        title = "Help",
                        onClick = { openWebPage("https://microbond.app/help/recovery-email") }
                    )
                ),
                onBackClick = handleBack
            )
        },
        bottomBar = {
            when (step) {
                BackupEmailStep.LOADING -> { /* No button during loading */ }

                BackupEmailStep.HAS_EMAIL -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        ReusableMicroButtonBig(
                            text = "Change",
                            onClick = {
                                emailInput = ""
                                errorMessage = null
                                step = BackupEmailStep.ENTER_EMAIL
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                BackupEmailStep.ENTER_EMAIL -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        ReusableMicroButtonBig(
                            text = "Send code",
                            onClick = ::submitLinkEmail,
                            enabled = emailInput.isNotBlank() && !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                BackupEmailStep.VERIFY_CODE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        ReusableMicroButtonBig(
                            text = "Verify",
                            onClick = { submitVerifyEmail(otpCode) },
                            enabled = otpCode.length == 6 && !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "BackupEmailStepTransition"
            ) { currentStep ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        BackupEmailStep.LOADING -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MicrobondActiveGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        BackupEmailStep.HAS_EMAIL -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            ) {
                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Your email address helps you access your account. It isn't visible to other users.",
                                    color = MicrobondInactiveGray,
                                    fontSize = 14.5.sp,
                                    lineHeight = 21.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Plain text email and verified status (no box, no mail icon, no inline change button)
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = currentEmail,
                                        color = MicrobondTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isVerified) {
                                            Icon(
                                                imageVector = Icons.Outlined.CheckCircle,
                                                contentDescription = "Verified badge",
                                                tint = MicrobondActiveGreen,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Verified",
                                                color = MicrobondActiveGreen,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        } else {
                                            Text(
                                                text = "Pending verification",
                                                color = Color(0xFFFFB300),
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Text(
                                    text = "If you lose your phone or forget your App PIN, we will send a security code to this email to help you recover your account.",
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        BackupEmailStep.ENTER_EMAIL -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Image(
                                    painter = painterResource(id = R.drawable.addrecoveryemail),
                                    contentDescription = "Add recovery email",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Protect your account",
                                    color = MicrobondTextPrimary,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ClickableText(
                                    text = annotatedDescription,
                                    style = TextStyle(
                                        color = MicrobondInactiveGray,
                                        fontSize = 14.5.sp,
                                        lineHeight = 22.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    onClick = { offset ->
                                        annotatedDescription.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                            .firstOrNull()?.let { annotation ->
                                                openWebPage(annotation.item)
                                            }
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(36.dp))

                                // Underlined single-line text field exactly matching screenshot
                                BasicTextField(
                                    value = emailInput,
                                    onValueChange = {
                                        emailInput = it
                                        if (errorMessage != null) errorMessage = null
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (emailInput.isNotBlank() && !isLoading) {
                                                submitLinkEmail()
                                            }
                                        }
                                    ),
                                    textStyle = TextStyle(
                                        color = MicrobondTextPrimary,
                                        fontSize = 16.sp
                                    ),
                                    cursorBrush = SolidColor(MicrobondActiveGreen),
                                    decorationBox = { innerTextField ->
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 8.dp)
                                            ) {
                                                if (emailInput.isEmpty()) {
                                                    Text(
                                                        text = "Email address",
                                                        style = TextStyle(
                                                            color = MicrobondInactiveGray.copy(alpha = 0.6f),
                                                            fontSize = 16.sp
                                                        )
                                                    )
                                                }
                                                innerTextField()
                                            }
                                            HorizontalDivider(
                                                modifier = Modifier.fillMaxWidth(),
                                                thickness = 1.dp,
                                                color = if (errorMessage != null) MicrobondErrorColor else MicrobondDividerDark
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "We'll send a verification code to this email.",
                                    style = TextStyle(
                                        color = MicrobondInactiveGray,
                                        fontSize = 13.5.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                )

                                if (!errorMessage.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = errorMessage!!,
                                        style = TextStyle(
                                            color = MicrobondErrorColor,
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Start
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        BackupEmailStep.VERIFY_CODE -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Image(
                                    painter = painterResource(id = R.drawable.addrecoveryemail),
                                    contentDescription = "Verify email",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Verify your email address",
                                    color = MicrobondTextPrimary,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Enter the 6-digit code sent to $emailInput",
                                    color = MicrobondInactiveGray,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                OtpInputField(
                                    otpValue = otpCode,
                                    onOtpChange = { input ->
                                        if (input.length <= 6) {
                                            otpCode = input
                                            if (input.length == 6) {
                                                submitVerifyEmail(input)
                                            }
                                        }
                                    },
                                    focusRequester = focusRequester,
                                    isError = !errorMessage.isNullOrBlank(),
                                    onOtpComplete = ::submitVerifyEmail
                                )

                                if (!errorMessage.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = errorMessage!!,
                                        style = TextStyle(
                                            color = MicrobondErrorColor,
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (resendSeconds > 0) {
                                        Text(
                                            text = "Resend code in ${resendSeconds}s",
                                            style = TextStyle(color = MicrobondInactiveGray, fontSize = 13.5.sp)
                                        )
                                    } else {
                                        Text(
                                            text = "Resend Email Code",
                                            style = TextStyle(
                                                color = MicrobondActiveGreen,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                textDecoration = TextDecoration.Underline
                                            ),
                                            modifier = Modifier.clickable { submitLinkEmail() }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun BackupEmailScreenPreview() {
    MicrobondTheme {
        BackupEmailScreen(onBack = {})
    }
}
