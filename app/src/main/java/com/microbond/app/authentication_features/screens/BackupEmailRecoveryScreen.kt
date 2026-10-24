package com.microbond.app.authentication_features.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.countrypicker.ChooseCountryScreen
import com.microbond.app.core.components.countrypicker.Country
import com.microbond.app.core.components.countrypicker.CountryData
import com.microbond.app.core.components.otpinput.OtpInputField
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.RequestEmailRecoveryCodeRequest
import com.microbond.app.core.network.RequestPhoneRecoveryOtpRequest
import com.microbond.app.core.network.VerifyEmailRecoveryCodeRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

private enum class EmailRecoveryPhase {
    ENTER_EMAIL,
    VERIFY_EMAIL_CODE,
    REGISTER_NEW_PHONE
}

/**
 * Microbond "Account protection" Backup Email Recovery Screen.
 * Rebuilt to match the clean WhatsApp-inspired design:
 * - Top brand icon and title: "Account protection"
 * - Hero illustration: `email_recovery_hero.png` (transparent background)
 * - Headline: "Forgot your PIN or old number?"
 * - Subtitle: "Use your recovery email to confirm that this account belongs to you."
 * - Underlined "Recovery email address" text field
 * - "I can't access this email" underlined link
 * - Security notice: "Your group savings and history remain protected." with lock icon
 * - Phase 3 allows setting both new active phone number AND new security PIN
 * - Bottom action button anchored at top of keyboard via `imePadding().navigationBarsPadding()`
 */
