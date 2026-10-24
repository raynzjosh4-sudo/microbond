package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.QuorumSettingsData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGovernanceRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateQuorumSettingsRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import kotlinx.coroutines.launch

@Composable
fun ApprovalQuorumScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var quorumData by remember { mutableStateOf<QuorumSettingsData?>(null) }
    var showQuorumPicker by remember { mutableStateOf(false) }
    var quorumPctInt by remember { mutableStateOf(60) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }

    fun loadQuorum() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getQuorumSettings(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    quorumData = data
                    quorumPctInt = data.quorumPercentage
                } else {
                    val govResp = NetworkClient.apiService.getGovernanceSettings(groupId)
                    if (govResp.isSuccessful && govResp.body()?.data != null) {
                        val str = govResp.body()!!.data!!.quorumPercentage
                        quorumPctInt = str.filter { it.isDigit() }.toIntOrNull() ?: 60
                    }
                }
            } catch (e: Exception) {
                // Keep local default
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadQuorum()
    }

    val steps = remember { listOf(0, 20, 40, 60, 80, 100) }

    fun saveQuorum(newPct: Int) {
        val prev = quorumPctInt
        val prevData = quorumData
        quorumPctInt = newPct

        if (groupId.isBlank() || !isAdmin) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateQuorumSettingsRequest(quorumPercentage = newPct)
                val resp = NetworkClient.apiService.updateQuorumSettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    quorumData = resp.body()!!.data!!
                    quorumPctInt = quorumData!!.quorumPercentage
                    snackbarHostState.showSnackbar("Minimum participation updated to $newPct%")
                } else {
                    // Fallback to updateGovernanceSettings
                    val govReq = UpdateGovernanceRequest(quorumPercentage = "$newPct%")
                    val govResp = NetworkClient.apiService.updateGovernanceSettings(groupId, govReq)
                    if (govResp.isSuccessful && govResp.body()?.data != null) {
                        snackbarHostState.showSnackbar("Minimum participation updated to $newPct%")
                    } else {
                        quorumPctInt = prev
                        quorumData = prevData
                        snackbarHostState.showSnackbar("Failed to update quorum")
                    }
                }
            } catch (e: Exception) {
                quorumPctInt = prev
                quorumData = prevData
                snackbarHostState.showSnackbar("Network error while updating quorum")
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
                title = "Approval quorum",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLoading || isSaving) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                        IconButton(onClick = { if (isAdmin && !isSaving) showQuorumPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MicrobondTextPrimary
                            )
                        }
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
            WaSettingsSectionHeader("Voting requirements")

            // ── Minimum participation Header Row ──────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isAdmin && !isSaving) { showQuorumPicker = true }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                BallotBoxCheckIcon(
                    modifier = Modifier
                        .size(28.dp)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Minimum participation",
                            color = MicrobondTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$quorumPctInt%",
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MicrobondInactiveGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Minimum percentage of eligible members that must participate for a vote to be valid.",
                        color = MicrobondInactiveGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Interactive Stepper Slider matching Screenshot 2 ───────────────
            QuorumStepperSlider(
                currentPct = quorumPctInt,
                steps = steps,
                enabled = isAdmin && !isSaving,
                onValueChange = { chosenPct ->
                    saveQuorum(chosenPct)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Dynamic participation message ──────────────────────────────────
            Text(
                text = "At least $quorumPctInt% of eligible members must vote.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Auto-approval Ceiling Section ──────────────────────────────────
            WaSettingsSectionHeader("Disbursement auto-approval")

            WaSettingsValueRow(
                icon = Icons.Default.VerifiedUser,
                title = "Maximum auto-approve ceiling",
                value = quorumData?.autoApproveCeilingFormatted ?: "500,000 UGX"
            )

            Text(
                text = "Signers can auto-approve routine disbursements up to their chosen threshold, strictly capped at 500,000 UGX. Any larger transfer mandates manual multi-signature authorization.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 72.dp, end = 20.dp, bottom = 24.dp)
            )
        }
    }

    if (showQuorumPicker) {
        val options = steps.map { if (it == 100) "100% (Unanimous)" else "$it%" }
        WaSettingsOptionsBottomSheet(
            title = "Select quorum percentage",
            options = options,
            currentSelection = if (quorumPctInt == 100) "100% (Unanimous)" else "$quorumPctInt%",
            onSelect = { selected ->
                val num = selected.filter { it.isDigit() }.toIntOrNull() ?: 60
                showQuorumPicker = false
                saveQuorum(num)
            },
            onDismiss = { showQuorumPicker = false }
        )
    }
}

/** Custom ballot box vector icon with checkmark matching the design. */
@Composable
private fun BallotBoxCheckIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height

        val boxPath = Path().apply {
            moveTo(w * 0.12f, h * 0.32f)
            lineTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.32f)
            lineTo(w * 0.88f, h * 0.88f)
            lineTo(w * 0.12f, h * 0.88f)
            close()
        }
        drawPath(boxPath, color = MicrobondActiveGreen, style = stroke)

        val checkPath = Path().apply {
            moveTo(w * 0.34f, h * 0.52f)
            lineTo(w * 0.46f, h * 0.66f)
            lineTo(w * 0.68f, h * 0.40f)
        }
        drawPath(checkPath, color = MicrobondActiveGreen, style = stroke)
    }
}

/** Discrete stepper slider with stops matching Screenshot 2. */
@Composable
private fun QuorumStepperSlider(
    currentPct: Int,
    steps: List<Int>,
    enabled: Boolean = true,
    onValueChange: (Int) -> Unit
) {
    val numSteps = steps.size
    val selectedIndex = steps.indexOfFirst { it >= currentPct }.let { if (it == -1) numSteps - 1 else it }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { offset ->
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        val stepFraction = 1f / (numSteps - 1)
                        val closest = ((fraction + stepFraction / 2f) / stepFraction).toInt().coerceIn(0, numSteps - 1)
                        onValueChange(steps[closest])
                    }
                }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        val stepFraction = 1f / (numSteps - 1)
                        val closest = ((fraction + stepFraction / 2f) / stepFraction).toInt().coerceIn(0, numSteps - 1)
                        onValueChange(steps[closest])
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2f
                val stepWidth = size.width / (numSteps - 1)
                val activeWidth = selectedIndex * stepWidth

                // Full inactive line
                drawLine(
                    color = Color(0xFF374151),
                    start = Offset(0f, centerY),
                    end = Offset(size.width, centerY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Active green line
                drawLine(
                    color = MicrobondActiveGreen,
                    start = Offset(0f, centerY),
                    end = Offset(activeWidth, centerY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Step dots
                steps.forEachIndexed { index, _ ->
                    val cx = index * stepWidth
                    if (index == selectedIndex) {
                        // Large thumb circle
                        drawCircle(
                            color = MicrobondActiveGreen,
                            radius = 9.dp.toPx(),
                            center = Offset(cx, centerY)
                        )
                    } else {
                        // Small step tick dot
                        val dotColor = if (index < selectedIndex) Color(0xFF94A3B8) else Color(0xFF64748B)
                        drawCircle(
                            color = dotColor,
                            radius = 3.5.dp.toPx(),
                            center = Offset(cx, centerY)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Labels underneath each step
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { step ->
                Text(
                    text = "$step%",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = if (step == steps[selectedIndex]) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
