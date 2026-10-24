package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/** "Your settings" page — every member sees these. */
internal fun personalSettings(groupType: SavingsGroupType): List<GroupSetting> =
    groupSpecificPersonalSettings(groupType) + allGroupsPersonalSettings()

/** Section 3 — private settings unique to each group type. */
private fun groupSpecificPersonalSettings(groupType: SavingsGroupType): List<GroupSetting> =
    when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> listOf(
            GroupSetting("My payout destination", "Choose the mobile money account used for your payout."),
            GroupSetting("Auto-pay my turn", toggle = true, initiallyEnabled = true),
            GroupSetting("Request turn swap", "Ask another member to exchange payout positions."),
            GroupSetting("Proxy contributor", "Choose a trusted person to contribute on your behalf.")
        )
        SavingsGroupType.GOAL_SAVINGS -> listOf(
            // Members only see this while the admin allows it.
            GroupSetting("My total saved", description = "View cumulative savings and interest gained.", gatedBy = MembersCanViewTotalSaved)
        )
        SavingsGroupType.BILL_SPLIT -> listOf(
            GroupSetting("My split share", value = "Equal split")
        )
        SavingsGroupType.WELFARE_FUND -> emptyList()
        SavingsGroupType.EVENT_COMMITTEE -> listOf(
            GroupSetting("My pledges", value = "UGX 500,000"),
            GroupSetting("Issue a challenge / match", "Invite members or sponsors to match a pledge.")
        )
        SavingsGroupType.INVESTMENT_CLUB -> listOf(
            GroupSetting("My equity / shares", value = "12 shares"),
            GroupSetting("My active loan details", "Review balance, repayment dates, and guarantors."),
            GroupSetting("Internal credit score", value = "Good"),
            GroupSetting("Dividend reinvestment", toggle = true, initiallyEnabled = true)
        )
        SavingsGroupType.FUNDRAISER -> listOf(
            GroupSetting("My contributions", value = "UGX 250,000"),
            GroupSetting("Anonymous contributions", "Hide your name on public campaign lists.", toggle = true),
            GroupSetting("My donation receipts")
        )
        SavingsGroupType.SHARED_WALLET -> listOf(
            GroupSetting("My contribution history"),
            GroupSetting("Spending alerts", "Notify me when a shared expense is approved.", toggle = true, initiallyEnabled = true),
            GroupSetting("Member balance visibility", "Show my contribution balance to group members.", toggle = true, initiallyEnabled = true)
        )
        SavingsGroupType.ASSOCIATION_DUES -> listOf(
            GroupSetting("My dues status", value = "Up to date"),
            GroupSetting("Payment schedule", value = "Monthly · UGX 100,000")
        )
    }
