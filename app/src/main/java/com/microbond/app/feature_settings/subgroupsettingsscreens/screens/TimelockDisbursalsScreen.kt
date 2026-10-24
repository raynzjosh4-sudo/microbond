package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateFinancialSettingsRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
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

    var enableTimelock by remember { mutableStateOf(true) }
    var delayDuration by remember { mutableStateOf("24 Hours") }
    var showDurationPicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val resp = NetworkClient.apiService.getFinancialSettings(groupId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val data = resp.body()!!.data!!
                enableTimelock = data.enableTimelock
                delayDuration = when (data.timelockDurationHours) {
                    12 -> "12 Hours"
                    24 -> "24 Hours"
                    48 -> "48 Hours"
                    72 -> "72 Hours"
                    168 -> "1 Week"
                    else -> "${data.timelockDurationHours} Hours"
                }
            }
        } catch (e: Exception) {
            // Keep default
        } finally {
            isLoading = false
        }
    }

    fun onToggleTimelock(newVal: Boolean) {
        val prev = enableTimelock
        enableTimelock = newVal

        if (groupId.isBlank() || !isAdmin) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateFinancialSettingsRequest(enableTimelock = newVal)
                val resp = NetworkClient.apiService.updateFinancialSettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    enableTimelock = resp.body()!!.data!!.enableTimelock
                    snackbarHostState.showSnackbar(
                        if (newVal) "Payout delay enabled" else "Payout delay disabled"
                    )
                } else {
                    enableTimelock = prev
                    snackbarHostState.showSnackbar("Failed to update payout delay")
                }
            } catch (e: Exception) {
                enableTimelock = prev
                snackbarHostState.showSnackbar("Network error while updating payout delay")
            } finally {
                isSaving = false
            }
        }
    }

    fun onSelectDuration(selectedOption: String) {
        val prev = delayDuration
        delayDuration = selectedOption
        showDurationPicker = false

        if (groupId.isBlank() || !isAdmin) return

        val hours = when (selectedOption) {
            "12 Hours" -> 12
            "24 Hours" -> 24
            "48 Hours" -> 48
            "72 Hours" -> 72
            "1 Week" -> 168
            else -> 24
        }

        scope.launch {
            isSaving = true
            try {
                val req = UpdateFinancialSettingsRequest(timelockDurationHours = hours)
                val resp = NetworkClient.apiService.updateFinancialSettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    delayDuration = when (data.timelockDurationHours) {
                        12 -> "12 Hours"
                        24 -> "24 Hours"
                        48 -> "48 Hours"
                        72 -> "72 Hours"
                        168 -> "1 Week"
                        else -> "${data.timelockDurationHours} Hours"
                    }
                    snackbarHostState.showSnackbar("Cooling duration updated to $selectedOption")
                } else {
                    delayDuration = prev
                    snackbarHostState.showSnackbar("Failed to update cooling duration")
                }
            } catch (e: Exception) {
                delayDuration = prev
                snackbarHostState.showSnackbar("Network error while updating cooling duration")
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
        ) {
            WaSettingsSectionHeader("Fraud prevention")

            WaSettingsSwitchRow(
                icon = Icons.Default.Timer,
                title = "Enable payout delay",
                subtitle = "Force all large transactions to wait a specific period before completing. Gives admins time to cancel fraudulent payouts.",
                checked = enableTimelock,
                onCheckedChange = { if (!isSaving) onToggleTimelock(it) }
            )

            if (enableTimelock) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.HourglassEmpty,
                    title = "Cooling duration",
                    value = delayDuration,
                    onClick = { if (isAdmin && !isSaving) showDurationPicker = true }
                )
            }
        }
    }

    if (showDurationPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select cooling duration",
            options = listOf("12 Hours", "24 Hours", "48 Hours", "72 Hours", "1 Week"),
            currentSelection = delayDuration,
            onSelect = { onSelectDuration(it) },
            onDismiss = { showDurationPicker = false }
        )
    }
}
