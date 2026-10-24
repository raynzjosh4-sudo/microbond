package com.microbond.app.feature_settings.subgroupsettingsscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GovernanceSettingsData
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGoalControlsRequest
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGovernanceRequest
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDestructiveRed
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupSettingsAccessState
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.SettingAccess
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.audienceHint
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.visibleSettings
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.pageDescription
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.pageTitle
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.GroupSettingActionRow
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.GroupSettingToggleRow
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.GroupTypeStrip
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.MemberViewNotice
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import kotlinx.coroutines.launch

/** Renders one settings page, filtered by the viewer's [viewerRole]. */
@Composable
fun GroupSubSettingsScreen(
    groupName: String,
    memberCount: Int,
    groupType: SavingsGroupType,
    page: GroupSettingsPage,
    viewerRole: GroupMemberRole,
    accessState: GroupSettingsAccessState,
    onBack: () -> Unit,
    onNavigateToSetting: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    groupId: String = ""
) {
    val rows = accessState.visibleSettings(groupType, page, viewerRole)
    val isGoalControls = groupType == SavingsGroupType.GOAL_SAVINGS && page == GroupSettingsPage.TYPE_RULES
    val isGovernance = page == GroupSettingsPage.GOVERNANCE
    val scope = rememberCoroutineScope()

    var governanceData by remember { mutableStateOf<GovernanceSettingsData?>(null) }
    var isLoading by remember { mutableStateOf((isGoalControls || isGovernance) && groupId.isNotBlank()) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupId, isGoalControls, isGovernance) {
        if ((!isGoalControls && !isGovernance) || groupId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        errorMsg = null
        try {
            if (isGoalControls) {
                val resp = NetworkClient.apiService.getGoalControls(groupId)
                if (resp.isSuccessful && resp.body() != null) {
                    val data = resp.body()!!.data
                    if (data != null) {
                        rows.forEach { row ->
                            when (row.setting.title) {
                                "Lock funds" -> accessState.setToggle(row.setting, data.lockFunds)
                                "Hide leaderboard" -> accessState.setToggle(row.setting, data.hideLeaderboard)
                                "Let members view their total saved" -> accessState.setToggle(row.setting, data.letMembersViewTotalSaved)
                            }
                        }
                    }
                }
            } else if (isGovernance) {
                val resp = NetworkClient.apiService.getGovernanceSettings(groupId)
                if (resp.isSuccessful && resp.body()?.data != null) {
                    val data = resp.body()!!.data!!
                    governanceData = data
                    rows.forEach { row ->
                        if (row.setting.title == "Require multi-signature") {
                            accessState.setToggle(row.setting, data.requireMultiSignature)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            errorMsg = "Failed to load live settings from server"
        } finally {
            isLoading = false
        }
    }

    fun onToggleChange(setting: GroupSetting, newEnabled: Boolean) {
        val prev = accessState.isToggleOn(setting)
        accessState.setToggle(setting, newEnabled)

        if (groupId.isNotBlank() && viewerRole.isAdmin) {
            if (isGoalControls) {
                scope.launch {
                    isSaving = true
                    try {
                        val req = when (setting.title) {
                            "Lock funds" -> UpdateGoalControlsRequest(lockFunds = newEnabled)
                            "Hide leaderboard" -> UpdateGoalControlsRequest(hideLeaderboard = newEnabled)
                            "Let members view their total saved" -> UpdateGoalControlsRequest(letMembersViewTotalSaved = newEnabled)
                            else -> null
                        }
                        if (req != null) {
                            val resp = NetworkClient.apiService.updateGoalControls(groupId, req)
                            if (!resp.isSuccessful) {
                                accessState.setToggle(setting, prev)
                            }
                        }
                    } catch (e: Exception) {
                        accessState.setToggle(setting, prev)
                    } finally {
                        isSaving = false
                    }
                }
            } else if (isGovernance && setting.title == "Require multi-signature") {
                scope.launch {
                    isSaving = true
                    try {
                        val req = UpdateGovernanceRequest(requireMultiSignature = newEnabled)
                        val resp = NetworkClient.apiService.updateGovernanceSettings(groupId, req)
                        if (!resp.isSuccessful) {
                            accessState.setToggle(setting, prev)
                        } else {
                            val updatedData = resp.body()?.data
                            if (updatedData != null) {
                                governanceData = updatedData
                            }
                        }
                    } catch (e: Exception) {
                        accessState.setToggle(setting, prev)
                    } finally {
                        isSaving = false
                    }
                }
            }
        }
    }

    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier,
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = pageTitle(groupType, page),
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
                .verticalScroll(rememberScrollState())
        ) {
            GroupTypeStrip(groupType, memberCount, pageDescription(page))
            WaSettingsDivider()
            WaSettingsSectionHeader(pageTitle(groupType, page))

            if (!viewerRole.isAdmin) {
                MemberViewNotice()
                WaSettingsDivider()
            }

            errorMsg?.let { msg ->
                Text(
                    text = "⚠ $msg",
                    color = MicrobondDestructiveRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            if (rows.isEmpty()) {
                Text(
                    text = "There are no settings for you on this page.",
                    color = MicrobondInactiveGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            rows.forEachIndexed { index, row ->
                val dynamicValue = if (isGovernance && governanceData != null) {
                    when (row.setting.title) {
                        "Multi-signature signer roster" -> governanceData?.signersDisplay ?: row.setting.value
                        "Active audit status" -> governanceData?.auditStatus ?: row.setting.value
                        "Approval quorum" -> governanceData?.quorumDisplay ?: row.setting.value
                        "Pending disbursals", "Pending payout queue", "Pending bill payments",
                        "Pending assistance payouts", "Pending vendor payments", "Pending loans",
                        "Pending withdrawals", "Pending expense approvals", "Pending dues adjustments" ->
                            governanceData?.pendingDisbursalsCount?.toString() ?: row.setting.value
                        "My approval authorization" -> governanceData?.myApprovalAuthorization ?: row.setting.value
                        else -> row.setting.value
                    }
                } else {
                    row.setting.value
                }
                val setting = if (dynamicValue != row.setting.value) row.setting.copy(value = dynamicValue) else row.setting
                val hint = accessState.audienceHint(setting)
                if (setting.toggle) {
                    GroupSettingToggleRow(
                        setting = setting,
                        checked = accessState.isToggleOn(setting),
                        onCheckedChange = { onToggleChange(setting, it) },
                        enabled = row.access == SettingAccess.EDITABLE,
                        audienceHint = hint
                    )
                } else {
                    GroupSettingActionRow(
                        setting = setting,
                        onClick = { onNavigateToSetting(setting.title) },
                        audienceHint = hint
                    )
                }
                if (index != rows.lastIndex) WaSettingsDivider()
            }

            WaSettingsDivider()

            if (!isGoalControls && !isGovernance) {
                Text(
                    text = "Settings shown here are a local prototype and are not synced to a server.",
                    color = MicrobondInactiveGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }
}
