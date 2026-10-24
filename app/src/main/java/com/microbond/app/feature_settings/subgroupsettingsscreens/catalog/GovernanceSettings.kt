package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** Governance & audit page — admin controlled, same settings for every group type. */
internal fun governanceSettings(groupType: SavingsGroupType): List<GroupSetting> {
    val pendingLabel = when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> "Pending payout queue"
        SavingsGroupType.GOAL_SAVINGS -> "Pending disbursals"
        SavingsGroupType.BILL_SPLIT -> "Pending bill payments"
        SavingsGroupType.WELFARE_FUND -> "Pending assistance payouts"
        SavingsGroupType.EVENT_COMMITTEE -> "Pending vendor payments"
        SavingsGroupType.INVESTMENT_CLUB -> "Pending loans"
        SavingsGroupType.FUNDRAISER -> "Pending withdrawals"
        SavingsGroupType.SHARED_WALLET -> "Pending expense approvals"
        SavingsGroupType.ASSOCIATION_DUES -> "Pending dues adjustments"
    }
    return listOf(
        GroupSetting("Multi-signature signer roster", "Review members authorized to approve fund movement.").adminShareable(),
        GroupSetting("Active audit status", value = "Healthy").adminShareable(),
        GroupSetting("Approval quorum", value = "3 of 5").adminShareable(),
        GroupSetting(pendingLabel, value = "2").adminShareable(),
        GroupSetting("My approval authorization", value = "Authorized").adminOnly(),
        GroupSetting("Require multi-signature", "Require a second authorized member to approve payments.", toggle = true, initiallyEnabled = true).adminShareable(),
        GroupSetting("Time-lock disbursals", value = "12 hours").adminShareable(),
        GroupSetting("Audit report delivery & compliance", "Automate compliance statements and email audit ledgers.").adminOnly(),
        GroupSetting("Escrow lock", "Temporarily prevent outgoing group payments.").adminOnly()
    )
}
