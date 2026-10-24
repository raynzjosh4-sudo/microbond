package com.microbond.app.authentication_features.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.R
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue
import com.microbond.app.core.ui.theme.microBondTextPrimaryLight

/**
 * Microbond "Add an account" / Welcome Onboarding Screen.
 * Emulates the modern messaging onboarding UX while strictly using Microbond branding and assets.
 *
 * Prompts the user to accept Terms of Service and Privacy Policy before proceeding
 * to phone number registration and OTP verification.
 */
@Composable
fun AddAccountWelcomeScreen(
    appName: String = "Microbond",
    onAgreeAndContinue: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onLearnMoreClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    // Centralized colors from theme Color.kt
    val backgroundColor = MicrobondDarkBackground
    val textPrimaryColor = MicrobondTextPrimary
    val textSecondaryColor = MicrobondInactiveGray
    val linkBlueColor = microBondCheckMarkBlue
    val brandGreenColor = MicrobondActiveGreen
    val buttonTextColor = microBondTextPrimaryLight

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {

            //MicrobondTopAppBar()
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimaryColor
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
        // Terms & Privacy statement with styled clickable links
        val linkStyles = TextLinkStyles(
            style = SpanStyle(color = linkBlueColor)
        )

        val disclaimerText = buildAnnotatedString {
            append("Read our ")

            withLink(
                LinkAnnotation.Clickable(
                    tag = "PRIVACY",
                    styles = linkStyles,
                    linkInteractionListener = { onPrivacyPolicyClick() }
                )
            ) {
                append("Privacy Policy")
            }

            append(". Tap \"Agree and continue\" to accept the ")

            withLink(
                LinkAnnotation.Clickable(
                    tag = "TERMS",
                    styles = linkStyles,
                    linkInteractionListener = { onTermsOfServiceClick() }
                )
            ) {
                append("Terms of Service")
            }

            append(". $appName will verify your account and may send account-related SMS messages. ")

            withLink(
                LinkAnnotation.Clickable(
                    tag = "LEARN_MORE",
                    styles = linkStyles,
                    linkInteractionListener = { onLearnMoreClick() }
                )
            ) {
                append("Learn more")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Upper spacing: modest gap (~20-22% down to icon)
            Spacer(modifier = Modifier.weight(0.6f))

            // Official Microbond App Glyph (Clean transparent background)
            Image(
                painter = painterResource(id = R.drawable.microtopicon),
                contentDescription = "$appName Logo",
                modifier = Modifier.size(140.dp)
            )

            // Very large gap between Icon and "Add an account" (matches WhatsApp reference ~21% of screen)
            Spacer(modifier = Modifier.weight(1.0f))

            // Title & Disclaimer text group positioned around vertical center (~50-52%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add an account",
                    style = TextStyle(
                        color = textPrimaryColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = disclaimerText,
                    style = TextStyle(
                        color = textSecondaryColor,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                )
            }

            // Lower spacing between text and button
            Spacer(modifier = Modifier.weight(1.25f))

            // Bottom CTA Button sitting right on the bottom navigation line
            ReusableMicroButtonBig(
                text = "Agree and continue",
                onClick = onAgreeAndContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun AddAccountWelcomeScreenPreview() {
    AddAccountWelcomeScreen()
}
