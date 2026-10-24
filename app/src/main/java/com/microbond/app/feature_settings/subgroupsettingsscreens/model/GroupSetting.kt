package com.microbond.app.feature_settings.subgroupsettingsscreens.model

/**
 * A single row on a group settings page.
 *
 * @param audience Who may see this setting (see [SettingAudience]).
 * @param sharedWithMembersByDefault For [SettingAudience.ADMIN_SHAREABLE] settings, whether members
 *        can view it before an admin changes the visibility.
 * @param gatedBy An admin toggle that must be ON for members to see this setting.
 */
data class GroupSetting(
    val title: String,
    val description: String = "",
    val value: String? = null,
    val toggle: Boolean = false,
    val initiallyEnabled: Boolean = false,
    val audience: SettingAudience = SettingAudience.EVERYONE,
    val sharedWithMembersByDefault: Boolean = false,
    val gatedBy: GroupSetting? = null
) {
    val key: String get() = title
}
