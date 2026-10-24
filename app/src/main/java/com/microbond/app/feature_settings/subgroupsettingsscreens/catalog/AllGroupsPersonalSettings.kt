package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Section 1 — personal settings every user has in every group (shown at the bottom of "Your settings"). */
internal fun allGroupsPersonalSettings(): List<GroupSetting> = listOf(
    GroupSetting("My payment method", "Manage the mobile money method used for group payments."),
    GroupSetting("Mute / custom notifications", value = "Off"),
    GroupSetting("Export my records", "Download a personal copy of your group activity."),
    GroupSetting("Report group", "Send a private report to Microbond support."),
    GroupSetting("Exit group", "Leave this group. Any unsettled financial obligations remain visible.")
)
