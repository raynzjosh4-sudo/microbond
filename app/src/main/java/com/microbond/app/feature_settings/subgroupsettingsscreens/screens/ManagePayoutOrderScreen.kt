package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.feature_settings.subgroupsettingsscreens.ui.SubSettingsTopBar
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

@Composable
fun ManagePayoutOrderScreen(
    groupName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectionStrategy by remember { mutableStateOf("Admin-Assigned") }
    var showArrangeOrder by remember { mutableStateOf(false) }

    if (showArrangeOrder) {
        AdminArrangePayoutOrderScreen(groupName = groupName, onBack = { showArrangeOrder = false })
        return
    }

    Scaffold(
        containerColor = MicrobondDarkBackground,
        topBar = {
            SubSettingsTopBar(
                title = "Manage payout order",
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
            WaSettingsSectionHeader("Cycle rotation")

            WaSettingsValueRow(
                icon = Icons.Default.Shuffle,
                title = "Selection strategy",
                value = selectionStrategy,
                onClick = { /* Open strategy picker */ }
            )

            WaSettingsDivider()

            WaSettingsRow(
                icon = Icons.Default.ListAlt,
                title = "View current rotation",
                subtitle = "See whose turn it is to receive the next scheduled payout.",
                onClick = { showArrangeOrder = true }
            )
        }
    }
}
