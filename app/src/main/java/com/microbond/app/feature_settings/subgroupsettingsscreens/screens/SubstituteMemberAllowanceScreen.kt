package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.TransferWithinAStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow

@Composable
fun SubstituteMemberAllowanceScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var allowSubstitutes by remember { mutableStateOf(false) }
    var requireAdminApproval by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Substitute members",
                subtitle = groupName,
                onBack = onBack
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            WaSettingsSectionHeader("Delegation")

            WaSettingsSwitchRow(
                icon = Icons.Default.TransferWithinAStation,
                title = "Allow substitute members",
                subtitle = "Let members permanently or temporarily transfer their seat and equity to someone outside the group.",
                checked = allowSubstitutes,
                onCheckedChange = { allowSubstitutes = it }
            )

            if (allowSubstitutes) {
                WaSettingsDivider()

                WaSettingsSwitchRow(
                    icon = Icons.Default.SupervisorAccount,
                    title = "Require admin vetting",
                    subtitle = "New substitutes must pass admin approval before taking over a seat.",
                    checked = requireAdminApproval,
                    onCheckedChange = { requireAdminApproval = it }
                )
            }
        }
    }
}
