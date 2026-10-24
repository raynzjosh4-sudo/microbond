package com.microbond.app.feature_settings.groupsetttingscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.groupsetttingscreen.settingswidgets.GroupSettingsPagesSection
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsDestination
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsDivider
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSectionHeader
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsSwitchRow
import com.microbond.app.screens.groupsetttingscreen.settingswidgets.WaSettingsValueRow

/**
 * WhatsApp-style settings preferences section containing group permissions,
 * notifications, media visibility, encryption, chat lock, advanced privacy,
 * archetype-specific settings pages, group finances, and exit/report actions.
 */
@Composable
fun GroupSettingsPreferencesSection(
    viewerRole: GroupMemberRole,
    groupType: SavingsGroupType,
    destinations: List<GroupSettingsDestination>,
    notificationsMuted: Boolean,
    mediaVisibility: Boolean,
    onMediaVisibilityChange: (Boolean) -> Unit,
    chatLocked: Boolean,
    onChatLockedChange: (Boolean) -> Unit,
    onPermissionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onEncryptionClick: () -> Unit,
    onAdvancedPrivacyClick: () -> Unit,
    onOpenPage: (GroupSettingsPage) -> Unit,
    onOpenMemberVisibility: () -> Unit,
    onPaymentMethodClick: () -> Unit,
    onExitGroupClick: () -> Unit,
    onReportGroupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        WaSettingsSectionHeader("Settings")

        WaSettingsRow(
            icon = Icons.Default.Settings,
            title = "Group permissions",
            onClick = onPermissionsClick
        )

        WaSettingsValueRow(
            icon = Icons.Default.NotificationsNone,
            title = "Notifications",
            value = if (notificationsMuted) "Muted" else "All",
            onClick = onNotificationsClick
        )

        WaSettingsSwitchRow(
            icon = Icons.Default.Image,
            title = "Media visibility",
            subtitle = if (mediaVisibility)
                "New media will be shown in your phone gallery"
            else
                "Media will not appear in your phone gallery",
            checked = mediaVisibility,
            onCheckedChange = onMediaVisibilityChange
        )

        WaSettingsRow(
            icon = Icons.Default.Lock,
            title = "Encryption",
            subtitle = "Messages and contributions are end-to-end encrypted. Learn more",
            onClick = onEncryptionClick
        )

        WaSettingsSwitchRow(
            icon = Icons.Default.Shield,
            title = "Chat lock",
            subtitle = "Lock and hide this group on this device.",
            checked = chatLocked,
            onCheckedChange = onChatLockedChange
        )

        WaSettingsRow(
            icon = Icons.Default.Security,
            title = "Advanced group privacy",
            subtitle = "On",
            onClick = onAdvancedPrivacyClick
        )

        WaSettingsDivider()

        // Group settings pages (filtered by role)
        GroupSettingsPagesSection(
            title = "${groupType.uiLabel} settings",
            destinations = destinations,
            showMemberVisibility = true,
            onOpenPage = onOpenPage,
            onOpenMemberVisibility = onOpenMemberVisibility
        )

        WaSettingsDivider()

        // Group finances
        WaSettingsSectionHeader("Group finances")
        WaSettingsRow(
            icon = Icons.Default.Payments,
            title = "My payment method",
            subtitle = "Mobile money · registered",
            onClick = onPaymentMethodClick
        )

        WaSettingsDivider()

        // Danger zone
        WaSettingsRow(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Exit group",
            titleColor = Color(0xFFFF4F4F),
            iconTint = Color(0xFFFF4F4F),
            onClick = onExitGroupClick
        )

        WaSettingsRow(
            icon = Icons.Default.Flag,
            title = "Report group",
            titleColor = Color(0xFFFF4F4F),
            iconTint = Color(0xFFFF4F4F),
            onClick = onReportGroupClick
        )

        WaSettingsDivider()

        Spacer(Modifier.height(48.dp))
    }
}
