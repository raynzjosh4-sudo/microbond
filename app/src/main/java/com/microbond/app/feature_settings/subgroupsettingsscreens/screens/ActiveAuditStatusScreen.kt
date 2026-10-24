package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun ActiveAuditStatusScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isAuditActive by remember { mutableStateOf(false) }
    var auditTitle by remember { mutableStateOf("No Active Audit") }
    var auditDescription by remember { mutableStateOf("The group's financial records are currently verified and clear.") }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isProcessing by remember { mutableStateOf(false) }
    var showDeliveryScreen by remember { mutableStateOf(false) }

    if (showDeliveryScreen) {
        AuditReportDeliveryScreen(
            groupName = groupName,
            onBack = { showDeliveryScreen = false },
            groupId = groupId,
            isAdmin = isAdmin
        )
        return
    }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val resp = NetworkClient.apiService.getGovernanceSettings(groupId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val data = resp.body()!!.data!!
                isAuditActive = data.isAuditActive
                auditTitle = data.auditStatusTitle
                auditDescription = data.auditStatusDescription
            }
        } catch (e: Exception) {
            // Keep default state
        } finally {
            isLoading = false
        }
    }

    fun triggerStartAudit() {
        if (groupId.isBlank()) {
            isAuditActive = true
            auditTitle = "Active Audit In Progress"
            auditDescription = "Deep-dive review of group transactions is underway. Records verified."
            return
        }
        val prevActive = isAuditActive
        val prevTitle = auditTitle
        val prevDesc = auditDescription

        isAuditActive = true
        auditTitle = "Active Audit In Progress"
        auditDescription = "Deep-dive review of group transactions is underway. Records verified."
        isProcessing = true

        scope.launch {
            try {
                val resp = NetworkClient.apiService.startAudit(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    isAuditActive = data.isAuditActive
                    auditTitle = data.auditStatusTitle
                    auditDescription = data.auditStatusDescription
                    snackbarHostState.showSnackbar("Financial audit initiated successfully.")
                } else {
                    isAuditActive = prevActive
                    auditTitle = prevTitle
                    auditDescription = prevDesc
                    snackbarHostState.showSnackbar("Failed to start audit: ${resp.message()}")
                }
            } catch (e: Exception) {
                isAuditActive = prevActive
                auditTitle = prevTitle
                auditDescription = prevDesc
                snackbarHostState.showSnackbar("Failed to reach server. Please check your connection.")
            } finally {
                isProcessing = false
            }
        }
    }

    fun triggerCompleteAudit() {
        if (groupId.isBlank()) {
            isAuditActive = false
            auditTitle = "No Active Audit"
            auditDescription = "The group's financial records are currently verified and clear."
            return
        }
        val prevActive = isAuditActive
        val prevTitle = auditTitle
        val prevDesc = auditDescription

        isAuditActive = false
        auditTitle = "No Active Audit"
        auditDescription = "The group's financial records are currently verified and clear."
        isProcessing = true

        scope.launch {
            try {
                val resp = NetworkClient.apiService.completeAudit(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    isAuditActive = data.isAuditActive
                    auditTitle = data.auditStatusTitle
                    auditDescription = data.auditStatusDescription
                    snackbarHostState.showSnackbar("Financial audit concluded.")
                } else {
                    isAuditActive = prevActive
                    auditTitle = prevTitle
                    auditDescription = prevDesc
                    snackbarHostState.showSnackbar("Failed to cancel audit: ${resp.message()}")
                }
            } catch (e: Exception) {
                isAuditActive = prevActive
                auditTitle = prevTitle
                auditDescription = prevDesc
                snackbarHostState.showSnackbar("Failed to reach server. Please check your connection.")
            } finally {
                isProcessing = false
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SubSettingsTopBar(
                title = "Active audit status",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isProcessing) {
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
        },
        floatingActionButton = {
            if (!isAuditActive && isAdmin && !isLoading) {
                ExtendedFloatingActionButton(
                    onClick = { triggerStartAudit() },
                    containerColor = MicrobondActiveGreen,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Start Audit") },
                    text = { Text("Start New Audit") }
                )
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(20.dp)
        ) {
            Text(
                text = "Monitor the status of financial audits or initiate a new deep-dive review of group transactions.",
                color = MicrobondInactiveGray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isAuditActive) Color(0xFF2C3E50) else MicrobondOverlayWidgetDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isAuditActive) Icons.Default.Analytics else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isAuditActive) Color(0xFF3498DB) else MicrobondActiveGreen,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 12.dp)
                    )

                    Text(
                        text = auditTitle,
                        color = MicrobondTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = auditDescription,
                        color = MicrobondInactiveGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    if (isAuditActive) {
                        LinearProgressIndicator(
                            color = Color(0xFF3498DB),
                            trackColor = Color(0xFF3498DB).copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { triggerCompleteAudit() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MicrobondDestructiveRed)
                            ) {
                                Text("Cancel Audit")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MicrobondOverlayWidgetDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDeliveryScreen = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MicrobondActiveGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MicrobondActiveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Report Delivery & Compliance",
                            color = MicrobondTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Automated email dispatch, recipients & instant statements",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MicrobondInactiveGray
                    )
                }
            }
        }
    }
}
