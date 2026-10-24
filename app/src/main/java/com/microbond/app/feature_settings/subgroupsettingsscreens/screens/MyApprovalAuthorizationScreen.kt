package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.UpdateMembershipMeRequest
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
fun MyApprovalAuthorizationScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var autoApprove by remember { mutableStateOf(false) }
    var autoApproveLimit by remember { mutableStateOf("50,000 UGX") }
    var showLimitPicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val resp = NetworkClient.apiService.getMyMembership(groupId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val data = resp.body()!!.data!!
                autoApprove = data.autoApproveSmallRequests
                data.autoApproveLimit?.let { autoApproveLimit = it }
            }
        } catch (e: Exception) {
            // Keep defaults
        } finally {
            isLoading = false
        }
    }

    fun onToggleAutoApprove(newVal: Boolean) {
        val prev = autoApprove
        autoApprove = newVal

        if (groupId.isBlank()) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateMembershipMeRequest(autoApproveSmallRequests = newVal)
                val resp = NetworkClient.apiService.updateMyMembership(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    autoApprove = data.autoApproveSmallRequests
                    snackbarHostState.showSnackbar(
                        if (newVal) "Auto-approval enabled" else "Auto-approval disabled"
                    )
                } else {
                    autoApprove = prev
                    snackbarHostState.showSnackbar("Failed to update auto-approval")
                }
            } catch (e: Exception) {
                autoApprove = prev
                snackbarHostState.showSnackbar("Network error while updating auto-approval")
            } finally {
                isSaving = false
            }
        }
    }

    fun onSelectLimit(newLimit: String) {
        val prev = autoApproveLimit
        autoApproveLimit = newLimit
        showLimitPicker = false

        if (groupId.isBlank()) return

        scope.launch {
            isSaving = true
            try {
                val req = UpdateMembershipMeRequest(autoApproveLimit = newLimit)
                val resp = NetworkClient.apiService.updateMyMembership(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    resp.body()!!.data?.autoApproveLimit?.let { autoApproveLimit = it }
                    snackbarHostState.showSnackbar("Threshold limit updated to $newLimit")
                } else {
                    autoApproveLimit = prev
                    snackbarHostState.showSnackbar("Failed to update threshold limit")
                }
            } catch (e: Exception) {
                autoApproveLimit = prev
                snackbarHostState.showSnackbar("Network error while updating threshold limit")
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
                title = "My authorizations",
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
            WaSettingsSectionHeader("Committee / Admin actions")

            WaSettingsSwitchRow(
                icon = Icons.Default.Autorenew,
                title = "Auto-approve small requests",
                subtitle = "Automatically cast your approval vote for loan requests below a certain threshold.",
                checked = autoApprove,
                onCheckedChange = { if (!isSaving) onToggleAutoApprove(it) }
            )

            if (autoApprove) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.VerifiedUser,
                    title = "Auto-approve limit",
                    value = autoApproveLimit,
                    onClick = { if (!isSaving) showLimitPicker = true }
                )
            }
        }
    }

    if (showLimitPicker) {
        WaSettingsOptionsBottomSheet(
            title = "Select auto-approve limit",
            options = listOf("10,000 UGX", "20,000 UGX", "50,000 UGX", "100,000 UGX", "200,000 UGX", "500,000 UGX"),
            currentSelection = autoApproveLimit,
            onSelect = { onSelectLimit(it) },
            onDismiss = { showLimitPicker = false }
        )
    }
}
