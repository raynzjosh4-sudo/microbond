package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Reminders & reports page — group-level automation, members can view by default. */
internal fun remindersReportsSettings(groupType: SavingsGroupType): List<GroupSetting> {
    val contribution = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "deposit"
        SavingsGroupType.GOAL_SAVINGS -> "contribution"
        SavingsGroupType.BILL_SPLIT -> "bill payment"
        SavingsGroupType.WELFARE_FUND -> "fund contribution"
        SavingsGroupType.EVENT_COMMITTEE -> "pledge"
        SavingsGroupType.INVESTMENT_CLUB -> "loan repayment"
        SavingsGroupType.FUNDRAISER -> "donation"
        SavingsGroupType.SHARED_WALLET -> "wallet top-up"
        SavingsGroupType.ASSOCIATION_DUES -> "dues"
    }
    val report = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "payout cycle and contributions"
        SavingsGroupType.GOAL_SAVINGS -> "goal progress and savings pace"
        SavingsGroupType.BILL_SPLIT -> "shared bill totals and spending trends"
        SavingsGroupType.WELFARE_FUND -> "pool status and replenishment progress"
        SavingsGroupType.EVENT_COMMITTEE -> "budget raised and remaining shortfall"
        SavingsGroupType.INVESTMENT_CLUB -> "loan balances and portfolio performance"
        SavingsGroupType.FUNDRAISER -> "campaign total and donor activity"
        SavingsGroupType.SHARED_WALLET -> "wallet balance and approved spending"
        SavingsGroupType.ASSOCIATION_DUES -> "dues collected and members in arrears"
    }
    return listOf(
        GroupSetting("Send daily group summaries", "Post a daily overview of $report.", toggle = true, initiallyEnabled = true),
        GroupSetting("Post weekly financial reports", "Share $report with group members.", toggle = true, initiallyEnabled = true),
        GroupSetting("Send $contribution due prompts", "Send a reminder before the due date.", toggle = true, initiallyEnabled = true),
        GroupSetting("Daily summary time", value = "6:00 PM"),
        GroupSetting("Weekly report day", value = "Sunday evening")
    ).map { it.adminShareable(sharedByDefault = true) }
}
