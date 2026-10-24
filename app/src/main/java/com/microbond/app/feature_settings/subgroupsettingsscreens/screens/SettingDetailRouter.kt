package com.microbond.app.feature_settings.subgroupsettingsscreens.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ActiveAuditStatusScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.AdvancedGroupPrivacyScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ApprovalProcessScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ApprovalQuorumScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ArrearsPolicyScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.CampaignTargetAndDeadlineScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ChatHistoryCleanupScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ContributionReminderScheduleScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.CoolingoffPeriodForNewMembersScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.DailySummaryTimeScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.DefaultAssetSeizureScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.DownloadOfficialStatementScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.DuesAmountAndFrequencyScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.EarlyRepaymentDiscountScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.EmergencyContactScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.EncryptionScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.EscrowLockScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ExitGroupScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ExportMyRecordsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.FeeExemptionsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.GracePeriodScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.GracePeriodWindowScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.GroupHealthRatingScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.InterestRatesScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.InternalCreditScoreScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.IssueAChallengeMatchScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.LatefeePenaltiesScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.LateFeeScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.LiquidityBufferCoverageScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.LoanGuarantorRulesScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ManagePayoutOrderScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MaximumBorrowingLimitScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MaximumPayoutLimitScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MilestoneUnlocksScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MinimumTrustScoreScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MultisignatureSignerRosterScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MuteCustomNotificationsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyActiveLoanDetailsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyApprovalAuthorizationScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyContributionHistoryScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyContributionsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyDataRetentionScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyDonationReceiptsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyDuesStatusScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyEquitySharesScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyPaymentMethodScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyPayoutDestinationScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyPledgesScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MySplitShareScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyTotalSavedScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.MyTrustBreakdownScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.NewShareIssuanceScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.NotificationsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.PaymentScheduleScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ProxyContributorScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.ReportGroupScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.RequestTurnSwapScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.SharedMediaLinksAndDocsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.SpendingApprovalThresholdScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.SubstituteMemberAllowanceScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.TimelockDisbursalsScreen
import com.microbond.app.feature_settings.subgroupsettingsscreens.screens.WeeklyReportDayScreen

