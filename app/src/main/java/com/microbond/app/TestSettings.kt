package com.microbond.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.microbond.app.feature_settings.subgroupsettingsscreens.access.GroupMemberRole

object TestSettings {
    var currentRole by mutableStateOf(GroupMemberRole.ADMIN)
}
