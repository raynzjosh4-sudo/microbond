package com.microbond.app.feature_settings.subgroupsettingsscreens.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondOverlayWidgetDark
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.SettingAccess
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.VisibleSetting

@Composable
internal fun SettingDetailDialog(
    row: VisibleSetting,
    groupType: SavingsGroupType,
    onDismiss: () -> Unit
) {
    val setting = row.setting
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MicrobondOverlayWidgetDark,
        title = { Text(setting.title, color = MicrobondTextPrimary) },
        text = {
            Column {
                Text(
                    text = setting.description.ifBlank {
                        "This setting is available for ${groupType.uiLabel} groups."
                    },
                    color = MicrobondInactiveGray
                )
                setting.value?.let {
                    Text(
                        text = "Current: $it",
                        color = MicrobondTextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (row.access == SettingAccess.READ_ONLY) {
                    Text(
                        text = "Managed by group admins. You can view this setting but not change it.",
                        color = MicrobondInactiveGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = MicrobondActiveGreen)
            }
        }
    )
}
