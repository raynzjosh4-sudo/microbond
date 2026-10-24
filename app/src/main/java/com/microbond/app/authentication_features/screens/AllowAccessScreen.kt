package com.microbond.app.authentication_features.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.ContextCompat
import com.microbond.app.core.components.menu.MicrobondOverflowMenu
import com.microbond.app.core.components.reusablemicrobuttonbig.ReusableMicroButtonBig
import com.microbond.app.core.data_classes_and_enums.MicrobondMenuAction
import com.microbond.app.core.network.sync.ContactSyncEngine
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.microBondCheckMarkBlue

/**
 * Microbond "Allow access" Screen.
 * Configured specifically for automatic SMS verification and ContactSyncEngine permissions,
 * following the exact visual layout of the reference screen.
 */
@Composable
fun AllowAccessScreen(
    appName: String = "Microbond",
    secondaryButtonText: String = "Verify another way",
    onContinue: () -> Unit = {},
    onVerifyAnotherWay: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onLearnMoreClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val backgroundColor = MicrobondDarkBackground
    val textPrimaryColor = MicrobondTextPrimary
    val textSecondaryColor = MicrobondInactiveGray
    val brandGreenColor = MicrobondActiveGreen
    val linkBlueColor = microBondCheckMarkBlue

    // Android runtime permissions launcher for ContactSyncEngine and SMS detection
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val contactsGranted = permissions[Manifest.permission.READ_CONTACTS] == true
        if (contactsGranted) {
            ContactSyncEngine.syncContacts(context)
        }
        onContinue()
    }

    fun handleContinueWithPermissions() {
        val neededPermissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.READ_CONTACTS)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.RECEIVE_SMS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (neededPermissions.isNotEmpty()) {
            permissionsLauncher.launch(neededPermissions.toTypedArray())
        } else {
            ContactSyncEngine.syncContacts(context)
            onContinue()
        }
    }

    BackHandler(onBack = onNavigateBack)

    Scaffold(
        containerColor = backgroundColor,
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
        val privacyLinkText = buildAnnotatedString {
            append("Learn more about ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "SMS_PRIVACY",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = linkBlueColor,
                            fontWeight = FontWeight.Medium
                        )
                    ),
                    linkInteractionListener = { onLearnMoreClick() }
                )
            ) {
                append("SMS verification and privacy")
            }
            append(".")
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Hero Illustration: Green SMS verification glyph
            Icon(
                imageVector = Icons.Filled.MarkChatRead,
                contentDescription = "SMS Verification",
                tint = brandGreenColor,
                modifier = Modifier.size(68.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Screen Title
            Text(
                text = "Allow access",
                style = TextStyle(
                    color = textPrimaryColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "To verify automatically with SMS and sync with your groups, allow access on the next screens:",
                style = TextStyle(
                    color = textSecondaryColor,
                    fontSize = 14.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                ),
                modifier = Modifier.padding(horizontal = 36.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Permissions / Access Details list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Feature item 1: Read SMS messages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sms,
                        contentDescription = null,
                        tint = textSecondaryColor,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "Send and view SMS messages",
                            style = TextStyle(
                                color = textPrimaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Allows $appName to automatically detect the verification code sent to your phone.",
                            style = TextStyle(
                                color = textSecondaryColor,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Feature item 2: Verify SIM / Device connection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhoneAndroid,
                        contentDescription = null,
                        tint = textSecondaryColor,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "Verify device and SIM",
                            style = TextStyle(
                                color = textPrimaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Allows $appName to confirm your carrier network and deliver your one-time password securely.",
                            style = TextStyle(
                                color = textSecondaryColor,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Feature item 3: Contacts & Groups for ContactSyncEngine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Contacts,
                        contentDescription = null,
                        tint = textSecondaryColor,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "Contacts and savings groups",
                            style = TextStyle(
                                color = textPrimaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Allows $appName to sync your contacts, identify friends using the app, and show which savings groups you share.",
                            style = TextStyle(
                                color = textSecondaryColor,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Privacy / Learn more statement
                Text(
                    text = privacyLinkText,
                    style = TextStyle(
                        color = textSecondaryColor,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Bottom Action: Continue
            ReusableMicroButtonBig(
                text = "Continue",
                onClick = ::handleContinueWithPermissions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Secondary Action: Verify another way / Not now
            TextButton(
                onClick = onVerifyAnotherWay,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = secondaryButtonText,
                    style = TextStyle(
                        color = brandGreenColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B141B)
@Composable
private fun AllowAccessScreenPreview() {
    AllowAccessScreen()
}
