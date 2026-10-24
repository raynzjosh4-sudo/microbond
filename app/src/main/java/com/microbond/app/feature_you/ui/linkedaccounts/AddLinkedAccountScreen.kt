package com.microbond.app.feature_you.ui.linkedaccounts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.authentication_features.data.AuthSessionManager
import com.microbond.app.core.components.MicrobondTopAppBar
import com.microbond.app.core.components.chips.MicrobondChoiceChips
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBorderDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondPureWhite
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkedAccountScreen(onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val userHasAppPin by AuthSessionManager.hasAppPin.collectAsState()

    var accountType by remember { mutableStateOf("Mobile Money") }
    var provider by remember { mutableStateOf("MTN Mobile Money") }
    var accountNumber by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(true) }
    var appPinText by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isPinValid = !userHasAppPin || appPinText.trim().length >= 4
    val canSubmit = provider.isNotBlank() && accountNumber.trim().length >= 7 && isPinValid && !isSubmitting

    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MicrobondDarkBackground,
        topBar = {
            MicrobondTopAppBar(
                title = "Link account",
                showMoreIcon = false,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Account Type",
                color = MicrobondInactiveGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            MicrobondChoiceChips(
                labels = listOf("Mobile Money", "Bank Account"),
                selectedChip = accountType,
                onChipSelected = { selected ->
                    accountType = selected
                    provider = if (selected == "Mobile Money") "MTN Mobile Money" else "Centenary Bank"
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick provider presets for Mobile Money
            if (accountType == "Mobile Money") {
                Text(
                    text = "Select Provider",
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                MicrobondChoiceChips(
                    labels = listOf("MTN Mobile Money", "Airtel Money", "Other"),
                    selectedChip = if (provider in listOf("MTN Mobile Money", "Airtel Money")) provider else "Other",
                    onChipSelected = { selected ->
                        if (selected != "Other") {
                            provider = selected
                        } else if (provider in listOf("MTN Mobile Money", "Airtel Money")) {
                            provider = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(
                    text = "Popular Banks",
                    color = MicrobondInactiveGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                MicrobondChoiceChips(
                    labels = listOf("Centenary Bank", "Stanbic Bank", "Equity Bank", "Absa Bank"),
                    selectedChip = provider,
                    onChipSelected = { provider = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom / Editable Provider Name
            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = {
                    Text(if (accountType == "Mobile Money") "Provider Name (e.g. MTN, Airtel)" else "Bank Name")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    focusedBorderColor = MicrobondActiveGreen,
                    unfocusedBorderColor = MicrobondBorderDark,
                    focusedLabelColor = MicrobondActiveGreen,
                    unfocusedLabelColor = MicrobondInactiveGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Account / Phone Number
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = {
                    Text(if (accountType == "Mobile Money") "Mobile Money Phone Number" else "Bank Account Number")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    focusedBorderColor = MicrobondActiveGreen,
                    unfocusedBorderColor = MicrobondBorderDark,
                    focusedLabelColor = MicrobondActiveGreen,
                    unfocusedLabelColor = MicrobondInactiveGray
                ),
                placeholder = {
                    Text(
                        if (accountType == "Mobile Money") "e.g. 0770123456" else "e.g. 3100012345",
                        color = MicrobondInactiveGray
                    )
                },
                singleLine = true
            )

            // App PIN authorization if user has set up a PIN
            if (userHasAppPin) {
                Spacer(modifier = Modifier.height(16.dp))
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
                    label = { Text("Security PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        focusedBorderColor = MicrobondActiveGreen,
                        unfocusedBorderColor = MicrobondBorderDark,
                        focusedLabelColor = MicrobondActiveGreen,
                        unfocusedLabelColor = MicrobondInactiveGray
                    ),
                    placeholder = { Text("4–6 digit security PIN", color = MicrobondInactiveGray) },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Set as default payout account toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDefault = !isDefault }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MicrobondActiveGreen,
                        uncheckedColor = MicrobondInactiveGray,
                        checkmarkColor = MicrobondPureWhite
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Set as default payout account",
                    color = MicrobondTextPrimary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Microbond verifies the account holder name via official banking & telco KYC gateways upon linking.",
                color = MicrobondInactiveGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isSubmitting = true
                        val typeCode = if (accountType == "Bank Account") "BANK_ACCOUNT" else "MOBILE_MONEY"
                        val result = LinkedAccountsManager.addAccount(
                            providerName = provider.trim(),
                            accountNumber = accountNumber.trim(),
                            accountType = typeCode,
                            isDefault = isDefault,
                            appPin = if (userHasAppPin) appPinText.trim().ifBlank { null } else null
                        )
                        isSubmitting = false
                        result.fold(
                            onSuccess = {
                                onBack()
                            },
                            onFailure = { err ->
                                errorMessage = err.localizedMessage ?: "Failed to link account"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MicrobondActiveGreen,
                    disabledContainerColor = MicrobondBorderDark
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = MicrobondPureWhite,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Verify & Link Account", color = MicrobondPureWhite, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            containerColor = MicrobondOverlayWidgetDark,
            title = {
                Text(
                    text = "Cannot Link Account",
                    color = MicrobondDestructiveRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = errorMessage ?: "",
                    color = MicrobondTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("OK", color = MicrobondActiveGreen)
                }
            }
        )
    }
}
