package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.SettingAudience

/** Only admins can see or change this setting. */
internal fun GroupSetting.adminOnly(): GroupSetting =
    copy(audience = SettingAudience.ADMIN_ONLY)

/**
 * Admins control this setting and can let members view it (read-only)
 * from the "Member visibility" page.
 */
internal fun GroupSetting.adminShareable(sharedByDefault: Boolean = false): GroupSetting =
    copy(audience = SettingAudience.ADMIN_SHAREABLE, sharedWithMembersByDefault = sharedByDefault)
