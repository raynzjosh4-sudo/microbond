package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Privacy & records page. */
internal fun privacySettings(groupType: SavingsGroupType): List<GroupSetting> {
    val charterName = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "Group charter"
        SavingsGroupType.GOAL_SAVINGS -> "Savings charter"
        SavingsGroupType.BILL_SPLIT -> "Bill-sharing agreement"
        SavingsGroupType.WELFARE_FUND -> "Welfare charter"
        SavingsGroupType.EVENT_COMMITTEE -> "Event charter"
        SavingsGroupType.INVESTMENT_CLUB -> "Investment charter"
        SavingsGroupType.FUNDRAISER -> "Campaign terms"
        SavingsGroupType.SHARED_WALLET -> "Wallet agreement"
        SavingsGroupType.ASSOCIATION_DUES -> "Association constitution"
    }
    return listOf(
        GroupSetting(charterName, "Read the rules and member-approved terms for this group."),
        GroupSetting("My data retention", value = "2 years"),
        GroupSetting("Download official statement", "Prepare an export of group account activity.").adminShareable(sharedByDefault = true),
        GroupSetting("Chat history cleanup", value = "After 12 months").adminShareable(sharedByDefault = true)
    )
}
