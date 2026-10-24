package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.TrustMembersData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateTrustMembersRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsOptionsBottomSheet
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow
import kotlinx.coroutines.launch

@Composable
fun LoanGuarantorRulesScreen(
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    BackHandler(onBack = onBack)

    val scope = rememberCoroutineScope()
    var trustData by remember { mutableStateOf<TrustMembersData?>(null) }
    var requireGuarantors by remember { mutableStateOf(false) }
    var guarantorCount by remember { mutableStateOf("2 Members") }
    var visGuarantorRequired by remember { mutableStateOf(false) }
    var minimumTrustScore by remember { mutableStateOf("70 / 100") }

    var showCountPicker by remember { mutableStateOf(false) }
    var showTrustPicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupId) {
        if (groupId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        errorMsg = null
        try {
            val resp = NetworkClient.apiService.getTrustMembersSettings(groupId)
            if (resp.isSuccessful && resp.body()?.data != null) {
                val data = resp.body()!!.data!!
                trustData = data
                requireGuarantors = data.requireGuarantors
                guarantorCount = "${data.guarantorCount} ${if (data.guarantorCount == 1) "Member" else "Members"}"
                visGuarantorRequired = data.visGuarantorRequired
                minimumTrustScore = "${data.minimumTrustScore} / 100"
            }
        } catch (e: Exception) {
            errorMsg = "Failed to load guarantor settings."
        } finally {
            isLoading = false
        }
    }

    fun saveSettings(
        newRequireGuarantors: Boolean? = null,
        newGuarantorCount: Int? = null,
        newVisGuarantorRequired: Boolean? = null,
        newMinimumTrustScore: Int? = null
    ) {
        if (groupId.isBlank() || !isAdmin) return
        scope.launch {
            isSaving = true
            errorMsg = null
            try {
                val req = UpdateTrustMembersRequest(
                    requireGuarantors = newRequireGuarantors,
                    guarantorCount = newGuarantorCount,
                    visGuarantorRequired = newVisGuarantorRequired,
                    minimumTrustScore = newMinimumTrustScore
                )

                val resp = NetworkClient.apiService.updateTrustMembersSettings(groupId, req)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    trustData = data
                    requireGuarantors = data.requireGuarantors
                    guarantorCount = "${data.guarantorCount} ${if (data.guarantorCount == 1) "Member" else "Members"}"
                    visGuarantorRequired = data.visGuarantorRequired
                    minimumTrustScore = "${data.minimumTrustScore} / 100"
                } else {
                    errorMsg = "Failed to update guarantor rules on server."
                }
            } catch (e: Exception) {
                errorMsg = e.localizedMessage ?: "Network error saving settings."
            } finally {
                isSaving = false
            }
        }
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Guarantor rules",
                subtitle = groupName,
                onBack = onBack,
                trailing = {
                    if (isLoading || isSaving) {
                        Box(modifier = Modifier.padding(end = 12.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = MicrobondActiveGreen,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
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
        ) {
            errorMsg?.let { msg ->
                Text(
                    text = "⚠ $msg",
                    color = MicrobondDestructiveRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            WaSettingsSectionHeader("Co-signers & Collateral")

            WaSettingsSwitchRow(
                icon = Icons.Default.Handshake,
                title = "Require guarantors",
                subtitle = "Require a guarantor before selected benefits, payouts, or loans are available.",
                checked = requireGuarantors,
                onCheckedChange = {
                    requireGuarantors = it
                    saveSettings(newRequireGuarantors = it)
                }
            )

            if (requireGuarantors) {
                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.Group,
                    title = "Minimum guarantors",
                    value = guarantorCount,
                    onClick = { if (isAdmin) showCountPicker = true }
                )

                WaSettingsDivider()

                WaSettingsValueRow(
                    icon = Icons.Default.Shield,
                    title = "Minimum trust score required",
                    value = minimumTrustScore,
                    onClick = { if (isAdmin) showTrustPicker = true }
                )

                WaSettingsDivider()

                WaSettingsSwitchRow(
                    icon = Icons.Default.Visibility,
                    title = "Visible to members",
                    subtitle = if (visGuarantorRequired) "Members can see the guarantor policy in settings." else "Hidden from members until finalized.",
                    checked = visGuarantorRequired,
                    onCheckedChange = {
                        visGuarantorRequired = it
                        saveSettings(newVisGuarantorRequired = it)
                    }
                )
            }

            if (!isAdmin) {
                Text(
                    text = "Managed by group admins.",
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }

    if (showCountPicker) {
        val options = listOf("1 Member", "2 Members", "3 Members", "4 Members", "5 Members")
        WaSettingsOptionsBottomSheet(
            title = "Select minimum guarantors",
            options = options,
            currentSelection = guarantorCount,
            onSelect = { selected ->
                guarantorCount = selected
                showCountPicker = false
                val countInt = selected.take(1).toIntOrNull() ?: 2
                saveSettings(newGuarantorCount = countInt)
            },
            onDismiss = { showCountPicker = false }
        )
    }

    if (showTrustPicker) {
        val options = listOf("50 / 100", "60 / 100", "70 / 100", "80 / 100", "90 / 100")
        WaSettingsOptionsBottomSheet(
            title = "Select minimum trust score",
            options = options,
            currentSelection = minimumTrustScore,
            onSelect = { selected ->
                minimumTrustScore = selected
                showTrustPicker = false
                val scoreInt = selected.substringBefore(" /").trim().toIntOrNull() ?: 70
                saveSettings(newMinimumTrustScore = scoreInt)
            },
            onDismiss = { showTrustPicker = false }
        )
    }
}
