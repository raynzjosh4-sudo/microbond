package com.microbond.app.feature_settings.subgroupsettingsscreens.access

import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** What the current viewer can do with a setting. */
enum class SettingAccess {
    HIDDEN,
    READ_ONLY,
    EDITABLE
}

/** A setting paired with the viewer's access level. */
data class VisibleSetting(
    val setting: GroupSetting,
    val access: SettingAccess
)
