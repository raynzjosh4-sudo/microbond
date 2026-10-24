package com.microbond.app.feature_groups.ui.newgroup.financialsettings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.microbond.app.feature_groups.ui.newgroup.widgets.SettingsSelectionRow
import com.microbond.app.feature_groups.ui.newgroup.widgets.SettingsSwitchRow

@Composable
fun GoalSavingsSettingsScreen() {
    SettingsSelectionRow(
        icon = Icons.Default.AttachMoney,
        title = "Target goal amount",
        value = "UGX 5,000,000",
        onClick = {}
    )

    SettingsSelectionRow(
        icon = Icons.Default.DateRange,
        title = "Target completion date",
        value = "Dec 31, 2024",
        onClick = {}
    )
    
    var strictLock by remember { mutableStateOf(true) }
    SettingsSwitchRow(
        icon = Icons.Default.Timer,
        title = "Lock funds until goal",
        description = "Withdrawals are restricted until the target date or goal amount is reached.",
        checked = strictLock,
        onCheckedChange = { strictLock = it }
    )
}
