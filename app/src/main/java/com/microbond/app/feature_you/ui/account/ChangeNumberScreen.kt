package com.microbond.app.feature_you.ui.account

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.countrypicker.ChooseCountryScreen
import com.microbond.app.core.components.countrypicker.Country
import com.microbond.app.core.components.countrypicker.CountryData
import com.microbond.app.core.components.otpinput.OtpInputField
import com.microbond.app.core.components.otpinput.SmsAutoFillObserver
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.RequestChangePhoneOTPRequest
import com.microbond.app.core.network.VerifyChangePhoneOTPRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondButtonDisabledBackground
import com.microbond.app.core.ui.theme.MicrobondButtonDisabledText
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondTextPrimaryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class ChangeNumberState {
    INFO, INPUT, VERIFY, SUCCESS
}

/**
 * Parses a raw phone string (e.g. "+256701234567") into a resolved Country and the remainder number.
 */
private fun parsePhoneNumber(raw: String): Pair<Country?, String> {
    val clean = raw.trim().removePrefix("+")
    val matched = CountryData.allCountries
        .filter { clean.startsWith(it.dialCode) }
        .maxByOrNull { it.dialCode.length }

    return if (matched != null) {
        Pair(matched, clean.removePrefix(matched.dialCode).trim())
    } else {
        val fallback = CountryData.findByIso("US") ?: CountryData.findByIso("UG")
        Pair(fallback, clean)
    }
}

/**
 * Modern Change Phone Number screen matching the design specification using official Microbond theme colors.
 */