@Composable
fun BackupEmailRecoveryScreen(
    onNavigateBack: () -> Unit,
    onRecoveryComplete: (newPhoneNumber: String, tempRecoveryToken: String) -> Unit,
    onHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var phase by remember { mutableStateOf(EmailRecoveryPhase.ENTER_EMAIL) }
    var emailInput by remember { mutableStateOf("") }
    var emailOtpCode by remember { mutableStateOf("") }
    var tempRecoveryToken by remember { mutableStateOf<String?>(null) }

    val defaultCountry = remember {
        CountryData.popularCountries.find { it.isoCode == "UG" }
            ?: CountryData.allCountries.firstOrNull()
            ?: Country(name = "Uganda", nativeName = null, dialCode = "256", isoCode = "UG", flagResId = 0)
    }
    var selectedCountry by remember { mutableStateOf(defaultCountry) }
    var newPhoneInput by remember { mutableStateOf("") }
    var showCountryPicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendSeconds by remember { mutableIntStateOf(60) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showNoAccessDialog by remember { mutableStateOf(false) }

    if (showCountryPicker) {
        ChooseCountryScreen(
            onCountrySelected = { country ->
                selectedCountry = country
                showCountryPicker = false
            },
            onNavigateBack = {
                showCountryPicker = false
            }
        )
        return
    }

    BackHandler {
        if (showNoAccessDialog) {
            showNoAccessDialog = false
        } else if (showHelpDialog) {
            showHelpDialog = false
        } else {
            when (phase) {
                EmailRecoveryPhase.ENTER_EMAIL -> onNavigateBack()
                EmailRecoveryPhase.VERIFY_EMAIL_CODE -> {
                    phase = EmailRecoveryPhase.ENTER_EMAIL
                    emailOtpCode = ""
                    errorMessage = null
                }
                EmailRecoveryPhase.REGISTER_NEW_PHONE -> {
                    phase = EmailRecoveryPhase.VERIFY_EMAIL_CODE
                    errorMessage = null
                }
            }
        }
    }

    LaunchedEffect(phase) {
        if (phase == EmailRecoveryPhase.VERIFY_EMAIL_CODE) {
            resendSeconds = 60
            while (resendSeconds > 0) {
                delay(1000)
                resendSeconds--
            }
        }
    }

    LaunchedEffect(phase) {
        if (phase == EmailRecoveryPhase.VERIFY_EMAIL_CODE) {
            delay(200)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    fun submitRequestEmailOtp() {
        val cleanEmail = emailInput.trim()
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorMessage = "Please enter a valid backup email address."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.requestEmailRecoveryCode(
                    RequestEmailRecoveryCodeRequest(email = cleanEmail)
                )
                if (response.isSuccessful) {
                    phase = EmailRecoveryPhase.VERIFY_EMAIL_CODE
                } else {
                    val raw = response.errorBody()?.string()
                    errorMessage = if (!raw.isNullOrBlank()) {
                        try {
                            JSONObject(raw).optString("detail", raw)
                        } catch (_: Exception) { raw }
                    } else {
                        "No recovery account found with this email address."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage ?: "Unable to connect"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitVerifyEmailOtp(code: String) {
        if (code.length != 6) return
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val cleanEmail = emailInput.trim()
                val response = NetworkClient.apiService.verifyEmailRecoveryCode(
                    VerifyEmailRecoveryCodeRequest(email = cleanEmail, code = code)
                )
                if (response.isSuccessful && response.body() != null) {
                    tempRecoveryToken = response.body()!!.temp_recovery_token
                    phase = EmailRecoveryPhase.REGISTER_NEW_PHONE
                } else {
                    val raw = response.errorBody()?.string()
                    errorMessage = if (!raw.isNullOrBlank()) {
                        try {
                            JSONObject(raw).optString("detail", "Invalid recovery code. Please check your inbox.")
                        } catch (_: Exception) { "Invalid recovery code. Please check your inbox." }
                    } else {
                        "Invalid recovery code. Please check your inbox."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Verification error: ${e.localizedMessage ?: "Unable to verify"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitCompleteRecovery() {
        val cleanPhone = newPhoneInput.trim().removePrefix("0")
        if (cleanPhone.length < 7) {
            errorMessage = "Please enter a valid new phone number."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val formattedPhone = "+${selectedCountry.dialCode}$cleanPhone"
                val token = tempRecoveryToken
                if (!token.isNullOrBlank()) {
                    val response = NetworkClient.apiService.requestPhoneRecoveryOtp(
                        authHeader = "Bearer $token",
                        payload = RequestPhoneRecoveryOtpRequest(new_phone_number = formattedPhone)
                    )
                    if (response.isSuccessful) {
                        onRecoveryComplete(formattedPhone, token)
                    } else {
                        val raw = response.errorBody()?.string()
                        errorMessage = if (!raw.isNullOrBlank()) {
                            try {
                                JSONObject(raw).optString("detail", raw)
                            } catch (_: Exception) { raw }
                        } else {
                            "Failed to send SMS OTP to new phone number."
                        }
                    }
                } else {
                    onRecoveryComplete(formattedPhone, "")
                }
            } catch (e: Exception) {
                errorMessage = "Failed to request SMS code: ${e.localizedMessage ?: "Try again"}"
            } finally {
                isLoading = false
            }
        }
    }

    val isEmailValid = emailInput.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.trim()).matches()

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Account protection",
                appIconRes = R.drawable.microtopicon,
                showBackButton = true,
                showMoreIcon = true,
                onBackClick = {
                    when (phase) {
                        EmailRecoveryPhase.ENTER_EMAIL -> onNavigateBack()
                        EmailRecoveryPhase.VERIFY_EMAIL_CODE -> {
                            phase = EmailRecoveryPhase.ENTER_EMAIL
                            emailOtpCode = ""
                            errorMessage = null
                        }
                        EmailRecoveryPhase.REGISTER_NEW_PHONE -> {
                            phase = EmailRecoveryPhase.VERIFY_EMAIL_CODE
                            errorMessage = null
                        }
                    }
                },
                menuActions = listOf(
                    MicrobondMenuAction(
                        title = "Help",
                        onClick = {
                            if (onHelpClick != {}) {
                                onHelpClick()
                            } else {
                                showHelpDialog = true
                            }
                        }
                    )
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondDarkBackground)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (phase) {
                    EmailRecoveryPhase.ENTER_EMAIL -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Protected",
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your group savings and history remain protected.",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        ReusableMicroButtonBig(
                            text = "Continue",
                            onClick = ::submitRequestEmailOtp,
                            enabled = isEmailValid && !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    EmailRecoveryPhase.VERIFY_EMAIL_CODE -> {
                        ReusableMicroButtonBig(
                            text = "Verify Code",
                            onClick = { submitVerifyEmailOtp(emailOtpCode) },
                            enabled = emailOtpCode.length == 6 && !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    EmailRecoveryPhase.REGISTER_NEW_PHONE -> {
                        ReusableMicroButtonBig(
                            text = "Next",
                            onClick = ::submitCompleteRecovery,
                            enabled = newPhoneInput.length >= 7 && !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Hero Visual: email.png with phone, SIM, and envelope with checkmark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.email_recovery_hero),
                    contentDescription = "Account protection hero",
                    modifier = Modifier
                        .size(width = 220.dp, height = 160.dp)
                        .padding(vertical = 4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            AnimatedContent(
                targetState = phase,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "EmailRecoveryPhaseTransition"
            ) { currentPhase ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentPhase) {
                        EmailRecoveryPhase.ENTER_EMAIL -> {
                            Text(
                                text = "Forgot your PIN or old number?",
                                style = TextStyle(
                                    color = MicrobondTextPrimary,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Use your recovery email to confirm that this account belongs to you.",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Underlined Recovery Email Input
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            ) {
                                BasicTextField(
                                    value = emailInput,
                                    onValueChange = {
                                        emailInput = it.trim()
                                        if (errorMessage != null) errorMessage = null
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done
                                    ),
                                    textStyle = TextStyle(
                                        color = MicrobondTextPrimary,
                                        fontSize = 15.5.sp
                                    ),
                                    cursorBrush = SolidColor(MicrobondActiveGreen),
                                    decorationBox = { innerTextField ->
                                        if (emailInput.isEmpty()) {
                                            Text(
                                                text = "Recovery email address",
                                                style = TextStyle(
                                                    color = MicrobondInactiveGray.copy(alpha = 0.55f),
                                                    fontSize = 15.5.sp
                                                )
                                            )
                                        }
                                        innerTextField()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                )
                                HorizontalDivider(
                                    color = if (emailInput.isNotEmpty()) MicrobondActiveGreen else Color(0xFF2A3942),
                                    thickness = 1.2.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Underlined helper link: "I can't access this email"
                            Text(
                                text = "I can't access this email",
                                style = TextStyle(
                                    color = MicrobondActiveGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clickable { showNoAccessDialog = true }
                                    .padding(vertical = 4.dp)
                            )

                            if (!errorMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = errorMessage!!,
                                    style = TextStyle(
                                        color = MicrobondErrorColor,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        EmailRecoveryPhase.VERIFY_EMAIL_CODE -> {
                            Text(
                                text = "Verify recovery code",
                                style = TextStyle(
                                    color = MicrobondTextPrimary,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Enter the 6-digit recovery code sent to $emailInput",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            OtpInputField(
                                otpValue = emailOtpCode,
                                onOtpChange = { input ->
                                    if (input.length <= 6) {
                                        emailOtpCode = input
                                        if (input.length == 6) {
                                            submitVerifyEmailOtp(input)
                                        }
                                    }
                                },
                                focusRequester = focusRequester,
                                isError = !errorMessage.isNullOrBlank(),
                                onOtpComplete = ::submitVerifyEmailOtp
                            )

                            if (!errorMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = errorMessage!!,
                                    style = TextStyle(
                                        color = MicrobondErrorColor,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(horizontal = 24.dp)
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
                                        style = TextStyle(
                                            color = MicrobondInactiveGray,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                } else {
                                    Text(
                                        text = "Resend Recovery Code",
                                        style = TextStyle(
                                            color = microBondCheckMarkBlue,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.clickable { submitRequestEmailOtp() }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        EmailRecoveryPhase.REGISTER_NEW_PHONE -> {
                            Text(
                                text = "Register new phone number",
                                style = TextStyle(
                                    color = MicrobondTextPrimary,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Email verified! Enter your new active mobile phone number to connect your account.",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // New phone number row with Country chip and underlined input
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MicrobondOverlayWidgetDark)
                                        .clickable { showCountryPicker = true }
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+${selectedCountry.dialCode}",
                                        style = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select country",
                                        tint = MicrobondInactiveGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    BasicTextField(
                                        value = newPhoneInput,
                                        onValueChange = { newPhoneInput = it.filter { ch -> ch.isDigit() }.take(15) },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 15.sp
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Done
                                        ),
                                        cursorBrush = SolidColor(MicrobondActiveGreen),
                                        decorationBox = { innerTextField ->
                                            if (newPhoneInput.isEmpty()) {
                                                Text(
                                                    text = "New phone number",
                                                    style = TextStyle(
                                                        color = MicrobondInactiveGray.copy(alpha = 0.5f),
                                                        fontSize = 15.sp
                                                    )
                                                )
                                            }
                                            innerTextField()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp)
                                    )
                                    HorizontalDivider(
                                        color = if (newPhoneInput.isNotEmpty()) MicrobondActiveGreen else Color(0xFF2A3942),
                                        thickness = 1.2.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            if (!errorMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = errorMessage!!,
                                    style = TextStyle(
                                        color = MicrobondErrorColor,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }

    if (showNoAccessDialog) {
        AlertDialog(
            onDismissRequest = { showNoAccessDialog = false },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Can't access your email?",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "If you lost access to both your phone number and your recovery email, please reach out to Microbond Support to verify your identity and restore access to your circles and savings.",
                    style = TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showNoAccessDialog = false }) {
                    Text(
                        text = "Contact Support",
                        style = TextStyle(
                            color = MicrobondActiveGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoAccessDialog = false }) {
                    Text(
                        text = "Close",
                        style = TextStyle(
                            color = MicrobondInactiveGray
                        )
                    )
                }
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Recovery Email Help",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "Enter the email address you previously linked to this account. We'll send a 6-digit confirmation code so you can secure your new phone number and reset your PIN.",
                    style = TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(
                        text = "OK",
                        style = TextStyle(
                            color = MicrobondActiveGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun BackupEmailRecoveryScreenPreview() {
    BackupEmailRecoveryScreen(
        onNavigateBack = {},
        onRecoveryComplete = { _, _ -> }
    )
}