@Composable
fun SettingDetailRouter(
    settingTitle: String,
    groupName: String,
    onBack: () -> Unit,
    groupId: String = "",
    isAdmin: Boolean = true
) {
    when (settingTitle) {
        "Active audit status" -> ActiveAuditStatusScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Advanced group privacy" -> AdvancedGroupPrivacyScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Approval process" -> ApprovalProcessScreen(groupName = groupName, onBack = onBack)
        "Audit report delivery", "Audit report delivery & compliance", "Compliance statement" -> AuditReportDeliveryScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Approval quorum" -> ApprovalQuorumScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Arrears policy" -> ArrearsPolicyScreen(groupName = groupName, onBack = onBack)
        "Campaign target and deadline" -> CampaignTargetAndDeadlineScreen(groupName = groupName, onBack = onBack)
        "Chat history cleanup" -> ChatHistoryCleanupScreen(groupName = groupName, onBack = onBack)
        "Contribution reminder schedule" -> ContributionReminderScheduleScreen(groupName = groupName, onBack = onBack)
        "Cooling-off period for new members" -> CoolingoffPeriodForNewMembersScreen(groupName = groupName, onBack = onBack)
        "Daily summary time" -> DailySummaryTimeScreen(groupName = groupName, onBack = onBack)
        "Default asset seizure" -> DefaultAssetSeizureScreen(groupName = groupName, onBack = onBack)
        "Download official statement" -> DownloadOfficialStatementScreen(groupName = groupName, onBack = onBack)
        "Dues amount and frequency" -> DuesAmountAndFrequencyScreen(groupName = groupName, onBack = onBack)
        "Early repayment discount" -> EarlyRepaymentDiscountScreen(groupName = groupName, onBack = onBack)
        "Emergency contact" -> EmergencyContactScreen(groupName = groupName, onBack = onBack)
        "Encryption" -> EncryptionScreen(groupName = groupName, onBack = onBack)
        "Escrow lock" -> EscrowLockScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Exit group" -> ExitGroupScreen(groupName = groupName, groupId = groupId, onBack = onBack)
        "Export my records" -> ExportMyRecordsScreen(groupName = groupName, groupId = groupId, onBack = onBack)
        "Fee exemptions" -> FeeExemptionsScreen(groupName = groupName, onBack = onBack)
        "Grace period" -> GracePeriodScreen(groupName = groupName, onBack = onBack)
        "Grace period window" -> GracePeriodWindowScreen(groupName = groupName, onBack = onBack)
        "Group health rating" -> GroupHealthRatingScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Interest rates" -> InterestRatesScreen(groupName = groupName, onBack = onBack)
        "Internal credit score" -> InternalCreditScoreScreen(groupName = groupName, onBack = onBack)
        "Issue a challenge / match" -> IssueAChallengeMatchScreen(groupName = groupName, onBack = onBack)
        "Late-fee penalties" -> LatefeePenaltiesScreen(groupName = groupName, onBack = onBack)
        "Late fee" -> LateFeeScreen(groupName = groupName, onBack = onBack)
        "Liquidity buffer coverage" -> LiquidityBufferCoverageScreen(groupName = groupName, onBack = onBack)
        "Loan guarantor rules", "Guarantor rules", "Guarantor required" -> LoanGuarantorRulesScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )

        "Manage payout order" -> ManagePayoutOrderScreen(groupName = groupName, onBack = onBack)
        "Maximum borrowing limit" -> MaximumBorrowingLimitScreen(groupName = groupName, onBack = onBack)
        "Maximum payout limit" -> MaximumPayoutLimitScreen(groupName = groupName, onBack = onBack)
        "Milestone unlocks" -> MilestoneUnlocksScreen(groupName = groupName, onBack = onBack)
        "Manage authorized signers" -> ManageAuthorizedSignersScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Multi-signature signer roster" -> MultisignatureSignerRosterScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Mute / custom notifications" -> MuteCustomNotificationsScreen(groupName = groupName, groupId = groupId, onBack = onBack)
        "My active loan details" -> MyActiveLoanDetailsScreen(groupName = groupName, onBack = onBack)
        "My approval authorization" -> MyApprovalAuthorizationScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "My contribution history" -> MyContributionHistoryScreen(groupName = groupName, onBack = onBack)
        "My contributions" -> MyContributionsScreen(groupName = groupName, onBack = onBack)
        "My data retention" -> MyDataRetentionScreen(groupName = groupName, onBack = onBack)
        "My donation receipts" -> MyDonationReceiptsScreen(groupName = groupName, onBack = onBack)
        "My dues status" -> MyDuesStatusScreen(groupName = groupName, onBack = onBack)
        "My equity / shares" -> MyEquitySharesScreen(groupName = groupName, onBack = onBack)
        "My payment method" -> MyPaymentMethodScreen(groupName = groupName, groupId = groupId, onBack = onBack)
        "My payout destination" -> MyPayoutDestinationScreen(groupName = groupName, onBack = onBack)
        "My pledges" -> MyPledgesScreen(groupName = groupName, onBack = onBack)
        "My split share" -> MySplitShareScreen(groupName = groupName, onBack = onBack)
        "My total saved" -> MyTotalSavedScreen(groupName = groupName, groupId = groupId, onBack = onBack)
        "New share issuance" -> NewShareIssuanceScreen(groupName = groupName, onBack = onBack)
        "Notifications" -> NotificationsScreen(groupName = groupName, onBack = onBack)
        "Payment schedule" -> PaymentScheduleScreen(groupName = groupName, onBack = onBack)
        "Pending disbursals", "Pending payout queue", "Pending bill payments",
        "Pending assistance payouts", "Pending vendor payments", "Pending loans",
        "Pending withdrawals", "Pending expense approvals", "Pending dues adjustments" -> PendingDisbursalsScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin,
            pageTitle = settingTitle
        )
        "Proxy contributor" -> ProxyContributorScreen(groupName = groupName, onBack = onBack)
        "Report group" -> ReportGroupScreen(groupName = groupName, groupId = groupId, onBack = onBack)
        "Request turn swap" -> RequestTurnSwapScreen(groupName = groupName, onBack = onBack)
        "Shared media, links and docs" -> SharedMediaLinksAndDocsScreen(groupName = groupName, onBack = onBack)
        "Spending approval threshold" -> SpendingApprovalThresholdScreen(groupName = groupName, onBack = onBack)
        "Substitute member allowance" -> SubstituteMemberAllowanceScreen(groupName = groupName, onBack = onBack)
        "Time-lock disbursals" -> TimelockDisbursalsScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin
        )
        "Weekly report day" -> WeeklyReportDayScreen(groupName = groupName, onBack = onBack)
        "Vouch for a member", "Vouch for a borrower" -> VouchForMemberScreen(
            groupName = groupName,
            onBack = onBack,
            groupId = groupId,
            isAdmin = isAdmin,
            isBorrower = (settingTitle == "Vouch for a borrower")
        )
        "Group charter", "Savings charter", "Bill-sharing agreement", "Welfare charter", 

        "Event charter", "Investment charter", "Campaign terms", "Wallet agreement", 
        "Association constitution" -> GroupCharterScreen(title = settingTitle, groupName = groupName, onBack = onBack)
        else -> {
            // Fallback
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Screen not found for: $settingTitle", color = Color.White)
            }
        }
    }
}
