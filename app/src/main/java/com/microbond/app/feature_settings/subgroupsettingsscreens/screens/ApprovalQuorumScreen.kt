package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.QuorumSettingsData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGovernanceRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateQuorumSettingsRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
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
    var quorumPercentage by remember { mutableStateOf("60%") }
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
                    quorumPercentage = data.minimumParticipation
                } else {
                    val govResp = NetworkClient.apiService.getGovernanceSettings(groupId)
                    if (govResp.isSuccessful && govResp.body()?.data != null) {
                        quorumPercentage = govResp.body()!!.data!!.quorumPercentage
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

    val options = remember(quorumData?.allowedOptions) {
        quorumData?.allowedOptions?.map {
            if (it == 100) "100% (Unanimous)" else "$it%"
        } ?: listOf("50%", "60%", "66%", "75%", "80%", "100% (Unanimous)")
    }

    fun onSelectQuorum(selected: String) {
        val prev = quorumPercentage
        val prevData = quorumData
        val pctNum = selected.filter { it.isDigit() }.toIntOrNull() ?: 60
        val displayPct = if (selected.contains("%")) selected.substringBefore(" (") else "$pctNum%"

        quorumPercentage = displayPct
        showQuorumPicker = false

        if (groupId.isBlank() || !isAdmin) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateQuorumSettingsRequest(quorumPercentage = pctNum)
                val resp = NetworkClient.apiService.updateQuorumSettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    quorumData = data
                    quorumPercentage = data.minimumParticipation
                    snackbarHostState.showSnackbar("Minimum participation updated to $displayPct")
                } else {
                    // Fallback to updateGovernanceSettings
                    val govReq = UpdateGovernanceRequest(quorumPercentage = displayPct)
                    val govResp = NetworkClient.apiService.updateGovernanceSettings(groupId, govReq)
                    if (govResp.isSuccessful && govResp.body()?.data != null) {
                        quorumPercentage = govResp.body()!!.data!!.quorumPercentage
                        snackbarHostState.showSnackbar("Minimum participation updated to $displayPct")
                    } else {
                        quorumPercentage = prev
                        quorumData = prevData
                        snackbarHostState.showSnackbar("Failed to update quorum")
                    }
                }
            } catch (e: Exception) {
                quorumPercentage = prev
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
            WaSettingsSectionHeader("Voting requirements")

            WaSettingsValueRow(
                icon = Icons.Default.HowToVote,
                title = "Minimum participation",
                value = quorumPercentage,
                onClick = { if (isAdmin && !isSaving) showQuorumPicker = true }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Set the minimum percentage of eligible members that must participate in a vote for it to be considered valid.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 72.dp, end = 20.dp, bottom = 12.dp)
            )

            val exampleText = quorumData?.exampleText?.ifBlank { null }
                ?: "Example: If set to $quorumPercentage, at least $quorumPercentage of eligible members must cast a vote for any resolution to pass."

            Text(
                text = exampleText,
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 72.dp, end = 20.dp, bottom = 24.dp)
            )

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
        WaSettingsOptionsBottomSheet(
            title = "Select quorum percentage",
            options = options,
            currentSelection = if (quorumPercentage == "100%") "100% (Unanimous)" else quorumPercentage,
            onSelect = { onSelectQuorum(it) },
            onDismiss = { showQuorumPicker = false }
        )
    }
}
