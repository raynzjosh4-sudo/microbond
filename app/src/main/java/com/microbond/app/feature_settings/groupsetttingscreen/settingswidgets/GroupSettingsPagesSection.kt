package com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsDestination
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader

/** Lists the settings pages the viewer is allowed to open. */
@Composable
fun GroupSettingsPagesSection(
    title: String,
    destinations: List<GroupSettingsDestination>,
    showMemberVisibility: Boolean,
    onOpenPage: (GroupSettingsPage) -> Unit,
    onOpenMemberVisibility: () -> Unit
) {
    if (destinations.isEmpty() && !showMemberVisibility) return

    WaSettingsSectionHeader(title)

    destinations.forEach { destination ->
        WaSettingsRow(
            icon = destination.icon,
            title = destination.title,
            subtitle = destination.subtitle,
            onClick = { onOpenPage(destination.page) }
        )
    }

    if (showMemberVisibility) {
        WaSettingsRow(
            icon = Icons.Default.Visibility,
            title = "Member visibility",
            subtitle = "Choose which admin settings members can see",
            onClick = onOpenMemberVisibility
        )
    }
}
