package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.avatar.MicroBondAvatar
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.PendingDisbursalsData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.PendingPayoutItemDto
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdatePendingDisbursalsRequest
import com.microbond.app.core.ui.theme.AvatarColorHelper
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.core.ui.theme.MicrobondWarningAmber
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import kotlinx.coroutines.launch

@Composable
fun PendingDisbursalsScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true,
    pageTitle: String = "Pending disbursals"
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var queueData by remember { mutableStateOf<PendingDisbursalsData?>(null) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }
    var showLimitPicker by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun loadData() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        errorMsg = null
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getPendingDisbursals(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    queueData = resp.body()!!.data!!
                } else {
                    errorMsg = resp.body()?.message ?: "Unable to load pending disbursals."
                }
            } catch (e: Exception) {
                errorMsg = "Network error loading queue data."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadData()
    }

    fun updateSettings(newMax: Int? = null, newVis: Boolean? = null) {
        if (groupId.isBlank() || !isAdmin) return

        val prevData = queueData
        val currentMax = newMax ?: queueData?.maxPendingPayouts ?: 2
        val currentVis = newVis ?: queueData?.visPendingPayoutQueue ?: true

        queueData = queueData?.copy(
            maxPendingPayouts = currentMax,
            visPendingPayoutQueue = currentVis
        )

        scope.launch {
            isSaving = true
            try {
                val req = UpdatePendingDisbursalsRequest(
                    maxPendingPayouts = newMax,
                    visPendingPayoutQueue = newVis
                )
                val resp = NetworkClient.apiService.updatePendingDisbursals(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    queueData = resp.body()!!.data!!
                    snackbarHostState.showSnackbar("Queue settings updated.")
                } else {
                    queueData = prevData
                    snackbarHostState.showSnackbar("Failed to update: ${resp.message()}")
                }
            } catch (e: Exception) {
                queueData = prevData
                snackbarHostState.showSnackbar("Network error updating settings.")
            } finally {
                isSaving = false
            }
        }
    }

    val maxOptions = remember {
        listOf("1 Payout", "2 Payouts (Recommended)", "3 Payouts", "4 Payouts", "5 Payouts")
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SubSettingsTopBar(
                title = pageTitle,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            // ── Circuit Breaker Status Banner ──────────────────────────────────
            item {
                val isFrozen = queueData?.isQueueFull == true
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFrozen) Color(0xFF2A1515) else MicrobondOverlayWidgetDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isFrozen) MicrobondDestructiveRed.copy(alpha = 0.2f)
                                        else MicrobondActiveGreen.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFrozen) Icons.Default.Warning else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (isFrozen) MicrobondDestructiveRed else MicrobondActiveGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isFrozen) "Circuit Breaker: FROZEN" else "Solvency Circuit Breaker",
                                    color = if (isFrozen) MicrobondDestructiveRed else MicrobondTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = queueData?.circuitBreakerStatus ?: "NORMAL (Safe)",
                                    color = if (isFrozen) MicrobondDestructiveRed else MicrobondActiveGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Protects group solvency by pausing new cycle collections whenever unresolved payouts reach the queue limit, preventing cascade defaults and allowing multi-sig signers to verify funds.",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MicrobondDarkBackground.copy(alpha = 0.6f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Active Pending", color = MicrobondInactiveGray, fontSize = 11.sp)
                                Text(
                                    "${queueData?.pendingDisbursalsCount ?: 0} Payouts",
                                    color = MicrobondTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column {
                                Text("Queue Limit", color = MicrobondInactiveGray, fontSize = 11.sp)
                                Text(
                                    "${queueData?.maxPendingPayouts ?: 2} Max",
                                    color = MicrobondTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column {
                                Text("Member Visibility", color = MicrobondInactiveGray, fontSize = 11.sp)
                                Text(
                                    if (queueData?.visPendingPayoutQueue == true) "Visible" else "Admin Only",
                                    color = if (queueData?.visPendingPayoutQueue == true) MicrobondActiveGreen else MicrobondWarningAmber,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // ── Error notice ───────────────────────────────────────────────────
            if (errorMsg != null) {
                item {
                    Text(
                        text = "⚠ $errorMsg",
                        color = MicrobondDestructiveRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // ── Queue Controls (Admin only) ────────────────────────────────────
            if (isAdmin) {
                item {
                    WaSettingsSectionHeader("Queue controls")

                    WaSettingsValueRow(
                        icon = Icons.Default.Tune,
                        title = "Max pending before freeze",
                        value = "${queueData?.maxPendingPayouts ?: 2} Payouts",
                        onClick = { if (!isSaving) showLimitPicker = true }
                    )

                    WaSettingsDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Visible to regular members",
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Allow group members to view the active pending queue status.",
                                color = MicrobondInactiveGray,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = queueData?.visPendingPayoutQueue ?: true,
                            onCheckedChange = { updateSettings(newVis = it) },
                            enabled = !isSaving,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MicrobondActiveGreen,
                                uncheckedThumbColor = MicrobondInactiveGray,
                                uncheckedTrackColor = MicrobondOverlayWidgetDark
                            )
                        )
                    }

                    WaSettingsDivider()
                }
            }

            // ── Itemized Queued Payouts Section ────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(10.dp))
                WaSettingsSectionHeader("Active queued payouts (${queueData?.payouts?.size ?: 0})")
            }

            val payoutsList = queueData?.payouts.orEmpty()
            if (payoutsList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MicrobondOverlayWidgetDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MicrobondActiveGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No payouts currently pending",
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "All scheduled payouts have been processed or are in good standing.",
                                color = MicrobondInactiveGray,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(payoutsList, key = { it.id }) { item ->
                    QueuedPayoutCard(item = item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    if (showLimitPicker) {
        val currentSelection = "${queueData?.maxPendingPayouts ?: 2} Payouts"
        WaSettingsOptionsBottomSheet(
            title = "Select max pending limit",
            options = maxOptions,
            currentSelection = if (queueData?.maxPendingPayouts == 2) "2 Payouts (Recommended)" else currentSelection,
            onSelect = { selected ->
                val num = selected.filter { it.isDigit() }.toIntOrNull() ?: 2
                showLimitPicker = false
                updateSettings(newMax = num)
            },
            onDismiss = { showLimitPicker = false }
        )
    }
}

@Composable
private fun QueuedPayoutCard(item: PendingPayoutItemDto) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MicrobondOverlayWidgetDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MicroBondAvatar(
                    avatarUrl = item.recipientAvatar,
                    name = item.recipientName,
                    backgroundColor = AvatarColorHelper.getColor(item.userId),
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.recipientName,
                            color = MicrobondTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MicrobondActivePillDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Cycle ${item.cycleNumber}",
                                color = MicrobondActiveGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (item.recipientPhone.isNotBlank()) {
                        Text(
                            text = item.recipientPhone,
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.amountExpectedFormatted,
                    color = MicrobondActiveGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                val (badgeBg, badgeText, badgeIcon) = when (item.status.uppercase()) {
                    "ON_HOLD" -> Triple(
                        MicrobondDestructiveRed.copy(alpha = 0.2f),
                        MicrobondDestructiveRed,
                        Icons.Default.PauseCircle
                    )
                    "PROCESSING" -> Triple(
                        MicrobondActiveGreen.copy(alpha = 0.2f),
                        MicrobondActiveGreen,
                        Icons.Default.HourglassTop
                    )
                    else -> Triple(
                        MicrobondWarningAmber.copy(alpha = 0.2f),
                        MicrobondWarningAmber,
                        Icons.Default.HourglassTop
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = badgeText,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.status.replace("_", " "),
                        color = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Scheduled: ${item.payoutDateFormatted}",
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp
                )
            }

            if (!item.holdReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2E1A1A))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Hold Reason: ${item.holdReason}",
                        color = MicrobondDestructiveRed,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
