package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.EscrowLockData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateEscrowLockRequest
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
fun EscrowLockScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var escrowData by remember { mutableStateOf<EscrowLockData?>(null) }
    var enableEscrow by remember { mutableStateOf(true) }
    var releaseCondition by remember { mutableStateOf("Upon unanimous approval") }
    var showConditionPicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }

    val defaultConditions = remember {
        listOf(
            "Upon unanimous approval",
            "Majority Vote",
            "Timelock or Consensus",
            "Milestone and Consensus"
        )
    }

    fun loadSettings() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getEscrowLockSettings(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    escrowData = data
                    enableEscrow = data.enableEscrow
                    releaseCondition = data.releaseCondition.ifBlank { data.escrowReleaseCondition }
                }
            } catch (e: Exception) {
                // Keep local defaults
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        loadSettings()
    }

    fun saveSettings(newEnable: Boolean? = null, newCondition: String? = null) {
        val targetEnable = newEnable ?: enableEscrow
        val targetCondition = newCondition ?: releaseCondition

        val prevEnable = enableEscrow
        val prevCondition = releaseCondition

        enableEscrow = targetEnable
        releaseCondition = targetCondition

        if (groupId.isBlank() || !isAdmin) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateEscrowLockRequest(
                    enableEscrow = targetEnable,
                    escrowReleaseCondition = targetCondition
                )
                val resp = NetworkClient.apiService.updateEscrowLockSettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    escrowData = data
                    enableEscrow = data.enableEscrow
                    releaseCondition = data.releaseCondition.ifBlank { data.escrowReleaseCondition }
                    snackbarHostState.showSnackbar(
                        if (newCondition != null) "Release condition updated to $newCondition"
                        else if (targetEnable) "Escrow lock enabled" else "Escrow lock disabled"
                    )
                } else {
                    enableEscrow = prevEnable
                    releaseCondition = prevCondition
                    snackbarHostState.showSnackbar("Failed to update escrow settings")
                }
            } catch (e: Exception) {
                enableEscrow = prevEnable
                releaseCondition = prevCondition
                snackbarHostState.showSnackbar("Network error while updating escrow settings")
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
                title = "Escrow lock",
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
            WaSettingsSectionHeader("Fund Protection")

            WaSettingsSwitchRow(
                icon = Icons.Default.Security,
                title = "Hold funds in escrow",
                subtitle = "Contributions are temporarily locked in a smart escrow vault until release conditions are met.",
                checked = enableEscrow,
                onCheckedChange = { if (isAdmin && !isSaving) saveSettings(newEnable = it) }
            )

            if (enableEscrow) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.LockClock,
                    title = "Release condition",
                    value = releaseCondition,
                    onClick = { if (isAdmin && !isSaving) showConditionPicker = true }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Specifies the consensus or verification milestone required before group funds unlock from escrow.",
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 72.dp, end = 20.dp, bottom = 12.dp)
                )
            }
        }
    }

    if (showConditionPicker) {
        val conditions = escrowData?.availableReleaseConditions ?: defaultConditions
        WaSettingsOptionsBottomSheet(
            title = "Select release condition",
            options = conditions,
            currentSelection = releaseCondition,
            onSelect = { selected ->
                showConditionPicker = false
                saveSettings(newCondition = selected)
            },
            onDismiss = { showConditionPicker = false }
        )
    }
}
