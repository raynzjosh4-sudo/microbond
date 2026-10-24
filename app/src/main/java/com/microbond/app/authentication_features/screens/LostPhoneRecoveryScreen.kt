package com.microbond.app.authentication_features.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.microbond.app.R
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.countrypicker.ChooseCountryScreen
import com.microbond.app.core.components.countrypicker.Country
import com.microbond.app.core.components.countrypicker.CountryData
import com.microbond.app.core.components.otpinput.OtpInputField
import com.microbond.app.core.components.otpinput.SmsAutoFillObserver
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.RequestRecoveryOTPRequest
import com.microbond.app.core.network.VerifyRecoveryOTPRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class LostPhoneStep {
    INPUT_CREDENTIALS,
    VERIFY_NEW_PHONE_OTP
}

/**
 * Microbond "Secure new number" Account Recovery Screen.
 * Rebuilt to match authentic WhatsApp-style SIM transfer workflow:
 * - Transparent Lottie illustration: `accountrecover.json`
 * - Old Phone Number row with Country picker chip + underlined phone input
 * - New Phone Number row with Country picker chip + underlined phone input
 * - App PIN underlined input
 * - Quick recovery helpers: "Forgot PIN?" & "Lost access to old number?" in top menu
 * - ReusableMicroButtonBig "Continue" anchored above soft keyboard
 */
