package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Milestones & security page — group-level automation, members can view by default. */
internal fun milestonesSecuritySettings(groupType: SavingsGroupType): List<GroupSetting> {
    val progress = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "Payout cycle progress and member participation"
        SavingsGroupType.GOAL_SAVINGS -> "Goal progress and savings pace"
        SavingsGroupType.BILL_SPLIT -> "Average spending and bill resolution"
        SavingsGroupType.WELFARE_FUND -> "Pool status and group coverage"
        SavingsGroupType.EVENT_COMMITTEE -> "Budget raised and remaining shortfall"
        SavingsGroupType.INVESTMENT_CLUB -> "Capital utilization and repayments"
        SavingsGroupType.FUNDRAISER -> "Campaign progress and donor participation"
        SavingsGroupType.SHARED_WALLET -> "Wallet balance and spending summary"
        SavingsGroupType.ASSOCIATION_DUES -> "Collection rate and current member count"
    }
    val pollsTitle = when (groupType) {
        SavingsGroupType.WELFARE_FUND -> "Post assistance approval polls"
        SavingsGroupType.EVENT_COMMITTEE -> "Post event approval polls"
        SavingsGroupType.BILL_SPLIT -> "Post bill dispute polls"
        SavingsGroupType.INVESTMENT_CLUB -> "Post loan approval polls"
        else -> "Post group approval polls"
    }
    val ruleChangesTitle = when (groupType) {
        SavingsGroupType.WELFARE_FUND -> "Keep claim details private"
        SavingsGroupType.EVENT_COMMITTEE -> "Notify members about refund rules"
        SavingsGroupType.BILL_SPLIT -> "Post provider outage notices"
        else -> "Notify members about important rule changes"
    }
    return listOf(
        GroupSetting("Share financial snapshots", progress, toggle = true, initiallyEnabled = true),
        GroupSetting("Announce system events", "Member joins, admin changes, and rule updates.", toggle = true, initiallyEnabled = true),
        GroupSetting("Post security-rule updates", "Keep members informed about governance changes.", toggle = true, initiallyEnabled = true),
        GroupSetting(pollsTitle, "Allow members to review and vote on decisions.", toggle = true, initiallyEnabled = true),
        GroupSetting(ruleChangesTitle, "Respect group privacy rules when sending announcements.", toggle = true, initiallyEnabled = true)
    ).map { it.adminShareable(sharedByDefault = true) }
}
