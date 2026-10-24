package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGovernanceRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateMultisigRosterRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultisignatureSignerRosterScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var requiredSignersDisplay by remember { mutableStateOf("") }
    var totalSignersCount by remember { mutableIntStateOf(0) }
    var currentApprovals by remember { mutableIntStateOf(0) }

    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }
    var showRatioPicker by remember { mutableStateOf(false) }
    var showManageSigners by remember { mutableStateOf(false) }

    fun reload() {
        if (groupId.isBlank()) {
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val resp = NetworkClient.apiService.getMultisigRoster(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    requiredSignersDisplay = data.display
                    totalSignersCount = data.totalSigners
                    currentApprovals = data.requiredApprovals
                } else {
                    val govResp = NetworkClient.apiService.getGovernanceSettings(groupId)
                    if (govResp.isSuccessful && govResp.body()?.data != null) {
                        val data = govResp.body()!!.data!!
                        requiredSignersDisplay = data.signersDisplay
                        totalSignersCount = data.totalSignersCount
                        currentApprovals = data.multiSigRequiredApprovals
                    }
                }
            } catch (e: Exception) {
                // Keep current state
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(groupId) {
        reload()
    }

    if (showManageSigners) {
        ManageAuthorizedSignersScreen(
            groupName = groupName,
            onBack = {
                showManageSigners = false
                reload()
            },
            groupId = groupId,
            isAdmin = isAdmin
        )
        return
    }

    val ratioOptions = remember(totalSignersCount) {
        if (totalSignersCount < 1) emptyList()
        else (1..totalSignersCount).map { "$it of $totalSignersCount Signers" }
    }

    fun onSelectRatio(option: String) {
        val prevDisplay = requiredSignersDisplay
        val prevApprovals = currentApprovals
        val selectedNum = option.substringBefore(" of").toIntOrNull() ?: return

        requiredSignersDisplay = option
        currentApprovals = selectedNum
        showRatioPicker = false

        if (groupId.isBlank() || !isAdmin) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateMultisigRosterRequest(requiredApprovals = selectedNum)
                val resp = NetworkClient.apiService.updateMultisigRoster(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    requiredSignersDisplay = data.display
                    totalSignersCount = data.totalSigners
                    currentApprovals = data.requiredApprovals
                    snackbarHostState.showSnackbar("Required approvals updated to $option")
                } else {
                    // Fallback to update governance
                    val govReq = UpdateGovernanceRequest(multiSigRequiredApprovals = selectedNum)
                    val govResp = NetworkClient.apiService.updateGovernanceSettings(groupId, govReq)
                    if (govResp.isSuccessful && govResp.body()?.data != null) {
                        val data = govResp.body()!!.data!!
                        requiredSignersDisplay = data.signersDisplay
                        totalSignersCount = data.totalSignersCount
                        currentApprovals = data.multiSigRequiredApprovals
                        snackbarHostState.showSnackbar("Required approvals updated to $option")
                    } else {
                        requiredSignersDisplay = prevDisplay
                        currentApprovals = prevApprovals
                        snackbarHostState.showSnackbar("Failed to update required approvals")
                    }
                }
            } catch (e: Exception) {
                requiredSignersDisplay = prevDisplay
                currentApprovals = prevApprovals
                snackbarHostState.showSnackbar("Network error while updating approvals")
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
                title = "Multisig roster",
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
            WaSettingsSectionHeader("Treasury protection")

            WaSettingsValueRow(
                icon = Icons.Default.Key,
                title = "Required approvals",
                value = requiredSignersDisplay.ifBlank { if (isLoading) "Loading..." else "1 of 1 Signers" },
                onClick = { if (isAdmin && !isSaving && ratioOptions.isNotEmpty()) showRatioPicker = true }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.PersonAdd,
                title = "Manage authorized signers",
                subtitle = "Add or remove admins who hold the cryptographic keys required to disburse funds.",
                onClick = { showManageSigners = true }
            )
        }
    }

    if (showRatioPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select required approvals",
            options = ratioOptions,
            currentSelection = requiredSignersDisplay,
            onSelect = { onSelectRatio(it) },
            onDismiss = { showRatioPicker = false }
        )
    }
}