@Composable
fun LostPhoneRecoveryScreen(
    onNavigateBack: () -> Unit,
    onRecoverySuccess: (newPhone: String, tempRecoveryToken: String) -> Unit,
    onBackupEmailRecoveryClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var step by remember { mutableStateOf(LostPhoneStep.INPUT_CREDENTIALS) }

    val defaultCountry = remember {
        CountryData.popularCountries.find { it.isoCode == "UG" }
            ?: CountryData.allCountries.firstOrNull()
            ?: Country(name = "Uganda", nativeName = null, dialCode = "256", isoCode = "UG", flagResId = 0)
    }

    var selectedOldCountry by remember { mutableStateOf(defaultCountry) }
    var oldPhoneNumber by remember { mutableStateOf("") }
    var showOldCountryPicker by remember { mutableStateOf(false) }

    var selectedNewCountry by remember { mutableStateOf(defaultCountry) }
    var newPhoneNumber by remember { mutableStateOf("") }
    var showNewCountryPicker by remember { mutableStateOf(false) }

    var appPin by remember { mutableStateOf("") }

    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendSeconds by remember { mutableIntStateOf(60) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showForgotPinDialog by remember { mutableStateOf(false) }

    if (showOldCountryPicker) {
        ChooseCountryScreen(
            onCountrySelected = { country ->
                selectedOldCountry = country
                showOldCountryPicker = false
            },
            onNavigateBack = {
                showOldCountryPicker = false
            }
        )
        return
    }

    if (showNewCountryPicker) {
        ChooseCountryScreen(
            onCountrySelected = { country ->
                selectedNewCountry = country
                showNewCountryPicker = false
            },
            onNavigateBack = {
                showNewCountryPicker = false
            }
        )
        return
    }

    BackHandler {
        if (showForgotPinDialog) {
            showForgotPinDialog = false
        } else if (showHelpDialog) {
            showHelpDialog = false
        } else if (step == LostPhoneStep.VERIFY_NEW_PHONE_OTP) {
            step = LostPhoneStep.INPUT_CREDENTIALS
            otpCode = ""
            errorMessage = null
        } else {
            onNavigateBack()
        }
    }

    LaunchedEffect(step) {
        if (step == LostPhoneStep.VERIFY_NEW_PHONE_OTP) {
            resendSeconds = 60
            while (resendSeconds > 0) {
                delay(1000)
                resendSeconds--
            }
        }
    }

    LaunchedEffect(step) {
        if (step == LostPhoneStep.VERIFY_NEW_PHONE_OTP) {
            delay(200)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {
            }
        }
    }

    val isFormValid = oldPhoneNumber.length >= 7 && newPhoneNumber.length >= 7 && appPin.length == 6

    fun submitRequestOtp() {
        val cleanOld = oldPhoneNumber.trim().removePrefix("0")
        val cleanNew = newPhoneNumber.trim().removePrefix("0")
        val formattedOld = "+${selectedOldCountry.dialCode}$cleanOld"
        val formattedNew = "+${selectedNewCountry.dialCode}$cleanNew"

        if (oldPhoneNumber.length < 7) {
            errorMessage = "Please enter your valid old phone number."
            return
        }
        if (appPin.length != 6) {
            errorMessage = "Please enter your 6-digit security PIN."
            return
        }
        if (newPhoneNumber.length < 7) {
            errorMessage = "Please enter your valid new active phone number."
            return
        }
        if (formattedOld == formattedNew) {
            errorMessage = "New phone number cannot be the same as old phone number."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.requestRecoveryOtp(
                    RequestRecoveryOTPRequest(
                        oldPhoneNumber = formattedOld,
                        appPin = appPin,
                        newPhoneNumber = formattedNew
                    )
                )

                if (response.isSuccessful) {
                    step = LostPhoneStep.VERIFY_NEW_PHONE_OTP
                } else {
                    val err = response.errorBody()?.string()
                    errorMessage = err ?: "Invalid old credentials or recovery rejected."
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage ?: "Unable to connect"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyNewPhoneOtp(code: String) {
        if (code.length != 6) return
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val cleanNew = newPhoneNumber.trim().removePrefix("0")
                val formattedNew = "+${selectedNewCountry.dialCode}$cleanNew"

                val response = NetworkClient.apiService.verifyRecoveryOtp(
                    VerifyRecoveryOTPRequest(
                        phoneNumber = formattedNew,
                        code = code
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.tempRecoveryToken
                    onRecoverySuccess(formattedNew, token)
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

    if (step == LostPhoneStep.VERIFY_NEW_PHONE_OTP) {
        SmsAutoFillObserver(enabled = !isLoading) { incomingCode ->
            if (incomingCode.length == 6) {
                otpCode = incomingCode
                verifyNewPhoneOtp(incomingCode)
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = if (step == LostPhoneStep.INPUT_CREDENTIALS) "Secure new number" else "Verify new number",
                appIconRes = R.drawable.microtopicon,
                showBackButton = true,
                showMoreIcon = true,
                onBackClick = {
                    if (step == LostPhoneStep.VERIFY_NEW_PHONE_OTP) {
                        step = LostPhoneStep.INPUT_CREDENTIALS
                        otpCode = ""
                        errorMessage = null
                    } else {
                        onNavigateBack()
                    }
                },
                menuActions = if (step == LostPhoneStep.INPUT_CREDENTIALS) {
                    listOf(
                        MicrobondMenuAction(
                            title = "Forgot PIN?",
                            onClick = {
                                showForgotPinDialog = true
                            }
                        ),
                        MicrobondMenuAction(
                            title = "Lost access to old number?",
                            onClick = {
                                onBackupEmailRecoveryClick()
                            }
                        ),
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
                } else {
                    listOf(
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
                }
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
                if (step == LostPhoneStep.INPUT_CREDENTIALS) {
                    Text(
                        text = "Your savings and group history remain protected.",
                        style = TextStyle(
                            color = MicrobondInactiveGray,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ReusableMicroButtonBig(
                        text = "Continue",
                        onClick = ::submitRequestOtp,
                        enabled = isFormValid && !isLoading,
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ReusableMicroButtonBig(
                        text = "Confirm Transfer & Log In",
                        onClick = { verifyNewPhoneOtp(otpCode) },
                        enabled = otpCode.length == 6 && !isLoading,
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
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
            // Hero Lottie Animation (transparent alpha against MicrobondDarkBackground)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("accountrecover.json")
                )
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                if (composition != null) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "LostPhoneStepTransition"
            ) { currentStep ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentStep == LostPhoneStep.INPUT_CREDENTIALS) {
                        // Title
                        Text(
                            text = "Enter your old and new numbers",
                            style = TextStyle(
                                color = MicrobondTextPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtitle
                        Text(
                            text = "We'll use your app PIN to verify that this account belongs to you.",
                            style = TextStyle(
                                color = MicrobondInactiveGray,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // 1. Old phone number row
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "Old phone number",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Country chip
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MicrobondOverlayWidgetDark)
                                        .clickable { showOldCountryPicker = true }
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+${selectedOldCountry.dialCode}",
                                        style = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select old phone country",
                                        tint = MicrobondInactiveGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Phone input
                                Column(modifier = Modifier.weight(1f)) {
                                    BasicTextField(
                                        value = oldPhoneNumber,
                                        onValueChange = { oldPhoneNumber = it.filter { ch -> ch.isDigit() }.take(15) },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 15.sp
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next
                                        ),
                                        cursorBrush = SolidColor(MicrobondActiveGreen),
                                        decorationBox = { innerTextField ->
                                            if (oldPhoneNumber.isEmpty()) {
                                                Text(
                                                    text = "Old phone number",
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
                                        color = if (oldPhoneNumber.isNotEmpty()) MicrobondActiveGreen else Color(0xFF2A3942),
                                        thickness = 1.2.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. New phone number row
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "New phone number",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Country chip
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MicrobondOverlayWidgetDark)
                                        .clickable { showNewCountryPicker = true }
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+${selectedNewCountry.dialCode}",
                                        style = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select new phone country",
                                        tint = MicrobondInactiveGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Phone input
                                Column(modifier = Modifier.weight(1f)) {
                                    BasicTextField(
                                        value = newPhoneNumber,
                                        onValueChange = { newPhoneNumber = it.filter { ch -> ch.isDigit() }.take(15) },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = MicrobondTextPrimary,
                                            fontSize = 15.sp
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next
                                        ),
                                        cursorBrush = SolidColor(MicrobondActiveGreen),
                                        decorationBox = { innerTextField ->
                                            if (newPhoneNumber.isEmpty()) {
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
                                        color = if (newPhoneNumber.isNotEmpty()) MicrobondActiveGreen else Color(0xFF2A3942),
                                        thickness = 1.2.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. App PIN field
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "App PIN",
                                style = TextStyle(
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            BasicTextField(
                                value = appPin,
                                onValueChange = {
                                    if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                        appPin = it
                                    }
                                },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation('•'),
                                textStyle = TextStyle(
                                    color = MicrobondTextPrimary,
                                    fontSize = 16.sp,
                                    letterSpacing = 4.sp
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done
                                ),
                                cursorBrush = SolidColor(MicrobondActiveGreen),
                                decorationBox = { innerTextField ->
                                    if (appPin.isEmpty()) {
                                        Text(
                                            text = "App PIN",
                                            style = TextStyle(
                                                color = MicrobondInactiveGray.copy(alpha = 0.5f),
                                                fontSize = 15.sp,
                                                letterSpacing = 0.sp
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
                                color = if (appPin.isNotEmpty()) MicrobondActiveGreen else Color(0xFF2A3942),
                                thickness = 1.2.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (!errorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
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
                    } else {
                        // VERIFY_NEW_PHONE_OTP step
                        Text(
                            text = "Verify new number",
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
                            text = "Enter the 6-digit verification code sent via SMS to +${selectedNewCountry.dialCode} $newPhoneNumber",
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
                            otpValue = otpCode,
                            onOtpChange = { input ->
                                if (input.length <= 6) {
                                    otpCode = input
                                    if (input.length == 6) {
                                        verifyNewPhoneOtp(input)
                                    }
                                }
                            },
                            focusRequester = focusRequester,
                            isError = !errorMessage.isNullOrBlank(),
                            onOtpComplete = ::verifyNewPhoneOtp
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
                                    text = "Resend SMS Code",
                                    style = TextStyle(
                                        color = microBondCheckMarkBlue,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.clickable {
                                        submitRequestOtp()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    if (showForgotPinDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPinDialog = false },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Forgot App PIN?",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "If you forgot your App PIN, you can recover your account using your linked backup email address.",
                    style = TextStyle(
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showForgotPinDialog = false
                        onBackupEmailRecoveryClick()
                    }
                ) {
                    Text(
                        text = "Use Backup Email",
                        style = TextStyle(
                            color = MicrobondActiveGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPinDialog = false }) {
                    Text(
                        text = "Cancel",
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
                    text = "Account Recovery Help",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "To recover your account after losing your SIM card, enter your registered old phone number, the 6-digit security PIN you set during onboarding, and your new phone number where you can receive SMS codes.",
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
private fun LostPhoneRecoveryScreenPreview() {
    LostPhoneRecoveryScreen(
        onNavigateBack = {},
        onRecoverySuccess = { _, _ -> }
    )
}
