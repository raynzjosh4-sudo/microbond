package com.microbond.app.feature_groups.ui.newgroup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NINSubmissionRequest
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondBottomSheetDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondInfoBannerContainer
import com.microbond.app.core.ui.theme.MicrobondInfoBannerIconBg
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_groups.data.GroupCreationManager
import kotlinx.coroutines.launch

/**
 * National ID (NIN) KYC Verification Screen.
 *
 * Verifies government identity to upgrade account to Tier 2,
 * immediately unlocking group creation archetypes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityVerificationScreen(
    onBack: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var ninInput by remember { mutableStateOf("") }
    var firstNameInput by remember { mutableStateOf("") }
    var lastNameInput by remember { mutableStateOf("") }
    var dobInput by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    fun submitVerification() {
        val cleanNin = ninInput.trim().uppercase()
        if (cleanNin.length < 14) {
            errorMessage = "Please enter a valid 14-character Ugandan National ID Number (NIN)."
            return
        }

        focusManager.clearFocus()
        isLoading = true
        errorMessage = null

        coroutineScope.launch {
            try {
                val payload = NINSubmissionRequest(
                    nin = cleanNin,
                    firstName = firstNameInput.trim().ifBlank { null },
                    lastName = lastNameInput.trim().ifBlank { null },
                    dob = dobInput.trim().ifBlank { null }
                )

                val response = NetworkClient.apiService.verifyNIN(payload)
                if (response.isSuccessful) {
                    isSuccess = true
                    // Refresh progressive unlock matrix so allowed groups immediately reflect
                    GroupCreationManager.fetchAllowedGroups(forceRefresh = true)
                    onVerificationSuccess()
                } else {
                    errorMessage = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "Verification failed (${response.code()}). Please verify your NIN details and try again."
                }
            } catch (e: Exception) {
                // If offline/mock server, fallback to upgrading allowed groups locally
                errorMessage = e.localizedMessage ?: "Network error connecting to verification gateway"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MicrobondDarkBackground)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MicrobondTextPrimary
                    )
                }
                Text(
                    text = "Identity Verification",
                    color = MicrobondTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Shield Header Icon ──
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color = MicrobondInfoBannerIconBg, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MicrobondActiveGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Verify your National ID (NIN)",
                color = MicrobondTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tier 2 identity verification unlocks all Microbond group savings pools, shared wallets, and community fundraising.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // ── Verification Benefits Card ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MicrobondInfoBannerContainer, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VerificationBenefitRow("Create Goal Savings, Rotating & Shared Wallet groups")
                VerificationBenefitRow("Higher daily & communal contribution thresholds")
                VerificationBenefitRow("Verified community trust badge for your account")
            }

            Spacer(Modifier.height(24.dp))

            // ── Input: National Identification Number ──
            OutlinedTextField(
                value = ninInput,
                onValueChange = { input ->
                    if (input.length <= 14) {
                        ninInput = input.uppercase()
                    }
                    if (errorMessage != null) errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("National ID Number (NIN)", color = MicrobondInactiveGray) },
                placeholder = { Text("e.g. CM920181023ABC", color = MicrobondInactiveGray.copy(alpha = 0.5f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                supportingText = {
                    Text(
                        text = "${ninInput.length}/14 characters",
                        color = if (ninInput.length == 14) MicrobondActiveGreen else MicrobondInactiveGray
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MicrobondActiveGreen,
                    unfocusedBorderColor = MicrobondDividerDark,
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    cursorColor = MicrobondActiveGreen,
                    focusedContainerColor = MicrobondOverlayWidgetDark,
                    unfocusedContainerColor = MicrobondOverlayWidgetDark
                )
            )

            Spacer(Modifier.height(10.dp))

            // ── Inputs: First & Last Name ──
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = firstNameInput,
                    onValueChange = { firstNameInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("First Name", color = MicrobondInactiveGray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MicrobondActiveGreen,
                        unfocusedBorderColor = MicrobondDividerDark,
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        cursorColor = MicrobondActiveGreen,
                        focusedContainerColor = MicrobondOverlayWidgetDark,
                        unfocusedContainerColor = MicrobondOverlayWidgetDark
                    )
                )

                OutlinedTextField(
                    value = lastNameInput,
                    onValueChange = { lastNameInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Last Name", color = MicrobondInactiveGray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MicrobondActiveGreen,
                        unfocusedBorderColor = MicrobondDividerDark,
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        cursorColor = MicrobondActiveGreen,
                        focusedContainerColor = MicrobondOverlayWidgetDark,
                        unfocusedContainerColor = MicrobondOverlayWidgetDark
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── Input: Date of Birth ──
            OutlinedTextField(
                value = dobInput,
                onValueChange = { dobInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date of Birth (YYYY-MM-DD)", color = MicrobondInactiveGray) },
                placeholder = { Text("1995-08-24", color = MicrobondInactiveGray.copy(alpha = 0.5f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submitVerification() }),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MicrobondActiveGreen,
                    unfocusedBorderColor = MicrobondDividerDark,
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    cursorColor = MicrobondActiveGreen,
                    focusedContainerColor = MicrobondOverlayWidgetDark,
                    unfocusedContainerColor = MicrobondOverlayWidgetDark
                )
            )

            // ── Error Message ──
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { error ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .background(MicrobondBottomSheetDark, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MicrobondDestructiveRed, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(text = error, color = MicrobondDestructiveRed, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Submit Verification Button ──
            Button(
                onClick = { submitVerification() },
                enabled = !isLoading && ninInput.length == 14,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MicrobondActiveGreen,
                    contentColor = MicrobondDarkBackground,
                    disabledContainerColor = MicrobondDividerDark,
                    disabledContentColor = MicrobondInactiveGray
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MicrobondDarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Verify & Unlock Groups",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationBenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MicrobondActiveGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = MicrobondTextPrimary,
            fontSize = 13.sp,
            lineHeight = 16.sp
        )
    }
}
