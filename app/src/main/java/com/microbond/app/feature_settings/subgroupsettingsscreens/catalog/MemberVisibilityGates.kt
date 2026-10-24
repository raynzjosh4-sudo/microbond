package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/**
 * Admin switches that decide whether members can see certain personal settings.
 * A setting points at one of these through [GroupSetting.gatedBy].
 */

/** Goal Savings: admin decides whether members can view their own total saved. */
internal val MembersCanViewTotalSaved: GroupSetting = GroupSetting(
    title = "Let members view their total saved",
    description = "When off, members cannot see their personal savings total.",
    toggle = true,
    initiallyEnabled = true
).adminOnly()