@Composable
fun ChangeNumberScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var currentState by remember { mutableStateOf(ChangeNumberState.INFO) }

    val registeredPhone = AuthSessionManager.phoneNumber.value.orEmpty()
    val (initialOldCountry, initialOldPhone) = remember(registeredPhone) {
        parsePhoneNumber(registeredPhone)
    }

    var oldCountry by remember { mutableStateOf(initialOldCountry) }
    var oldPhoneNumber by remember { mutableStateOf(initialOldPhone) }

    var newCountry by remember {
        mutableStateOf(initialOldCountry ?: CountryData.findByIso("US"))
    }
    var newPhoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }

    var showCountryPicker by remember { mutableStateOf(false) }
    var pickingForOld by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendSeconds by remember { mutableIntStateOf(60) }

    val handleBack: () -> Unit = {
        when {
            showCountryPicker -> showCountryPicker = false
            currentState == ChangeNumberState.INFO -> onBack()
            currentState == ChangeNumberState.INPUT -> {
                currentState = ChangeNumberState.INFO
                errorMessage = null
            }
            currentState == ChangeNumberState.VERIFY -> {
                currentState = ChangeNumberState.INPUT
                verificationCode = ""
                errorMessage = null
            }
            currentState == ChangeNumberState.SUCCESS -> onBack()
        }
    }

    BackHandler(onBack = handleBack)

    // Full-screen Country Picker if user taps country code
    if (showCountryPicker) {
        ChooseCountryScreen(
            onCountrySelected = { selected ->
                if (pickingForOld) {
                    oldCountry = selected
                } else {
                    newCountry = selected
                }
                showCountryPicker = false
            },
            onNavigateBack = { showCountryPicker = false }
        )
        return
    }

    // Timer countdown for resending code
    LaunchedEffect(currentState) {
        if (currentState == ChangeNumberState.VERIFY) {
            resendSeconds = 60
            while (resendSeconds > 0) {
                delay(1000)
                resendSeconds--
            }
        }
    }

    // Auto-focus OTP input
    LaunchedEffect(currentState) {
        if (currentState == ChangeNumberState.VERIFY) {
            delay(200)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    fun submitRequestOtp() {
        val cleanNew = newPhoneNumber.trim()
        if (cleanNew.isBlank() || cleanNew.length < 6) {
            errorMessage = "Please enter a valid new phone number."
            return
        }

        val newDial = newCountry?.dialCode ?: "1"
        val fullNew = "+$newDial${cleanNew.removePrefix("+").removePrefix(newDial)}"

        val oldDial = oldCountry?.dialCode ?: "1"
        val fullOld = "+$oldDial${oldPhoneNumber.trim().removePrefix("+").removePrefix(oldDial)}"

        if (fullNew == fullOld) {
            errorMessage = "New phone number cannot be identical to your current phone number."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.requestChangePhoneOtp(
                    RequestChangePhoneOTPRequest(newPhoneNumber = fullNew)
                )

                if (response.isSuccessful) {
                    verificationCode = ""
                    resendSeconds = 60
                    currentState = ChangeNumberState.VERIFY
                } else {
                    val rawError = response.errorBody()?.string()
                    errorMessage = if (!rawError.isNullOrBlank()) {
                        try {
                            JSONObject(rawError).optString("detail", rawError)
                        } catch (_: Exception) {
                            rawError
                        }
                    } else {
                        "Failed to send code to new phone. Please try again."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage ?: "Unable to connect"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun submitVerifyOtp(code: String) {
        if (code.length != 6) return
        val newDial = newCountry?.dialCode ?: "1"
        val cleanNew = newPhoneNumber.trim().removePrefix("+").removePrefix(newDial)
        val fullNew = "+$newDial$cleanNew"

        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.apiService.verifyChangePhoneOtp(
                    VerifyChangePhoneOTPRequest(
                        newPhoneNumber = fullNew,
                        code = code
                    )
                )

                if (response.isSuccessful) {
                    val token = AuthSessionManager.accessToken.value.orEmpty()
                    AuthSessionManager.saveSession(
                        context = context,
                        token = token,
                        userId = null,
                        phoneNumber = fullNew,
                        hasAppPin = AuthSessionManager.hasAppPin.value
                    )
                    currentState = ChangeNumberState.SUCCESS
                } else {
                    val rawError = response.errorBody()?.string()
                    errorMessage = if (!rawError.isNullOrBlank()) {
                        try {
                            JSONObject(rawError).optString("detail", "Invalid verification code.")
                        } catch (_: Exception) {
                            "Invalid verification code. Please check and try again."
                        }
                    } else {
                        "Invalid verification code. Please check and try again."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Verification error: ${e.localizedMessage ?: "Unable to verify"}"
            } finally {
                isLoading = false
            }
        }
    }

    if (currentState == ChangeNumberState.VERIFY) {
        SmsAutoFillObserver(enabled = !isLoading) { incomingCode ->
            if (incomingCode.length == 6) {
                verificationCode = incomingCode
                submitVerifyOtp(incomingCode)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = handleBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MicrobondTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.microtopicon),
                        contentDescription = "Microbond Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Microbond",
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                when (currentState) {
                    ChangeNumberState.INFO -> {
                        Button(
                            onClick = { currentState = ChangeNumberState.INPUT },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MicrobondActiveGreen,
                                contentColor = microBondTextPrimaryLight
                            )
                        ) {
                            Text(
                                text = "NEXT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    ChangeNumberState.INPUT -> {
                        Button(
                            onClick = ::submitRequestOtp,
                            enabled = newPhoneNumber.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MicrobondActiveGreen,
                                contentColor = microBondTextPrimaryLight,
                                disabledContainerColor = MicrobondButtonDisabledBackground,
                                disabledContentColor = MicrobondButtonDisabledText
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = microBondTextPrimaryLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = "SEND CODE TO NEW NUMBER",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    ChangeNumberState.VERIFY -> {
                        Button(
                            onClick = { submitVerifyOtp(verificationCode) },
                            enabled = verificationCode.length == 6 && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MicrobondActiveGreen,
                                contentColor = microBondTextPrimaryLight,
                                disabledContainerColor = MicrobondButtonDisabledBackground,
                                disabledContentColor = MicrobondButtonDisabledText
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = microBondTextPrimaryLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = "CONFIRM & MIGRATE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    ChangeNumberState.SUCCESS -> {
                        ReusableMicroButtonBig(
                            text = "Done",
                            onClick = onBack,
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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = currentState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ChangeNumberStateTransition"
            ) { state ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state) {
                        ChangeNumberState.INFO -> {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Changing your phone number",
                                color = MicrobondTextPrimary,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Migrating your phone number will transfer all your account info, groups, microbonds, and transaction history.",
                                color = MicrobondInactiveGray,
                                fontSize = 14.5.sp,
                                lineHeight = 21.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Image(
                                painter = painterResource(id = R.drawable.phonechangefirstpageimage),
                                contentDescription = "Migration Overview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                InfoBenefitRow("Your active groups, savings cycles, and rotation payouts will remain completely intact.")
                                InfoBenefitRow("Your wallet balances and financial records will safely migrate to the new SIM.")
                                InfoBenefitRow("Before proceeding, confirm that your new phone number can receive SMS codes.")
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        ChangeNumberState.INPUT -> {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Change phone number",
                                color = MicrobondTextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Update your phone number while keeping your account, groups, and settings.",
                                color = MicrobondInactiveGray,
                                fontSize = 14.5.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Glowing Phones illustration (phonechangelastpage.png)
                            Image(
                                painter = painterResource(id = R.drawable.phonechangelastpage),
                                contentDescription = "Change phone illustration",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Current Phone Input
                            Text(
                                text = "Current phone number",
                                color = MicrobondTextPrimary,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PhoneNumberEntryRow(
                                country = oldCountry,
                                phoneNumber = oldPhoneNumber,
                                onPhoneNumberChange = {
                                    oldPhoneNumber = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                onCountryClick = {
                                    pickingForOld = true
                                    showCountryPicker = true
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // New Phone Input
                            Text(
                                text = "New phone number",
                                color = MicrobondTextPrimary,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PhoneNumberEntryRow(
                                country = newCountry,
                                phoneNumber = newPhoneNumber,
                                onPhoneNumberChange = {
                                    newPhoneNumber = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                onCountryClick = {
                                    pickingForOld = false
                                    showCountryPicker = true
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Security note with shield icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = "Security Note",
                                    tint = MicrobondActiveGreen,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "You must be able to receive SMS or calls on your new number.",
                                    color = MicrobondInactiveGray,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            if (!errorMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = errorMessage!!,
                                    style = TextStyle(
                                        color = MicrobondErrorColor,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        ChangeNumberState.VERIFY -> {
                            Spacer(modifier = Modifier.height(16.dp))

                            Image(
                                painter = painterResource(id = R.drawable.phonechangelastpage),
                                contentDescription = "Verify phone",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Verify your new number",
                                color = MicrobondTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val displayNewPhone = "+${newCountry?.dialCode ?: "1"} $newPhoneNumber"
                            Text(
                                text = "Enter the 6-digit SMS verification code sent to $displayNewPhone",
                                color = MicrobondInactiveGray,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            OtpInputField(
                                otpValue = verificationCode,
                                onOtpChange = { input ->
                                    if (input.length <= 6) {
                                        verificationCode = input
                                        if (input.length == 6) {
                                            submitVerifyOtp(input)
                                        }
                                    }
                                },
                                focusRequester = focusRequester,
                                isError = !errorMessage.isNullOrBlank(),
                                onOtpComplete = ::submitVerifyOtp
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
                                        text = "Resend SMS Code",
                                        style = TextStyle(
                                            color = MicrobondActiveGreen,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            textDecoration = TextDecoration.Underline
                                        ),
                                        modifier = Modifier.clickable { submitRequestOtp() }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        ChangeNumberState.SUCCESS -> {
                            Spacer(modifier = Modifier.height(32.dp))

                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MicrobondActiveGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "Success",
                                    tint = MicrobondActiveGreen,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Phone Number Updated!",
                                color = MicrobondTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val finalPhone = "+${newCountry?.dialCode ?: "1"} $newPhoneNumber"
                            Text(
                                text = "Your account is now linked to $finalPhone. All your microbonds, groups, and settings have migrated successfully.",
                                color = MicrobondInactiveGray,
                                fontSize = 14.5.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Underlined phone number input row with country picker trigger using official Microbond theme colors.
 */
@Composable
private fun PhoneNumberEntryRow(
    country: Country?,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onCountryClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country Dial Code with dropdown chevron
            Row(
                modifier = Modifier
                    .clickable(onClick = onCountryClick)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+${country?.dialCode ?: "1"}",
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select country",
                    tint = MicrobondInactiveGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Thin vertical separator
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp)
                    .background(MicrobondDividerDark)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Phone digits input
            BasicTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle = TextStyle(
                    color = MicrobondTextPrimary,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(MicrobondActiveGreen),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (phoneNumber.isEmpty()) {
                            Text(
                                text = "Phone number",
                                style = TextStyle(
                                    color = MicrobondInactiveGray.copy(alpha = 0.5f),
                                    fontSize = 16.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Official Microbond active green underline
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.5.dp,
            color = MicrobondActiveGreen
        )
    }
}

@Composable
private fun InfoBenefitRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp, end = 12.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MicrobondActiveGreen)
        )
        Text(
            text = text,
            color = MicrobondTextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}
