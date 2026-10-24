package com.microbond.app.authentication_features.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.authdialogs.ConfirmNumberDialog
import com.microbond.app.core.components.authdialogs.NumberAlreadyRegisteredDialog
import com.microbond.app.core.components.countrypicker.ChooseCountryScreen
import com.microbond.app.core.components.countrypicker.Country
import com.microbond.app.core.components.countrypicker.CountryData
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondErrorColor
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary

/**
 * Microbond "Enter your phone number" Registration Screen.
 * Uses modular country picker and authentication dialog packages, adhering to
 * zero hardcoded colors and pixel-accurate messaging design.
 */
@Composable
fun EnterPhoneNumberScreen(
    appName: String = "Microbond",
    initialCountry: Country? = null,
    initialPhoneNumber: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    alreadyRegisteredNumber: String? = null,
    onNavigateBack: () -> Unit = {},
    onPhoneNumberChange: (country: Country?, phoneNumber: String) -> Unit = { _, _ -> },
    onNextClick: (fullPhoneNumber: String) -> Unit = {},
    onGoToAccountClick: (fullPhoneNumber: String) -> Unit = {},
    onDismissAlreadyRegistered: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onLostPhoneRecoveryClick: () -> Unit = {},
    onLostAccessToNumberClick: () -> Unit = onLostPhoneRecoveryClick
) {
    val backgroundColor = MicrobondDarkBackground
    val textPrimaryColor = MicrobondTextPrimary
    val textSecondaryColor = MicrobondInactiveGray
    val brandGreenColor = MicrobondActiveGreen

    var selectedCountry by remember(initialCountry) { mutableStateOf(initialCountry) }
    var countryCode by remember(initialCountry) { mutableStateOf(initialCountry?.dialCode ?: "") }
    var phoneNumber by remember(initialPhoneNumber) { mutableStateOf(initialPhoneNumber) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (showConfirmDialog) {
            showConfirmDialog = false
        } else if (showInfoDialog) {
            showInfoDialog = false
        } else {
            onNavigateBack()
        }
    }

    val isPhoneValid = countryCode.isNotBlank() && phoneNumber.length >= 7

    // Full-screen Country Picker when requested
    if (showCountryPicker) {
        ChooseCountryScreen(
            onCountrySelected = { country ->
                selectedCountry = country
                countryCode = country.dialCode
                showCountryPicker = false
                onPhoneNumberChange(country, phoneNumber)
            },
            onNavigateBack = {
                showCountryPicker = false
            }
        )
        return
    }

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "",
                showBackButton = true,
                showMoreIcon = true,
                onBackClick = onNavigateBack,
                menuActions = listOf(
                    MicrobondMenuAction(
                        title = "Recover account",
                        onClick = onLostPhoneRecoveryClick
                    ),
                    MicrobondMenuAction(
                        title = "Lost access to my number",
                        onClick = onLostAccessToNumberClick
                    ),
                    MicrobondMenuAction(
                        title = "Help",
                        onClick = {
                            if (onHelpClick != {}) {
                                onHelpClick()
                            } else {
                                showInfoDialog = true
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
                    .background(backgroundColor)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReusableMicroButtonBig(
                    text = "Next",
                    onClick = {
                        if (isPhoneValid) {
                            showConfirmDialog = true
                        }
                    },
                    enabled = isPhoneValid,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        val subtitleText = buildAnnotatedString {
            append("$appName will verify your account. ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "WHATS_MY_NUMBER",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = brandGreenColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    ),
                    linkInteractionListener = { showInfoDialog = true }
                )
            ) {
                append("What's my phone number?")
            }
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Title
            Text(
                text = "Enter your phone number",
                style = TextStyle(
                    color = textPrimaryColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle with green clickable "What's my phone number?"
            Text(
                text = subtitleText,
                style = TextStyle(
                    color = textSecondaryColor,
                    fontSize = 13.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input Fields Block (Centered, matching reference 62% width)
            Column(
                modifier = Modifier.fillMaxWidth(0.64f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Country Selection Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCountryPicker = true }
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = selectedCountry?.name ?: "Choose a country",
                        style = TextStyle(
                            color = textPrimaryColor,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Choose a country",
                        tint = brandGreenColor,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                HorizontalDivider(
                    color = brandGreenColor,
                    thickness = 1.2.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Phone Input Row: [+ Code] and [Phone number]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code section
                    Column(modifier = Modifier.width(62.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "+",
                                style = TextStyle(
                                    color = textSecondaryColor,
                                    fontSize = 16.sp
                                )
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            BasicTextField(
                                value = countryCode,
                                onValueChange = { newCode ->
                                    val filtered = newCode.filter { it.isDigit() }.take(4)
                                    countryCode = filtered
                                    val matched = CountryData.findByDialCode(filtered)
                                    if (matched != null) {
                                        selectedCountry = matched
                                    }
                                    onPhoneNumberChange(selectedCountry, phoneNumber)
                                },
                                textStyle = TextStyle(
                                    color = textPrimaryColor,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                cursorBrush = SolidColor(brandGreenColor),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        HorizontalDivider(
                            color = brandGreenColor,
                            thickness = 1.2.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Phone number section
                    Column(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = phoneNumber,
                            onValueChange = { newNumber ->
                                val filtered = newNumber.filter { it.isDigit() }.take(15)
                                phoneNumber = filtered
                                onPhoneNumberChange(selectedCountry, filtered)
                            },
                            textStyle = TextStyle(
                                color = textPrimaryColor,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            cursorBrush = SolidColor(brandGreenColor),
                            decorationBox = { innerTextField ->
                                if (phoneNumber.isEmpty()) {
                                    Text(
                                        text = "Phone number",
                                        style = TextStyle(
                                            color = textSecondaryColor,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )

                        HorizontalDivider(
                            color = brandGreenColor,
                            thickness = 1.2.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = TextStyle(
                        color = MicrobondErrorColor,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Confirmation Dialog: "Is this the correct number?"
    if (showConfirmDialog) {
        val formattedNumber = "+$countryCode $phoneNumber"
        ConfirmNumberDialog(
            formattedPhoneNumber = formattedNumber,
            onDismissRequest = { showConfirmDialog = false },
            onEditClick = { showConfirmDialog = false },
            onYesClick = {
                showConfirmDialog = false
                val cleanCountry = countryCode.trim().removePrefix("+")
                val clean = phoneNumber.trim().removePrefix("0")
                onNextClick("+$cleanCountry$clean")
            }
        )
    }

    // "Number already registered on this device" Dialog
    if (!alreadyRegisteredNumber.isNullOrBlank()) {
        NumberAlreadyRegisteredDialog(
            formattedPhoneNumber = alreadyRegisteredNumber,
            onDismissRequest = onDismissAlreadyRegistered,
            onCancelClick = onDismissAlreadyRegistered,
            onGoToAccountClick = {
                onGoToAccountClick(alreadyRegisteredNumber)
            }
        )
    }

    // Informational Dialog: "What's my phone number?"
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "What's my phone number?",
                    style = TextStyle(
                        color = textPrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "Your phone number is the number associated with your SIM card. You can verify it in your device's SIM card manager or by dialing your carrier's USSD check code.",
                    style = TextStyle(
                        color = textSecondaryColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(
                        text = "OK",
                        style = TextStyle(
                            color = brandGreenColor,
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
private fun EnterPhoneNumberScreenPreview() {
    EnterPhoneNumberScreen()
}
