package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateMembershipMeRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.MyAuthorizationData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateMyAuthorizationRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondDragHandleDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondWarningAmber
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private const val MAX_AUTO_APPROVE_CEILING = 500_000L

@Composable
fun MyApprovalAuthorizationScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var authData by remember { mutableStateOf<MyAuthorizationData?>(null) }
    var autoApprove by remember { mutableStateOf(false) }
    var autoApproveAmount by remember { mutableStateOf(50_000L) }
    var autoApproveFormatted by remember { mutableStateOf("50,000 UGX") }

    var showAmountPicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }

    fun formatCurrency(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return "${formatter.format(amount)} UGX"
    }

    fun loadData() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getMyAuthorization(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    authData = data
                    autoApprove = data.autoApproveSmallRequests
                    val parsedAmt = data.autoApproveMaxAmount.toLong().coerceIn(0L, MAX_AUTO_APPROVE_CEILING)
                    autoApproveAmount = if (parsedAmt > 0) parsedAmt else 50_000L
                    autoApproveFormatted = if (parsedAmt > 0) formatCurrency(parsedAmt) else "50,000 UGX"
                } else {
                    // Fallback to membership me
                    val memResp = NetworkClient.apiService.getMyMembership(groupId)
                    if (memResp.isSuccessful && memResp.body()?.data != null) {
                        val mData = memResp.body()!!.data!!
                        autoApprove = mData.autoApproveSmallRequests
                        mData.autoApproveLimit?.let { lim ->
                            autoApproveFormatted = lim
                            val digits = lim.filter { it.isDigit() }.toLongOrNull() ?: 50_000L
                            autoApproveAmount = digits.coerceIn(0L, MAX_AUTO_APPROVE_CEILING)
                        }
                    }
                }
            } catch (e: Exception) {
                // Keep local defaults
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadData()
    }

    fun saveAutoApprove(enabled: Boolean, newAmount: Long? = null) {
        if (groupId.isBlank()) return

        val prevAutoApprove = autoApprove
        val prevAmount = autoApproveAmount
        val prevFormatted = autoApproveFormatted

        val targetAmount = (newAmount ?: autoApproveAmount).coerceIn(0L, MAX_AUTO_APPROVE_CEILING)
        val targetFormatted = formatCurrency(targetAmount)

        autoApprove = enabled
        autoApproveAmount = targetAmount
        autoApproveFormatted = targetFormatted

        scope.launch {
            isSaving = true
            try {
                val req = UpdateMyAuthorizationRequest(
                    autoApproveSmallRequests = enabled,
                    autoApproveMaxAmount = targetAmount.toDouble()
                )
                val resp = NetworkClient.apiService.updateMyAuthorization(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    authData = data
                    autoApprove = data.autoApproveSmallRequests
                    val amt = data.autoApproveMaxAmount.toLong()
                    autoApproveAmount = amt
                    autoApproveFormatted = formatCurrency(amt)
                    snackbarHostState.showSnackbar(
                        if (enabled) "Auto-approval set to $targetFormatted." else "Auto-approval disabled."
                    )
                } else {
                    // Fallback to updateMyMembership
                    val memReq = UpdateMembershipMeRequest(
                        autoApproveSmallRequests = enabled,
                        autoApproveLimit = targetFormatted
                    )
                    val memResp = NetworkClient.apiService.updateMyMembership(groupId, memReq)
                    if (memResp.isSuccessful) {
                        snackbarHostState.showSnackbar(
                            if (enabled) "Auto-approval set to $targetFormatted." else "Auto-approval disabled."
                        )
                    } else {
                        autoApprove = prevAutoApprove
                        autoApproveAmount = prevAmount
                        autoApproveFormatted = prevFormatted
                        val errorText = resp.body()?.message ?: "Failed to update auto-approval settings."
                        snackbarHostState.showSnackbar(errorText)
                    }
                }
            } catch (e: Exception) {
                autoApprove = prevAutoApprove
                autoApproveAmount = prevAmount
                autoApproveFormatted = prevFormatted
                snackbarHostState.showSnackbar("Network error while updating auto-approval.")
            } finally {
                isSaving = false
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SubSettingsTopBar(
                title = "My approval authorization",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isSaving) {
                        CircularProgressIndicator(
                            color = MicrobondActiveGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(18.dp)
                        )
                    }
                }
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Signer Authorization Status Card ──────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = MicrobondOverlayWidgetDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MicrobondActiveGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MicrobondActiveGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAdmin) "Authorized Group Administrator" else "Authorized Treasury Signer",
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Eligible to sign outgoing disbursals and payout queue items.",
                                color = MicrobondInactiveGray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MicrobondDarkBackground.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Auto-Approve", color = MicrobondInactiveGray, fontSize = 11.sp)
                            Text(
                                if (autoApprove) autoApproveFormatted else "Disabled",
                                color = if (autoApprove) MicrobondActiveGreen else MicrobondInactiveGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Hard Ceiling Limit", color = MicrobondInactiveGray, fontSize = 11.sp)
                            Text(
                                "500,000 UGX",
                                color = MicrobondTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ── Auto-Approve Settings Section ──────────────────────────────────
            WaSettingsSectionHeader("Disbursement auto-approval")

            WaSettingsSwitchRow(
                icon = Icons.Default.Autorenew,
                title = "Auto-approve small requests",
                subtitle = "Automatically cast your approval signature for routine payouts up to your configured threshold.",
                checked = autoApprove,
                onCheckedChange = { isChecked ->
                    if (!isSaving) {
                        if (isChecked && autoApproveAmount <= 0) {
                            showAmountPicker = true
                        } else {
                            saveAutoApprove(enabled = isChecked)
                        }
                    }
                }
            )

            if (autoApprove) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.VerifiedUser,
                    title = "Auto-approve threshold amount",
                    value = autoApproveFormatted,
                    onClick = { if (!isSaving) showAmountPicker = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Explanatory guidance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MicrobondInactiveGray,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Payout or disbursement requests up to $autoApproveFormatted will be signed automatically without prompt. Any request above this threshold or exceeding 500,000 UGX strictly requires manual multi-sig signature review.",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showAmountPicker) {
        AutoApproveAmountBottomSheet(
            currentAmount = autoApproveAmount,
            maxCeiling = MAX_AUTO_APPROVE_CEILING,
            onConfirm = { chosenAmount ->
                showAmountPicker = false
                saveAutoApprove(enabled = true, newAmount = chosenAmount)
            },
            onDismiss = { showAmountPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutoApproveAmountBottomSheet(
    currentAmount: Long,
    maxCeiling: Long = MAX_AUTO_APPROVE_CEILING,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textInput by remember { mutableStateOf(if (currentAmount > 0) currentAmount.toString() else "50000") }

    val parsedAmount = textInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val isOverLimit = parsedAmount > maxCeiling
    val isZeroOrEmpty = parsedAmount <= 0L
    val isValid = !isOverLimit && !isZeroOrEmpty

    val presets = remember {
        listOf(
            20_000L to "20,000",
            50_000L to "50,000",
            100_000L to "100,000",
            250_000L to "250,000",
            500_000L to "500,000 (Max)"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MicrobondOverlayWidgetDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MicrobondDragHandleDark) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Auto-approve threshold amount",
                color = MicrobondTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Enter any threshold amount up to 500,000 UGX. Payout requests below this amount are approved automatically.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Numeric input
            OutlinedTextField(
                value = textInput,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    if (filtered.length <= 9) {
                        textInput = filtered
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = {
                    Text(
                        text = "UGX ",
                        color = MicrobondActiveGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MicrobondTextPrimary,
                    unfocusedTextColor = MicrobondTextPrimary,
                    focusedBorderColor = if (isOverLimit) MicrobondDestructiveRed else MicrobondActiveGreen,
                    unfocusedBorderColor = if (isOverLimit) MicrobondDestructiveRed else MicrobondDividerDark,
                    cursorColor = MicrobondActiveGreen,
                    focusedContainerColor = MicrobondDarkBackground,
                    unfocusedContainerColor = MicrobondDarkBackground
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Live validation feedback
            when {
                isOverLimit -> {
                    Text(
                        text = "⚠ Amount cannot exceed 500,000 UGX (Hard treasury limit).",
                        color = MicrobondDestructiveRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                isZeroOrEmpty -> {
                    Text(
                        text = "Please enter an amount between 1 and 500,000 UGX.",
                        color = MicrobondWarningAmber,
                        fontSize = 12.sp
                    )
                }
                else -> {
                    val formattedNum = NumberFormat.getNumberInstance(Locale.US).format(parsedAmount)
                    Text(
                        text = "Requests up to $formattedNum UGX will be auto-approved.",
                        color = MicrobondActiveGreen,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick preset chips
            Text(
                text = "Quick presets",
                color = MicrobondInactiveGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (presetVal, label) ->
                    val isSelected = parsedAmount == presetVal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MicrobondActiveGreen.copy(alpha = 0.2f)
                                else MicrobondDarkBackground
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MicrobondActiveGreen else MicrobondDividerDark,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { textInput = presetVal.toString() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MicrobondActiveGreen else MicrobondTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MicrobondInactiveGray),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MicrobondDividerDark)
                    )
                ) {
                    Text("Cancel", fontSize = 14.sp)
                }

                Button(
                    onClick = { if (isValid) onConfirm(parsedAmount) },
                    enabled = isValid,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MicrobondActiveGreen,
                        disabledContainerColor = MicrobondActiveGreen.copy(alpha = 0.3f),
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Save Limit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
