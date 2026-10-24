package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Trust & members page. */
internal fun trustSettings(groupType: SavingsGroupType): List<GroupSetting> = listOf(
    GroupSetting("Group health rating", value = "Good"),
    GroupSetting(
        if (groupType == SavingsGroupType.INVESTMENT_CLUB) "Vouch for a borrower" else "Vouch for a member",
        "Submit or review a member endorsement."
    ),
    GroupSetting("Guarantor required", "Require a guarantor before selected benefits are available.", toggle = true).adminShareable()
)
