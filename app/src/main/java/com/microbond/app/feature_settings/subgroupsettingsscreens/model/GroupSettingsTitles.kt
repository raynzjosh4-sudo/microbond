package com.microbond.app.feature_settings.subgroupsettingsscreens.model

import com.microbond.app.core.data_classes_and_enums.SavingsGroupType

internal fun pageTitle(groupType: SavingsGroupType, page: GroupSettingsPage): String =
    when (page) {
        GroupSettingsPage.TYPE_RULES -> typeRulesTitle(groupType)
        GroupSettingsPage.TRANSACTION_MESSAGES -> transactionMessagesTitle(groupType)
        GroupSettingsPage.MILESTONES_SECURITY -> milestonesTitle(groupType)
        else -> page.pageTitle
    }

internal fun typeRulesTitle(groupType: SavingsGroupType): String = when (groupType) {
    SavingsGroupType.MERRY_GO_ROUND -> "Payout rules"
    SavingsGroupType.GOAL_SAVINGS -> "Goal controls"
    SavingsGroupType.BILL_SPLIT -> "Bill administration"
    SavingsGroupType.WELFARE_FUND -> "Welfare rules"
    SavingsGroupType.EVENT_COMMITTEE -> "Pledge rules"
    SavingsGroupType.INVESTMENT_CLUB -> "Investment rules"
    SavingsGroupType.FUNDRAISER -> "Fundraiser rules"
    SavingsGroupType.SHARED_WALLET -> "Wallet controls"
    SavingsGroupType.ASSOCIATION_DUES -> "Dues administration"
}

internal fun transactionMessagesTitle(groupType: SavingsGroupType): String = when (groupType) {
    SavingsGroupType.MERRY_GO_ROUND -> "Transaction messages"
    SavingsGroupType.GOAL_SAVINGS -> "Contribution messages"
    SavingsGroupType.BILL_SPLIT -> "Bill payment messages"
    SavingsGroupType.WELFARE_FUND -> "Fund & payment messages"
    SavingsGroupType.EVENT_COMMITTEE -> "Pledge payment messages"
    SavingsGroupType.INVESTMENT_CLUB -> "Loan & investment messages"
    SavingsGroupType.FUNDRAISER -> "Donation messages"
    SavingsGroupType.SHARED_WALLET -> "Wallet transaction messages"
    SavingsGroupType.ASSOCIATION_DUES -> "Dues payment messages"
}

internal fun milestonesTitle(groupType: SavingsGroupType): String = when (groupType) {
    SavingsGroupType.MERRY_GO_ROUND -> "Milestones & security"
    SavingsGroupType.GOAL_SAVINGS -> "Goal milestones & security"
    SavingsGroupType.BILL_SPLIT -> "Utility alerts & security"
    SavingsGroupType.WELFARE_FUND -> "Approvals & security"
    SavingsGroupType.EVENT_COMMITTEE -> "Milestones & security"
    SavingsGroupType.INVESTMENT_CLUB -> "Portfolio milestones & security"
    SavingsGroupType.FUNDRAISER -> "Campaign milestones & security"
    SavingsGroupType.SHARED_WALLET -> "Wallet milestones & security"
    SavingsGroupType.ASSOCIATION_DUES -> "Dues milestones & security"
}

internal fun pageDescription(page: GroupSettingsPage): String = when (page) {
    GroupSettingsPage.PERSONAL -> "private to you"
    GroupSettingsPage.TYPE_RULES -> "admin settings"
    GroupSettingsPage.GOVERNANCE -> "approvals & audit"
    GroupSettingsPage.TRUST -> "trust & membership"
    GroupSettingsPage.LIQUIDITY -> "fund stability"
    GroupSettingsPage.PRIVACY -> "privacy & records"
    GroupSettingsPage.TRANSACTION_MESSAGES -> "confirmed transactions"
    GroupSettingsPage.REMINDERS_REPORTS -> "scheduled messages"
    GroupSettingsPage.MILESTONES_SECURITY -> "progress & governance"
}
