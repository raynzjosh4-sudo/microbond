package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.TimelockDisbursalsData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateFinancialSettingsRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateTimelockDisbursalsRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import kotlinx.coroutines.launch

@Composable
fun TimelockDisbursalsScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var timelockData by remember { mutableStateOf<TimelockDisbursalsData?>(null) }
    var enableTimelock by remember { mutableStateOf(true) }
    var delayDuration by remember { mutableStateOf("24 Hours") }
    var showDurationPicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }

    val defaultOptions = remember {
        listOf("6 Hours", "12 Hours", "24 Hours (Recommended)", "48 Hours", "72 Hours")
    }

    fun loadData() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getTimelockDisbursals(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    timelockData = data
                    enableTimelock = data.enablePayoutDelay
                    delayDuration = data.coolingDurationDisplay.ifBlank { "${data.coolingDurationHours} Hours" }
                } else {
                    // Fallback to financial settings
                    val finResp = NetworkClient.apiService.getFinancialSettings(groupId)
                    if (finResp.isSuccessful && finResp.body()?.data != null) {
                        val finData = finResp.body()!!.data!!
                        enableTimelock = finData.enableTimelock
                        delayDuration = "${finData.timelockDurationHours} Hours"
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

    fun saveTimelock(newEnable: Boolean? = null, newDurationStr: String? = null) {
        val targetEnable = newEnable ?: enableTimelock
        val targetDuration = newDurationStr ?: delayDuration

        val prevEnable = enableTimelock
        val prevDuration = delayDuration

        enableTimelock = targetEnable
        delayDuration = targetDuration

        if (groupId.isBlank() || !isAdmin) return

        val hours = targetDuration.filter { it.isDigit() }.toIntOrNull() ?: 24

        scope.launch {
            isSaving = true
            try {
                val req = UpdateTimelockDisbursalsRequest(
                    enablePayoutDelay = targetEnable,
                    coolingDurationHours = hours,
                    coolingDuration = targetDuration
                )
                val resp = NetworkClient.apiService.updateTimelockDisbursals(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    timelockData = data
                    enableTimelock = data.enablePayoutDelay
                    delayDuration = data.coolingDurationDisplay
                    snackbarHostState.showSnackbar(
                        if (newDurationStr != null) "Cooling duration updated to $newDurationStr"
                        else if (targetEnable) "Payout delay enabled" else "Payout delay disabled"
                    )
                } else {
                    // Fallback to updateFinancialSettings
                    val finReq = UpdateFinancialSettingsRequest(
                        enableTimelock = targetEnable,
                        timelockDurationHours = hours
                    )
                    val finResp = NetworkClient.apiService.updateFinancialSettings(groupId, finReq)
                    if (finResp.isSuccessful) {
                        snackbarHostState.showSnackbar(
                            if (newDurationStr != null) "Cooling duration updated to $newDurationStr"
                            else if (targetEnable) "Payout delay enabled" else "Payout delay disabled"
                        )
                    } else {
                        enableTimelock = prevEnable
                        delayDuration = prevDuration
                        snackbarHostState.showSnackbar("Failed to update timelock settings")
                    }
                }
            } catch (e: Exception) {
                enableTimelock = prevEnable
                delayDuration = prevDuration
                snackbarHostState.showSnackbar("Network error while updating timelock settings")
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
                title = "Timelock disbursals",
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
            WaSettingsSectionHeader("Fraud prevention")

            WaSettingsSwitchRow(
                icon = Icons.Default.Timer,
                title = "Enable payout delay",
                subtitle = "Force all large transactions to wait a specific period before completing. Gives admins time to cancel fraudulent payouts.",
                checked = enableTimelock,
                onCheckedChange = { if (isAdmin && !isSaving) saveTimelock(newEnable = it) }
            )

            if (enableTimelock) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.HourglassEmpty,
                    title = "Cooling duration",
                    value = delayDuration,
                    onClick = { if (isAdmin && !isSaving) showDurationPicker = true }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Specifies how long approved payouts sit in the pending disbursals queue before mobile money or bank execution.",
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 72.dp, end = 20.dp, bottom = 12.dp)
                )
            }
        }
    }

    if (showDurationPicker) {
        val options = timelockData?.allowedOptions?.map { "$it Hours" } ?: defaultOptions
        WaSettingsOptionsBottomSheet(
            title = "Select cooling duration",
            options = options,
            currentSelection = delayDuration,
            onSelect = { selected ->
                showDurationPicker = false
                saveTimelock(newDurationStr = selected)
            },
            onDismiss = { showDurationPicker = false }
        )
    }
}
