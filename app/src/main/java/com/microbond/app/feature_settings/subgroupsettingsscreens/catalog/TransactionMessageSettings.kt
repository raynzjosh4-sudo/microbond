package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Transaction messages page — group-level automation, members can view by default. */
internal fun transactionMessageSettings(groupType: SavingsGroupType): List<GroupSetting> {
    val transaction = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "deposit"
        SavingsGroupType.GOAL_SAVINGS -> "contribution"
        SavingsGroupType.BILL_SPLIT -> "bill contribution"
        SavingsGroupType.WELFARE_FUND -> "welfare contribution"
        SavingsGroupType.EVENT_COMMITTEE -> "pledge"
        SavingsGroupType.INVESTMENT_CLUB -> "loan repayment"
        SavingsGroupType.FUNDRAISER -> "donation"
        SavingsGroupType.SHARED_WALLET -> "wallet transfer"
        SavingsGroupType.ASSOCIATION_DUES -> "dues payment"
    }
    val payout = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "payout"
        SavingsGroupType.GOAL_SAVINGS -> "approved withdrawal"
        SavingsGroupType.BILL_SPLIT -> "bill payment"
        SavingsGroupType.WELFARE_FUND -> "approved assistance payout"
        SavingsGroupType.EVENT_COMMITTEE -> "vendor payment"
        SavingsGroupType.INVESTMENT_CLUB -> "loan disbursement"
        SavingsGroupType.FUNDRAISER -> "campaign withdrawal"
        SavingsGroupType.SHARED_WALLET -> "approved wallet expense"
        SavingsGroupType.ASSOCIATION_DUES -> "dues adjustment"
    }
    val noticeTitle = when (groupType) {
        SavingsGroupType.BILL_SPLIT -> "Post utility service notices"
        SavingsGroupType.WELFARE_FUND -> "Show direct-vendor payment receipts"
        SavingsGroupType.EVENT_COMMITTEE -> "Announce approved event expenses"
        else -> "Announce admin transactions"
    }
    return listOf(
        GroupSetting(
            "Broadcast individual $transaction receipts",
            "Show confirmed $transaction receipts in group chat.",
            toggle = true,
            initiallyEnabled = true
        ),
        GroupSetting(
            "Announce $payout updates",
            "Post a confirmation when an approved $payout is completed.",
            toggle = true,
            initiallyEnabled = true
        ),
        GroupSetting("Show transfer receipts", "Include wallet top-ups and member transfers.", toggle = true, initiallyEnabled = true),
        GroupSetting(
            noticeTitle,
            "Only confirmed and authorized transactions are announced.",
            toggle = true,
            initiallyEnabled = true
        )
    ).map { it.adminShareable(sharedByDefault = true) }
}
