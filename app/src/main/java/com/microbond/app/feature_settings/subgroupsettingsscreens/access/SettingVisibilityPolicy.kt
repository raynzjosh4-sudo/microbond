package com.microbond.app.feature_settings.subgroupsettingsscreens.access

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.catalog.settingsFor
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsDestination
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.SettingAudience
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.groupSettingsDestinations

/**
 * Role rules:
 * - Admins see and edit everything.
 * - Members see [SettingAudience.EVERYONE] settings, unless an admin gate is switched off.
 * - Members see [SettingAudience.ADMIN_SHAREABLE] settings read-only, only when an admin shares them.
 * - Members never see [SettingAudience.ADMIN_ONLY] settings.
 */
/**
 * In development / testing:
 * Any settings that are still using dummy data or need integration are shown in the app
 * so they are never hidden from the user, regardless of admin status.
 */
fun GroupSettingsAccessState.accessFor(
    setting: GroupSetting,
    role: GroupMemberRole
): SettingAccess {
    return SettingAccess.EDITABLE
}

/** Settings on [page] the viewer is allowed to see. */
fun GroupSettingsAccessState.visibleSettings(
    groupType: SavingsGroupType,
    page: GroupSettingsPage,
    role: GroupMemberRole
): List<VisibleSetting> =
    settingsFor(groupType, page).map { setting ->
        VisibleSetting(setting, accessFor(setting, role))
    }

/** Pages the viewer can open. All pages shown so user can inspect which settings still use dummy data. */
fun GroupSettingsAccessState.visibleDestinations(
    groupType: SavingsGroupType,
    role: GroupMemberRole
): List<GroupSettingsDestination> =
    groupSettingsDestinations(groupType)

/** All settings an admin can share with members, grouped by page. */
fun shareableSettingsByPage(
    groupType: SavingsGroupType
): Map<GroupSettingsPage, List<GroupSetting>> =
    GroupSettingsPage.values()
        .associateWith { page ->
            settingsFor(groupType, page).filter { it.audience == SettingAudience.ADMIN_SHAREABLE }
        }
        .filterValues { it.isNotEmpty() }

/** Short label shown to admins describing who can see a setting. */
fun GroupSettingsAccessState.audienceHint(setting: GroupSetting): String? =
    when (setting.audience) {
        SettingAudience.EVERYONE -> null
        SettingAudience.ADMIN_ONLY -> "Admins only"
        SettingAudience.ADMIN_SHAREABLE ->
            if (isSharedWithMembers(setting)) "Visible to members" else "Hidden from members"
    }
