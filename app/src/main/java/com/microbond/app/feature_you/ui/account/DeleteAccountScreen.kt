package com.microbond.app.feature_you.ui.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.network.DeleteAccountRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondBorderDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveDisabledBg
import com.microbond.app.core.ui.theme.MicrobondDestructiveDisabledText
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun DeleteAccountScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val currentPhone by AuthSessionManager.phoneNumber.collectAsState()
    val userHasAppPin by AuthSessionManager.hasAppPin.collectAsState()

    var reasonText by remember { mutableStateOf("") }
    var appPinText by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var confirmationPhrase by remember { mutableStateOf("") }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isConfirmationValid = confirmationPhrase.trim().equals("DELETE MY ACCOUNT", ignoreCase = true)
    val isPinValid = !userHasAppPin || appPinText.trim().length >= 4
    val canProceed = isConfirmationValid && isPinValid && !isDeleting

    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MicrobondTopAppBar(
                title = "Delete this account",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MicrobondDarkBackground)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Warning Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MicrobondDestructiveRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "If you delete this account:",
                    color = MicrobondDestructiveRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "• The account will be deleted from Microbond and all your devices\n• Your message history and linked payment accounts will be removed\n• You will be removed from all your Microbond saving circles\n• Your personal data will be anonymized in accordance with GDPR principles",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Registered Phone Number Info
            Text(
                text = "Account Phone Number",
                color = MicrobondTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (!currentPhone.isNullOrBlank()) currentPhone!! else "Active Session Account",
                color = MicrobondInactiveGray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            // App PIN Field (if user has set up a PIN)
            if (userHasAppPin) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Enter Security PIN",
                    color = MicrobondTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = appPinText,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            appPinText = it
                        }
                    },
                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { isPinVisible = !isPinVisible }) {
                            Icon(
                                imageVector = if (isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPinVisible) "Hide PIN" else "Show PIN",
                                tint = MicrobondInactiveGray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        focusedBorderColor = MicrobondDestructiveRed,
                        unfocusedBorderColor = MicrobondBorderDark,
                        cursorColor = MicrobondDestructiveRed
                    ),
                    placeholder = { Text("4–6 digit security PIN", color = MicrobondInactiveGray) },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Optional Reason Field
            Text(
                text = "Reason for leaving (Optional)",
                color = MicrobondTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = reasonText,
                onValueChange = { reasonText = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    focusedBorderColor = MicrobondDestructiveRed,
                    unfocusedBorderColor = MicrobondBorderDark,
                    cursorColor = MicrobondDestructiveRed
                ),
                placeholder = { Text("Tell us why you are deleting your account", color = MicrobondInactiveGray) },
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation Phrase Requirement
            Text(
                text = "To confirm deletion, type:",
                color = MicrobondTextPrimary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "DELETE MY ACCOUNT",
                color = MicrobondDestructiveRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmationPhrase,
                onValueChange = { confirmationPhrase = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    focusedBorderColor = if (isConfirmationValid) MicrobondDestructiveRed else MicrobondBorderDark,
                    unfocusedBorderColor = MicrobondBorderDark,
                    cursorColor = MicrobondDestructiveRed
                ),
                placeholder = { Text("Type DELETE MY ACCOUNT", color = MicrobondInactiveGray) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = canProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MicrobondDestructiveRed,
                    disabledContainerColor = MicrobondDestructiveDisabledBg,
                    disabledContentColor = MicrobondDestructiveDisabledText
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        color = MicrobondPureWhite,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Delete account",
                        color = if (canProceed) MicrobondPureWhite else MicrobondDestructiveDisabledText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Confirmation Alert Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showConfirmDialog = false },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Permanently delete account?",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "This action is final and irreversible. Your circles, messages, and wallet accounts will be permanently closed.",
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
                        showConfirmDialog = false
                        isDeleting = true
                        coroutineScope.launch {
                            try {
                                val payload = DeleteAccountRequest(
                                    confirmation = "DELETE MY ACCOUNT",
                                    reason = reasonText.trim().ifBlank { null },
                                    appPin = if (userHasAppPin) appPinText.trim().ifBlank { null } else null
                                )
                                val response = NetworkClient.apiService.deleteAccount(payload)
                                if (response.isSuccessful) {
                                    AuthSessionManager.clearSession(context)
                                    onAccountDeleted()
                                } else {
                                    val rawError = response.errorBody()?.string()
                                    val message = try {
                                        val json = JSONObject(rawError.orEmpty())
                                        if (json.has("detail")) {
                                            val d = json.get("detail")
                                            if (d is String) d else d.toString()
                                        } else {
                                            json.optString("message", "Failed to delete account.")
                                        }
                                    } catch (_: Exception) {
                                        rawError ?: "Failed to delete account."
                                    }
                                    errorMessage = message
                                }
                            } catch (e: Exception) {
                                errorMessage = e.localizedMessage ?: "Network error. Please check your connection."
                            } finally {
                                isDeleting = false
                            }
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text(
                        text = "Permanently Delete",
                        color = MicrobondDestructiveRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    enabled = !isDeleting
                ) {
                    Text(text = "Cancel", color = MicrobondInactiveGray)
                }
            }
        )
    }

    // Error Feedback Dialog
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Cannot Delete Account",
                    style = TextStyle(
                        color = MicrobondDestructiveRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = errorMessage ?: "",
                    style = TextStyle(
                        color = MicrobondTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text(text = "OK", color = MicrobondDestructiveRed)
                }
            }
        )
    }
}

