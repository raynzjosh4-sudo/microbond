package com.microbond.app.feature_settings.subgroupsettingsscreens.catalog

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.feature_settings.subgroupsettingsscreens.model.GroupSetting

/**
 * Group rules page — admin controlled. Admins may share any rule with members (read-only)
 * from the "Member visibility" page.
 */
internal fun typeRulesSettings(groupType: SavingsGroupType): List<GroupSetting> =
    when (groupType) {
        SavingsGroupType.MERRY_GO_ROUND -> listOf(
            GroupSetting("Manage payout order", "Edit member sequence and scheduled payout dates.").adminShareable(),
            GroupSetting("Late-fee penalties", value = "UGX 10,000").adminShareable(),
            GroupSetting("Grace period window", value = "24 hours").adminShareable(),
            GroupSetting("Enforce payout holds", toggle = true, initiallyEnabled = true).adminShareable(),
            GroupSetting("Substitute member allowance", "Define when a substitute contributor is allowed.").adminShareable()
        )
        SavingsGroupType.GOAL_SAVINGS -> listOf(
            GroupSetting("Lock funds", "Freeze contributions until the group goal is met.", toggle = true).adminShareable(),
            GroupSetting("Hide leaderboard", "Hide member-by-member savings totals.", toggle = true).adminShareable(),
            MembersCanViewTotalSaved
        )
        SavingsGroupType.BILL_SPLIT -> listOf(
            GroupSetting("Auto-pay bills", "Automatically pay approved bills before the due date.", toggle = true, initiallyEnabled = true).adminShareable()
        )
        SavingsGroupType.WELFARE_FUND -> listOf(
            GroupSetting("Approval process", value = "Admin and group vote").adminShareable(),
            GroupSetting("Maximum payout limit", value = "UGX 500,000").adminShareable(),
            GroupSetting("Direct-to-vendor payouts", "Pay approved clinics or vendors directly.", toggle = true, initiallyEnabled = true).adminShareable(),
            GroupSetting("Cooling-off period for new members", value = "30 days").adminShareable()
        )
        SavingsGroupType.EVENT_COMMITTEE -> listOf(
            GroupSetting("Milestone unlocks", "Release approved expenses when fundraising goals are reached.").adminShareable()
        )
        SavingsGroupType.INVESTMENT_CLUB -> listOf(
            GroupSetting("Interest rates", value = "Set by group vote").adminShareable(),
            GroupSetting("Loan guarantor rules", "Set minimum guarantors and eligibility.").adminShareable(),
            GroupSetting("Maximum borrowing limit", value = "UGX 2,000,000").adminShareable(),
            GroupSetting("Early repayment discount", value = "2%").adminShareable(),
            GroupSetting("New share issuance", "Set member approval rules for new shares.").adminShareable(),
            GroupSetting("Default asset seizure", "Define recovery actions permitted by the group.").adminShareable()
        )
        SavingsGroupType.FUNDRAISER -> listOf(
            GroupSetting("Allow anonymous contributions", "Hide donor identity from public campaign pages.", toggle = true, initiallyEnabled = true).adminShareable(),
            GroupSetting("Campaign target and deadline", "Manage fundraising target and close date.").adminShareable(),
            GroupSetting("Public campaign visibility", toggle = true, initiallyEnabled = true).adminShareable()
        )
        SavingsGroupType.SHARED_WALLET -> listOf(
            GroupSetting("Spending approval threshold", value = "UGX 200,000").adminShareable(),
            GroupSetting("Require multi-member approval", toggle = true, initiallyEnabled = true).adminShareable()
        )
        SavingsGroupType.ASSOCIATION_DUES -> listOf(
            GroupSetting("Dues amount and frequency", value = "UGX 100,000 · monthly").adminShareable(),
            GroupSetting("Grace period", value = "7 days").adminShareable(),
            GroupSetting("Late fee", value = "UGX 5,000").adminShareable(),
            GroupSetting("Allow installments", toggle = true, initiallyEnabled = true).adminShareable(),
            GroupSetting("Arrears policy", "Set reminders and member eligibility rules for overdue dues.").adminShareable(),
            GroupSetting("Fee exemptions", "Manage temporary or permanent dues exemptions.").adminShareable()
        )
    }
