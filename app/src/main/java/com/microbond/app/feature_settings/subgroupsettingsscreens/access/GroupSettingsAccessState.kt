package com.microbond.app.feature_settings.subgroupsettingsscreens.access

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/**
 * Local (prototype) state for one group's settings: toggle values and which
 * admin settings have been shared with members. Shared across every settings page
 * so an admin change on one page is reflected on the others.
 */
@Stable
class GroupSettingsAccessState {
    private val toggleOverrides = mutableStateMapOf<String, Boolean>()
    private val memberVisibilityOverrides = mutableStateMapOf<String, Boolean>()

    fun isToggleOn(setting: GroupSetting): Boolean =
        toggleOverrides[setting.key] ?: setting.initiallyEnabled

    fun setToggle(setting: GroupSetting, enabled: Boolean) {
        toggleOverrides[setting.key] = enabled
    }

    fun isSharedWithMembers(setting: GroupSetting): Boolean =
        memberVisibilityOverrides[setting.key] ?: setting.sharedWithMembersByDefault

    fun setSharedWithMembers(setting: GroupSetting, shared: Boolean) {
        memberVisibilityOverrides[setting.key] = shared
    }
}

@Composable
fun rememberGroupSettingsAccessState(vararg keys: Any?): GroupSettingsAccessState =
    remember(*keys) { GroupSettingsAccessState() }
