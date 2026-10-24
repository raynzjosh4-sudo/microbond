package com.microbond.app.feature_groups.ui.newgroup.financialsettings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.Composable
import com.microbond.app.feature_groups.ui.newgroup.widgets.SettingsSelectionRow

@Composable
fun GenericSettingsScreen() {
    SettingsSelectionRow(
        icon = Icons.Default.AttachMoney,
        title = "Default currency",
        value = "UGX",
        onClick = {}
    )
}
