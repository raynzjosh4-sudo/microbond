package com.microbond.app.feature_settings.groupsetttingscreen

import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSettingsPage

/** Which screen the group settings flow is currently showing. */
sealed interface GroupSettingsRoute {
    data object Main : GroupSettingsRoute
    data object Permissions : GroupSettingsRoute
    data object MemberVisibility : GroupSettingsRoute
    data object EditName : GroupSettingsRoute
    data object EditDescription : GroupSettingsRoute
    data object GroupIcon : GroupSettingsRoute
    data object AddMember : GroupSettingsRoute
    data class Page(val page: GroupSettingsPage) : GroupSettingsRoute
    data class SettingDetail(
        val settingTitle: String,
        val parentRoute: GroupSettingsRoute? = null
    ) : GroupSettingsRoute
}
