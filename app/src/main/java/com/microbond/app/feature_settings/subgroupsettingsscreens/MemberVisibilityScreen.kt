package com.microbond.app.feature_settings.subgroupsettingsscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupSettingsAccessState
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.shareableSettingsByPage
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.pageTitle
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.iconFor
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow

/**
 * Admin-only page: choose which admin-controlled settings members can see (read-only).
 * Settings marked "Admins only" never appear here.
 */
@Composable
fun MemberVisibilityScreen(
    groupName: String,
    groupType: SavingsGroupType,
    accessState: GroupSettingsAccessState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsByPage = shareableSettingsByPage(groupType)

    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier,
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Member visibility",
                subtitle = groupName,
                onBack = onBack
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Turn on a setting to let members view it. Members can never change admin settings.",
                color = MicrobondInactiveGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
            WaSettingsDivider()

            settingsByPage.forEach { (page, settings) ->
                WaSettingsSectionHeader(pageTitle(groupType, page))
                settings.forEach { setting ->
                    val shared = accessState.isSharedWithMembers(setting)
                    WaSettingsSwitchRow(
                        icon = iconFor(setting.title),
                        title = setting.title,
                        subtitle = if (shared) "Members can view" else "Hidden from members",
                        checked = shared,
                        onCheckedChange = { accessState.setSharedWithMembers(setting, it) }
                    )
                }
                WaSettingsDivider()
            }
        }
    }
}
