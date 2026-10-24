package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.AuditDeliverySettingsData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.ExportFinancialReportRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateAuditDeliverySettingsRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import kotlinx.coroutines.launch

@Composable
fun AuditReportDeliveryScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var settingsData by remember { mutableStateOf<AuditDeliverySettingsData?>(null) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    var automatedDispatch by remember { mutableStateOf(false) }
    var recipientEmail by remember { mutableStateOf("") }
    var monthlyCompliance by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("PDF") }
    var defaultAdminEmail by remember { mutableStateOf<String?>(null) }

    fun loadSettings() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getAuditReportDeliverySettings(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    settingsData = data
                    automatedDispatch = data.enableAutomatedAuditDispatch
                    recipientEmail = data.auditReportRecipientEmail.orEmpty()
                    monthlyCompliance = data.dispatchMonthlyComplianceStatement
                    exportFormat = data.exportFileFormat
                    defaultAdminEmail = data.defaultAdminEmail
                }
            } catch (e: Exception) {
                // Keep local state
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadSettings()
    }

    fun saveSettings(
        newAutomated: Boolean = automatedDispatch,
        newEmail: String = recipientEmail,
        newMonthly: Boolean = monthlyCompliance,
        newFormat: String = exportFormat
    ) {
        if (groupId.isBlank() || !isAdmin) return

        automatedDispatch = newAutomated
        recipientEmail = newEmail
        monthlyCompliance = newMonthly
        exportFormat = newFormat

        scope.launch {
            isSaving = true
            try {
                val req = UpdateAuditDeliverySettingsRequest(
                    enableAutomatedAuditDispatch = newAutomated,
                    auditReportRecipientEmail = newEmail.trim().ifBlank { null },
                    dispatchMonthlyComplianceStatement = newMonthly,
                    exportFileFormat = newFormat
                )
                val resp = NetworkClient.apiService.updateAuditReportDeliverySettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    settingsData = data
                    automatedDispatch = data.enableAutomatedAuditDispatch
                    recipientEmail = data.auditReportRecipientEmail.orEmpty()
                    monthlyCompliance = data.dispatchMonthlyComplianceStatement
                    exportFormat = data.exportFileFormat
                    snackbarHostState.showSnackbar("Audit delivery settings updated.")
                } else {
                    snackbarHostState.showSnackbar("Failed to update settings: ${resp.message()}")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Network error updating audit delivery.")
            } finally {
                isSaving = false
            }
        }
    }

    fun exportAndEmailNow() {
        if (groupId.isBlank() || !isAdmin) return
        val target = recipientEmail.trim()
        if (target.isBlank() && defaultAdminEmail.isNullOrBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Please enter a recipient email first.")
            }
            return
        }

        focusManager.clearFocus()
        scope.launch {
            isExporting = true
            try {
                val req = ExportFinancialReportRequest(
                    exportFormat = exportFormat,
                    recipientEmail = target.ifBlank { null }
                )
                val resp = NetworkClient.apiService.exportAndEmailFinancialReport(groupId, req)
                if (resp.isSuccessful) {
                    val msg = resp.body()?.message ?: "Compliance audit statement compiled and emailed."
                    snackbarHostState.showSnackbar(msg)
                } else {
                    snackbarHostState.showSnackbar("Export failed: ${resp.message()}")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Network error dispatching report.")
            } finally {
                isExporting = false
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SubSettingsTopBar(
                title = "Audit report delivery",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isSaving || isExporting) {
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
            // Hero info banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MicrobondOverlayWidgetDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MicrobondActiveGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MicrobondActiveGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Audit Report Delivery & Compliance",
                                color = MicrobondTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Double-entry reconciliation dispatch",
                                color = MicrobondActiveGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Automate the dispatch of verified double-entry ledgers to external regulators, banking partners, or internal audit committees to maintain zero-trust transparency.",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            // Section 1: Automated Dispatch Automation
            WaSettingsSectionHeader("Automated dispatch")

            // Toggle 1: Automated Audit Dispatch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Automated audit dispatch",
                        color = MicrobondTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Automatically email signed ledger records when an audit concludes.",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = automatedDispatch,
                    onCheckedChange = { if (isAdmin && !isSaving) saveSettings(newAutomated = it) },
                    enabled = isAdmin && !isSaving,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MicrobondActiveGreen,
                        uncheckedThumbColor = MicrobondInactiveGray,
                        uncheckedTrackColor = MicrobondOverlayWidgetDark
                    )
                )
            }

            WaSettingsDivider()

            // Toggle 2: Monthly Compliance Statement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Monthly compliance statement",
                        color = MicrobondTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Compile and email reconciliation statements on the 1st of each month.",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = monthlyCompliance,
                    onCheckedChange = { if (isAdmin && !isSaving) saveSettings(newMonthly = it) },
                    enabled = isAdmin && !isSaving,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MicrobondActiveGreen,
                        uncheckedThumbColor = MicrobondInactiveGray,
                        uncheckedTrackColor = MicrobondOverlayWidgetDark
                    )
                )
            }

            WaSettingsDivider()

            // Section 2: Recipient Configuration
            Spacer(modifier = Modifier.height(8.dp))
            WaSettingsSectionHeader("Recipient email")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = recipientEmail,
                    onValueChange = { recipientEmail = it },
                    label = { Text("Auditor / Regulatory Recipient Email") },
                    placeholder = { Text("compliance@sacco.org or partner bank") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = MicrobondInactiveGray)
                    },
                    singleLine = true,
                    enabled = isAdmin && !isSaving,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            saveSettings(newEmail = recipientEmail)
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        focusedBorderColor = MicrobondActiveGreen,
                        unfocusedBorderColor = MicrobondInactiveGray.copy(alpha = 0.3f),
                        focusedContainerColor = MicrobondOverlayWidgetDark,
                        unfocusedContainerColor = MicrobondOverlayWidgetDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!defaultAdminEmail.isNullOrBlank() && recipientEmail != defaultAdminEmail) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Use admin email ($defaultAdminEmail)",
                        color = MicrobondActiveGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                recipientEmail = defaultAdminEmail.orEmpty()
                                saveSettings(newEmail = recipientEmail)
                            }
                            .padding(vertical = 4.dp)
                    )
                }

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            saveSettings(newEmail = recipientEmail)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MicrobondActiveGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Recipient Email", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            WaSettingsDivider()

            // Section 3: Export Format Selection
            Spacer(modifier = Modifier.height(8.dp))
            WaSettingsSectionHeader("Export file format")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("PDF", "CSV").forEach { fmt ->
                    val isSelected = exportFormat.equals(fmt, ignoreCase = true)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MicrobondActiveGreen.copy(alpha = 0.15f) else MicrobondOverlayWidgetDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MicrobondActiveGreen) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = isAdmin && !isSaving) {
                                saveSettings(newFormat = fmt)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (fmt == "PDF") Icons.Default.PictureAsPdf else Icons.Default.TableChart,
                                contentDescription = null,
                                tint = if (isSelected) MicrobondActiveGreen else MicrobondInactiveGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = fmt,
                                color = if (isSelected) MicrobondActiveGreen else MicrobondTextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            WaSettingsDivider()

            // Section 4: On-Demand Compilation & Email Action
            Spacer(modifier = Modifier.height(8.dp))
            WaSettingsSectionHeader("Instant export & email")

            Card(
                colors = CardDefaults.cardColors(containerColor = MicrobondOverlayWidgetDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Compile Double-Entry Ledger Now",
                        color = MicrobondTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generates the complete ledger including itemized member contributions, disbursed payouts, and vault reconciliation, and emails it immediately as a $exportFormat attachment.",
                        color = MicrobondInactiveGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { exportAndEmailNow() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MicrobondActiveGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isAdmin && !isExporting && !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compiling Statement...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export & Email Statement", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
