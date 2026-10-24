package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage

/** All settings defined for [page] in a [groupType] group, before any role filtering. */
internal fun settingsFor(
    groupType: SavingsGroupType,
    page: GroupSettingsPage
): List<GroupSetting> = when (page) {
    GroupSettingsPage.PERSONAL -> personalSettings(groupType)
    GroupSettingsPage.TYPE_RULES -> typeRulesSettings(groupType)
    GroupSettingsPage.GOVERNANCE -> governanceSettings(groupType)
    GroupSettingsPage.TRUST -> trustSettings(groupType)
    GroupSettingsPage.LIQUIDITY -> liquiditySettings()
    GroupSettingsPage.PRIVACY -> privacySettings(groupType)
    GroupSettingsPage.TRANSACTION_MESSAGES -> transactionMessageSettings(groupType)
    GroupSettingsPage.REMINDERS_REPORTS -> remindersReportsSettings(groupType)
    GroupSettingsPage.MILESTONES_SECURITY -> milestonesSecuritySettings(groupType)
}
