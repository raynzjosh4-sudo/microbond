package com.microbond.app.feature_groups.ui.newgroup.financialsettings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.runtime.Composable
import com.microbond.app.feature_groups.ui.newgroup.widgets.SettingsSelectionRow

@Composable
fun BillSplitSettingsScreen() {
    SettingsSelectionRow(
        icon = Icons.Default.AttachMoney,
        title = "Total bill amount",
        value = "Not set",
        onClick = {}
    )

    SettingsSelectionRow(
        icon = Icons.Default.FormatListNumbered,
        title = "Split method",
        value = "Equal split",
        onClick = {}
    )
}
