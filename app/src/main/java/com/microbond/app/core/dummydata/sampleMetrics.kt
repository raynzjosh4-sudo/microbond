package com.microbond.app.core.dummydata

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// ──────────────────────────────────────────────────────────────────────────────
//  Domain model
// ──────────────────────────────────────────────────────────────────────────────

/**
 * A single selectable metric that the user can pick when building a statistics
 * bubble to share in a group thread.
 *
 * @param id           Unique stable key (snake_case)
 * @param label        Short display name shown in the grid
 * @param description  One-line explanation shown below the label
 * @param icon         Material icon representing the metric
 * @param category     Which filter tab this metric belongs to
 * @param groupType    Which group type(s) this metric applies to (ALL = all)
 * @param currentValue Formatted sample value for display
 * @param trend        Optional trending indicator string, e.g. "+4.2%"
 * @param trendUp      True = green arrow, False = red arrow, null = neutral
 * @param rawValue     Unformatted numeric value for chart calculations
 * @param unit         Unit for axis labels, e.g. "UGX", "%", or "days"
 * @param historicalData Ordered sample y-values for graph rendering
 * @param preferredChartType Suggested chart layout for this metric
 */
data class MetricItem(
    val id: String,
    val label: String,
    val description: String,
    val icon: ImageVector,
    val category: MetricCategory,
    val groupType: MetricGroupType = MetricGroupType.ALL,
    val currentValue: String = "",
    val trend: String? = null,
    val trendUp: Boolean? = null,
    val rawValue: Double? = null,
    val unit: String = "",
    val historicalData: List<Float> = emptyList(),
    val preferredChartType: ChartType = ChartType.LINE,
    val chartSegments: List<MetricChartSegment> = emptyList(),
    val heatmapData: List<List<Float>> = emptyList()
)

data class MetricChartSegment(
    val label: String,
    val value: Double
)

enum class ChartType {
    LINE,
    BAR,
    AREA,
    PIE,
    HEATMAP
}

enum class MetricCategory(val label: String) {
    ALL("All"),
    SAVING("Saving"),
    MEMBERS("Members"),
    PAYOUTS("Payouts"),
    FUNDRAISER("Fundraiser"),
    PAYMENTS("Payments"),
    RISK("Risk"),
    SOCIAL("Social"),
    DISPUTES("Disputes")
}

enum class MetricGroupType {
    ALL,
    FIXED_SAVINGS,
    ROTATING_ROSCA,
    CONTRIBUTION_FUNDRAISER,
    SHARED_BILL_UTILITY,
    LENDING_INVESTMENT,
    EMERGENCY_WELFARE,
    EVENT_PLEDGE,
    SHARED_WALLET,
    ASSOCIATION_DUES
}

// ──────────────────────────────────────────────────────────────────────────────
//  Axis / comparison options
// ──────────────────────────────────────────────────────────────────────────────

data class AxisOption(val id: String, val label: String, val icon: ImageVector)

val sampleAxisOptions = listOf(
    AxisOption("total_accumulation",  "Total accumulation",    Icons.Default.AccountBalance),
    AxisOption("active_members",      "Active members",        Icons.Default.Groups),
    AxisOption("target_met_rate",     "Target met rate",       Icons.Default.Percent),
    AxisOption("payout_progress",     "Payout progress",       Icons.Default.TrendingUp),
    AxisOption("successful_days",     "Successful save-days",  Icons.Default.CalendarMonth),
    AxisOption("total_disbursed",     "Total disbursed",       Icons.Default.MonetizationOn),
    AxisOption("avg_deposit",         "Avg. deposit size",     Icons.Default.LocalAtm),
    AxisOption("missed_rate",         "Missed-saving rate",    Icons.Default.Warning),
    AxisOption("wow_change",          "Week-over-week change", Icons.Default.SyncAlt),
    AxisOption("total_raised",        "Total raised",          Icons.Default.Flag),
    AxisOption("active_donors",       "Active donors",         Icons.Default.Favorite),
    AxisOption("pledge_rate",         "Pledge fulfillment",    Icons.Default.QueryStats)
)

// ──────────────────────────────────────────────────────────────────────────────
//  Period options
// ──────────────────────────────────────────────────────────────────────────────

data class PeriodOption(val id: String, val label: String)

val samplePeriodOptions = listOf(
    PeriodOption("1w",  "Last 1 week"),
    PeriodOption("2w",  "Last 2 weeks"),
    PeriodOption("4w",  "Last 4 weeks"),
    PeriodOption("3m",  "Last 3 months"),
    PeriodOption("6m",  "Last 6 months"),
    PeriodOption("1y",  "Last 1 year"),
    PeriodOption("all", "All time")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Cross-Group / General Metrics  (apply to all group types)
// ──────────────────────────────────────────────────────────────────────────────

val generalMetrics = listOf(
    MetricItem("total_accumulation",  "Total accumulation",       "All-time group savings total",                Icons.Default.AccountBalance,  MetricCategory.SAVING,   currentValue = "UGX 3,620,000",  trend = "+8.3%",  trendUp = true),
    MetricItem("target_met_rate",     "Target met rate",          "% of daily targets hit",                      Icons.Default.Percent,         MetricCategory.SAVING,   currentValue = "91%",            trend = "+2.1%",  trendUp = true),
    MetricItem("wow_change",          "Week-over-week change",    "Savings growth vs. previous week",            Icons.Default.SyncAlt,         MetricCategory.SAVING,   currentValue = "+4.2%",          trend = "+4.2%",  trendUp = true),
    MetricItem("busiest_day",         "Busiest saving day",       "Day most members deposit",                    Icons.Default.WbSunny,         MetricCategory.SAVING,   currentValue = "Thursday"),
    MetricItem("quietest_day",        "Quietest saving day",      "Day fewest members deposit",                  Icons.Default.Schedule,        MetricCategory.SAVING,   currentValue = "Monday"),
    MetricItem("avg_deposit",         "Average deposit size",     "Mean deposit per member per day",             Icons.Default.LocalAtm,        MetricCategory.SAVING,   currentValue = "UGX 10,500"),
    MetricItem("active_members",      "Total active members",     "Members who saved at least once",             Icons.Default.Groups,          MetricCategory.MEMBERS,  currentValue = "49 / 50",        trend = "+2",     trendUp = true),
    MetricItem("new_join_rate",       "New member join rate",     "New members this cycle",                      Icons.Default.PersonAdd,       MetricCategory.MEMBERS,  currentValue = "2 joined"),
    MetricItem("activity_index",      "Group activity index",     "Combined engagement score",                   Icons.Default.Speed,           MetricCategory.SAVING,   currentValue = "87 / 100"),
    MetricItem("payout_progress",     "Payout progress",          "Payouts completed this cycle",                Icons.Default.TrendingUp,      MetricCategory.PAYOUTS,  currentValue = "2 / 12",         trend = "+1",     trendUp = true),
    MetricItem("total_disbursed",     "Total money disbursed",    "Sum of all payouts made",                     Icons.Default.MonetizationOn,  MetricCategory.PAYOUTS,  currentValue = "UGX 960,000"),
    MetricItem("next_payout",         "Next payout countdown",    "Days until the next member is paid",          Icons.Default.Alarm,           MetricCategory.PAYOUTS,  currentValue = "3 days"),
    MetricItem("members_arrears",     "Members with arrears",     "Members carrying a running balance",          Icons.Default.Warning,         MetricCategory.MEMBERS,  currentValue = "11 members"),
    MetricItem("cleared_balance",     "Members cleared balance",  "Members who settled their debt",              Icons.Default.HealthAndSafety, MetricCategory.MEMBERS,  currentValue = "2 this week"),
    MetricItem("deposit_history",     "Individual deposit history","Per-member deposit log",                     Icons.Default.WorkHistory,     MetricCategory.MEMBERS,  currentValue = "See timeline"),
    MetricItem("saving_streaks",      "Individual saving streaks", "Longest consecutive saving run",             Icons.Default.Star,            MetricCategory.MEMBERS,  currentValue = "Grace: 28 days"),
    MetricItem("missed_rate",         "Missed-saving rate",       "% of eligible days with no deposit",          Icons.Default.Percent,         MetricCategory.SAVING,   currentValue = "2.3%",           trend = "-0.5%",  trendUp = true),
    MetricItem("partial_vs_nosave",   "Partial vs. no-save days", "Partial-save ratio vs. full miss",           Icons.Default.BarChart,        MetricCategory.SAVING,   currentValue = "21 vs 8"),
    MetricItem("avg_wait_time",       "Average wait / payout delay","Mean days between deposit & payout",       Icons.Default.Schedule,        MetricCategory.PAYOUTS,  currentValue = "3.2 days"),
    MetricItem("payment_method_split","Payment method breakdown",  "MTN vs Airtel vs Cash share",                Icons.Default.PieChart,        MetricCategory.PAYMENTS, currentValue = "MTN 72%"),
    MetricItem("peak_hours",          "Peak deposit hours",       "Hour range with most deposits",               Icons.Default.QueryStats,      MetricCategory.SAVING,   currentValue = "7–9 PM"),
    MetricItem("group_retention",     "Group retention rate",     "Members who stayed across cycles",            Icons.Default.Groups,          MetricCategory.MEMBERS,  currentValue = "96%"),
    MetricItem("payment_reliability", "Payment reliability index","On-time payment consistency score",           Icons.Default.HealthAndSafety, MetricCategory.PAYMENTS, currentValue = "0.91"),
    MetricItem("stk_fail_rate",       "STK push failure rate",    "% of STK pushes that failed",                Icons.Default.Warning,         MetricCategory.PAYMENTS, currentValue = "1.4%",           trend = "-0.3%",  trendUp = true),
    MetricItem("time_to_wallet",      "Time-to-wallet speed",     "Avg. minutes from collection to payout",      Icons.Default.Speed,           MetricCategory.PAYMENTS, currentValue = "4 min 12 sec"),
    MetricItem("avg_tx_cost",         "Average transaction cost", "Mean fee per deposit",                        Icons.Default.CreditCard,      MetricCategory.PAYMENTS, currentValue = "UGX 350"),
    MetricItem("ghost_members",       "Ghost member count",       "Members with zero activity",                  Icons.Default.PublicOff,       MetricCategory.MEMBERS,  currentValue = "1 member"),
    MetricItem("collective_health",   "Collective group health",  "Overall group health score",                  Icons.Default.Favorite,        MetricCategory.SAVING,   currentValue = "88 / 100"),
    MetricItem("chat_activity_index", "Chat activity index",     "Messages sent per day across the group",      Icons.Default.QueryStats,      MetricCategory.SOCIAL,   currentValue = "128 messages/day"),
    MetricItem("poll_participation_rate", "Poll participation rate", "Members voting in group decisions",       Icons.Default.PieChart,        MetricCategory.SOCIAL,   currentValue = "76%"),
    MetricItem("voice_chat_usage",    "Voice chat usage",        "Minutes spent in live audio rooms per week", Icons.Default.Schedule,        MetricCategory.SOCIAL,   currentValue = "42 min/week"),
    MetricItem("flagged_transactions", "Flagged transactions",   "Receipts or payments disputed by members",   Icons.Default.Warning,         MetricCategory.DISPUTES, currentValue = "2 disputes"),
    MetricItem("admin_intervention_rate", "Admin intervention rate", "Ledger entries manually reviewed or changed", Icons.Default.HealthAndSafety, MetricCategory.DISPUTES, currentValue = "3.5%")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Fixed Savings Group Metrics
// ──────────────────────────────────────────────────────────────────────────────

val fixedSavingsMetrics = listOf(
    MetricItem("capital_utilization",   "Capital utilization rate",     "How much of the vault is deployed",            Icons.Default.AccountBalance,  MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "84%"),
    MetricItem("late_fee_pool",         "Late-fee pool growth",         "Total late fees accumulated",                  Icons.Default.Warning,         MetricCategory.PAYMENTS, MetricGroupType.FIXED_SAVINGS,  currentValue = "UGX 48,000"),
    MetricItem("projected_dividend",    "Projected dividend yield",     "Estimated end-of-cycle dividend",              Icons.Default.TrendingUp,      MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "UGX 72,000"),
    MetricItem("seasonal_fluctuation",  "Seasonal savings fluctuation", "Month-over-month savings volatility",          Icons.Default.ShowChart,       MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "±12%"),
    MetricItem("deposit_frequency",     "Deposit frequency spectrum",   "Distribution of daily vs. weekly depositors",  Icons.Default.BarChart,        MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "Daily: 68%"),
    MetricItem("compound_trajectory",   "Compound vault trajectory",    "Projected vault value at cycle end",            Icons.Default.TrendingUp,      MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "UGX 5.1M"),
    MetricItem("reserve_flow",          "Net reserve flow rate",        "Inflows minus outflows this cycle",            Icons.Default.SyncAlt,         MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "+UGX 240,000"),
    MetricItem("vault_streak",          "Group saving streak",          "Consecutive cycles with no missed payout",     Icons.Default.Star,            MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "4 cycles"),
    MetricItem("monthly_volatility",    "Monthly deposit volatility",   "Std. dev. of monthly deposit totals",          Icons.Default.BarChart,        MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "±UGX 18,000"),
    MetricItem("reminder_lag",          "Reminder-to-deposit time lag", "Avg. minutes from reminder to deposit",        Icons.Default.Alarm,           MetricCategory.SAVING,   MetricGroupType.FIXED_SAVINGS,  currentValue = "22 min"),
    MetricItem("member_credit_risk",    "Individual credit risk heatmap","Per-member arrears & default likelihood",      Icons.Default.Warning,         MetricCategory.RISK,     MetricGroupType.FIXED_SAVINGS,  currentValue = "See heatmap"),
    MetricItem("penalty_audit",         "Penalty override & exemption", "Exemptions granted vs. enforced penalties",    Icons.Default.WorkHistory,     MetricCategory.RISK,     MetricGroupType.FIXED_SAVINGS,  currentValue = "3 exemptions"),
    MetricItem("savings_tier",          "Savings tier distribution",    "Wealth spread across deposit tiers",           Icons.Default.PieChart,        MetricCategory.MEMBERS,  MetricGroupType.FIXED_SAVINGS,  currentValue = "See chart")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Rotating ROSCA (Merry-Go-Round) Metrics
// ──────────────────────────────────────────────────────────────────────────────

val roscaMetrics = listOf(
    MetricItem("pot_speed",             "Pot collection speed bands",   "Fast vs slow collection per round",            Icons.Default.Speed,           MetricCategory.PAYOUTS,  MetricGroupType.ROTATING_ROSCA, currentValue = "Avg 2.1 days"),
    MetricItem("round_activity",        "Round activity vs. off-day",   "Active deposit days in each round",            Icons.Default.CalendarMonth,   MetricCategory.SAVING,   MetricGroupType.ROTATING_ROSCA, currentValue = "83% active"),
    MetricItem("installment_ratio",     "Installment breakdown ratio",  "One-shot vs. installment depositors",          Icons.Default.BarChart,        MetricCategory.PAYMENTS, MetricGroupType.ROTATING_ROSCA, currentValue = "60/40 split"),
    MetricItem("cycle_velocity",        "Cycle completion velocity",    "Days to fully complete a round",               Icons.Default.TrendingUp,      MetricCategory.PAYOUTS,  MetricGroupType.ROTATING_ROSCA, currentValue = "7.2 days avg"),
    MetricItem("post_payout_risk",      "Post-payout default risk",     "Default likelihood after a member gets paid",  Icons.Default.Warning,         MetricCategory.RISK,     MetricGroupType.ROTATING_ROSCA, currentValue = "Low – 3%"),
    MetricItem("defaulter_roster",      "Defaulter flag & CRB risk",    "Members flagged for default history",          Icons.Default.Warning,         MetricCategory.RISK,     MetricGroupType.ROTATING_ROSCA, currentValue = "1 flagged"),
    MetricItem("grace_freq",            "Grace period request freq.",   "How often members request extra time",         Icons.Default.Alarm,           MetricCategory.RISK,     MetricGroupType.ROTATING_ROSCA, currentValue = "2 this cycle"),
    MetricItem("payout_delivery_speed", "Payout delivery speed",        "Minutes from collection to M-Pesa credit",    Icons.Default.Speed,           MetricCategory.PAYOUTS,  MetricGroupType.ROTATING_ROSCA, currentValue = "4 min 12 sec"),
    MetricItem("round_completion_rate", "Round completion on-time rate","% of rounds where pot was fully collected",    Icons.Default.Percent,         MetricCategory.PAYOUTS,  MetricGroupType.ROTATING_ROSCA, currentValue = "91.6%"),
    MetricItem("pot_consistency",       "Pot value consistency index",  "Variance in pot total across rounds",          Icons.Default.QueryStats,      MetricCategory.PAYOUTS,  MetricGroupType.ROTATING_ROSCA, currentValue = "±UGX 5,200"),
    MetricItem("payout_readiness",      "Payout readiness battery",     "% of required funds collected pre-payout",    Icons.Default.HealthAndSafety, MetricCategory.PAYOUTS,  MetricGroupType.ROTATING_ROSCA, currentValue = "97%"),
    MetricItem("telecom_latency",       "Telecom confirmation latency", "Seconds from STK to wallet confirmation",      Icons.Default.Schedule,        MetricCategory.PAYMENTS, MetricGroupType.ROTATING_ROSCA, currentValue = "18 sec avg"),
    MetricItem("guarantor_web",         "Inter-member guarantor web",   "Liability network among members",              Icons.Default.Groups,          MetricCategory.RISK,     MetricGroupType.ROTATING_ROSCA, currentValue = "See graph")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Shared Bill / Utility Metrics
// ──────────────────────────────────────────────────────────────────────────────

val sharedBillMetrics = listOf(
    MetricItem("average_monthly_spend", "Average monthly spend", "Average utility spend per month", Icons.Default.AccountBalance, MetricCategory.SAVING, MetricGroupType.SHARED_BILL_UTILITY, currentValue = "UGX 150,000"),
    MetricItem("bill_resolution_speed", "Bill resolution speed", "Days from invoice upload to group payment", Icons.Default.Schedule, MetricCategory.PAYMENTS, MetricGroupType.SHARED_BILL_UTILITY, currentValue = "2.4 days"),
    MetricItem("overdue_invoices", "Overdue invoices", "Count of unpaid utility bills past due", Icons.Default.Warning, MetricCategory.RISK, MetricGroupType.SHARED_BILL_UTILITY, currentValue = "3 invoices")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Lending & Investment Club Metrics
// ──────────────────────────────────────────────────────────────────────────────

val lendingInvestmentMetrics = listOf(
    MetricItem("active_loan_volume", "Active loan volume", "Principal currently borrowed by members", Icons.Default.MonetizationOn, MetricCategory.SAVING, MetricGroupType.LENDING_INVESTMENT, currentValue = "UGX 850,000"),
    MetricItem("interest_generated_roi", "Interest generated / ROI", "Interest earned from loan repayments", Icons.Default.TrendingUp, MetricCategory.PAYOUTS, MetricGroupType.LENDING_INVESTMENT, currentValue = "8.5%"),
    MetricItem("loan_default_rate", "Loan default rate", "Loans not repaid on time", Icons.Default.Warning, MetricCategory.RISK, MetricGroupType.LENDING_INVESTMENT, currentValue = "4.2%")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Emergency & Welfare Fund Metrics
// ──────────────────────────────────────────────────────────────────────────────

val emergencyWelfareMetrics = listOf(
    MetricItem("welfare_pool_balance", "Welfare pool balance", "Available funds in the group's welfare pool", Icons.Default.AccountBalance, MetricCategory.SAVING, MetricGroupType.EMERGENCY_WELFARE, currentValue = "UGX 2,400,000"),
    MetricItem("active_help_requests", "Active help requests", "Assistance claims currently awaiting resolution", Icons.Default.Favorite, MetricCategory.MEMBERS, MetricGroupType.EMERGENCY_WELFARE, currentValue = "3 requests"),
    MetricItem("assistance_disbursed", "Assistance disbursed", "Total welfare support paid to beneficiaries", Icons.Default.MonetizationOn, MetricCategory.PAYOUTS, MetricGroupType.EMERGENCY_WELFARE, currentValue = "UGX 680,000"),
    MetricItem("welfare_replenishment_rate", "Welfare replenishment rate", "Contributions received against the replenishment target", Icons.Default.TrendingUp, MetricCategory.SAVING, MetricGroupType.EMERGENCY_WELFARE, currentValue = "82%"),
    MetricItem("claim_resolution_days", "Claim resolution time", "Average days from assistance request to decision", Icons.Default.Schedule, MetricCategory.PAYOUTS, MetricGroupType.EMERGENCY_WELFARE, currentValue = "1.8 days"),
    MetricItem("eligible_welfare_members", "Members in good standing", "Members currently eligible to request support", Icons.Default.Groups, MetricCategory.MEMBERS, MetricGroupType.EMERGENCY_WELFARE, currentValue = "46 / 50"),
    MetricItem("welfare_approval_rate", "Assistance approval rate", "Share of reviewed claims approved", Icons.Default.CheckCircle, MetricCategory.RISK, MetricGroupType.EMERGENCY_WELFARE, currentValue = "88%"),
    MetricItem("average_assistance_amount", "Average assistance amount", "Mean value of approved assistance payments", Icons.Default.LocalAtm, MetricCategory.PAYOUTS, MetricGroupType.EMERGENCY_WELFARE, currentValue = "UGX 170,000")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Event & Pledge Committee Metrics
// ──────────────────────────────────────────────────────────────────────────────

val eventPledgeMetrics = listOf(
    MetricItem("event_budget", "Event budget", "Approved budget for the group event", Icons.Default.AccountBalance, MetricCategory.FUNDRAISER, MetricGroupType.EVENT_PLEDGE, currentValue = "UGX 12,000,000"),
    MetricItem("event_funds_raised", "Event funds raised", "Cleared contributions collected for the event", Icons.Default.MonetizationOn, MetricCategory.FUNDRAISER, MetricGroupType.EVENT_PLEDGE, currentValue = "UGX 8,240,000"),
    MetricItem("pledge_total_value", "Total pledged", "Combined value of member and sponsor pledges", Icons.Default.Flag, MetricCategory.FUNDRAISER, MetricGroupType.EVENT_PLEDGE, currentValue = "UGX 10,500,000"),
    MetricItem("event_pledge_fulfillment", "Pledge fulfillment rate", "Share of pledged value already paid", Icons.Default.Percent, MetricCategory.FUNDRAISER, MetricGroupType.EVENT_PLEDGE, currentValue = "78%"),
    MetricItem("outstanding_pledges", "Outstanding pledges", "Pledged value still awaiting payment", Icons.Default.Warning, MetricCategory.PAYMENTS, MetricGroupType.EVENT_PLEDGE, currentValue = "UGX 2,260,000"),
    MetricItem("event_budget_shortfall", "Remaining budget shortfall", "Amount still needed to reach the event budget", Icons.Default.Warning, MetricCategory.FUNDRAISER, MetricGroupType.EVENT_PLEDGE, currentValue = "UGX 3,760,000"),
    MetricItem("pledge_payment_lag", "Pledge-to-payment lag", "Average days between pledge and payment", Icons.Default.Schedule, MetricCategory.PAYMENTS, MetricGroupType.EVENT_PLEDGE, currentValue = "2.7 days"),
    MetricItem("active_event_contributors", "Active event contributors", "Unique members who have paid toward the event", Icons.Default.Groups, MetricCategory.MEMBERS, MetricGroupType.EVENT_PLEDGE, currentValue = "148 contributors"),
    MetricItem("sponsor_contribution_share", "Sponsor contribution share", "Raised funds contributed by sponsors", Icons.Default.Favorite, MetricCategory.FUNDRAISER, MetricGroupType.EVENT_PLEDGE, currentValue = "18%"),
    MetricItem("event_vendor_spend", "Vendor spend", "Approved event funds paid to vendors", Icons.Default.CreditCard, MetricCategory.PAYOUTS, MetricGroupType.EVENT_PLEDGE, currentValue = "UGX 3,100,000")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Shared Wallet Metrics
// ──────────────────────────────────────────────────────────────────────────────

val sharedWalletMetrics = listOf(
    MetricItem("shared_wallet_balance", "Shared wallet balance", "Current balance held for common group expenses", Icons.Default.AccountBalance, MetricCategory.SAVING, MetricGroupType.SHARED_WALLET, currentValue = "UGX 1,280,000"),
    MetricItem("shared_wallet_inflows", "Shared wallet inflows", "Member contributions and transfers into the wallet", Icons.Default.TrendingUp, MetricCategory.PAYMENTS, MetricGroupType.SHARED_WALLET, currentValue = "UGX 2,400,000"),
    MetricItem("shared_wallet_outflows", "Shared wallet outflows", "Approved spending from the shared wallet", Icons.Default.CreditCard, MetricCategory.PAYMENTS, MetricGroupType.SHARED_WALLET, currentValue = "UGX 1,120,000"),
    MetricItem("shared_wallet_approval_rate", "Wallet approval rate", "Share of expense requests approved by co-signers", Icons.Default.Percent, MetricCategory.RISK, MetricGroupType.SHARED_WALLET, currentValue = "94%")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Association Dues Metrics
// ──────────────────────────────────────────────────────────────────────────────

val associationDuesMetrics = listOf(
    MetricItem("dues_collection_rate", "Dues collection rate", "Members paid in the current collection period", Icons.Default.Percent, MetricCategory.PAYMENTS, MetricGroupType.ASSOCIATION_DUES, currentValue = "88%"),
    MetricItem("dues_members_current", "Members up to date", "Members who have paid current dues", Icons.Default.Groups, MetricCategory.MEMBERS, MetricGroupType.ASSOCIATION_DUES, currentValue = "44 members"),
    MetricItem("dues_members_in_arrears", "Members in arrears", "Members with unpaid membership dues", Icons.Default.Warning, MetricCategory.RISK, MetricGroupType.ASSOCIATION_DUES, currentValue = "6 members"),
    MetricItem("dues_arrears_amount", "Dues outstanding", "Total unpaid balance across members", Icons.Default.MonetizationOn, MetricCategory.PAYMENTS, MetricGroupType.ASSOCIATION_DUES, currentValue = "UGX 300,000"),
    MetricItem("dues_collected_total", "Dues collected", "Total fees collected in the current period", Icons.Default.AccountBalance, MetricCategory.SAVING, MetricGroupType.ASSOCIATION_DUES, currentValue = "UGX 4,400,000")
)

// ──────────────────────────────────────────────────────────────────────────────
//  Contribution / Fundraiser Metrics
// ──────────────────────────────────────────────────────────────────────────────

val fundraiserMetrics = listOf(
    MetricItem("link_conversion",       "Link conversion rate",         "Views-to-donation rate on shared links",       Icons.Default.ShowChart,       MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "8.4%",          trend = "+1.2%", trendUp = true),
    MetricItem("pledge_fulfillment",    "Pledge fulfillment rate",      "Pledges converted to actual payments",         Icons.Default.Flag,            MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "78%"),
    MetricItem("diaspora_share",        "Diaspora contribution share",  "Contributions from abroad",                    Icons.Default.PublicOff,       MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "34%"),
    MetricItem("avg_donation",          "Average donation per share",   "Mean donation per link-share event",           Icons.Default.LocalAtm,        MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "UGX 28,500"),
    MetricItem("pledge_cash_lag",       "Pledge-to-cash lag",          "Days between pledge and actual payment",        Icons.Default.Alarm,           MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "2.7 days"),
    MetricItem("donation_distribution", "Donation size distribution",   "Histogram of donation amounts",                Icons.Default.BarChart,        MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "See chart"),
    MetricItem("daily_pace",            "Daily pace vs. run-rate",      "Required daily pace vs. actual",               Icons.Default.Speed,           MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "On track"),
    MetricItem("domestic_vs_diaspora",  "Domestic vs. diaspora split",  "Local vs. foreign currency contributions",     Icons.Default.PieChart,        MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "66% / 34%"),
    MetricItem("milestone_velocity",    "Milestone reaching velocity",  "Days to hit each campaign milestone",          Icons.Default.TrendingUp,      MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "Avg 4 days"),
    MetricItem("campaign_momentum",     "Campaign share momentum",      "Rate of new shares per day",                   Icons.Default.Campaign,        MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "12 shares/day"),
    MetricItem("goal_coverage_pace",    "Goal coverage pace",           "Actual % collected vs. required pace",         Icons.Default.QueryStats,      MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "103% of pace"),
    MetricItem("tod_heatmap",           "Time-of-day heatmap",          "Donation activity by hour of day",             Icons.Default.Schedule,        MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "8–10 PM peak"),
    MetricItem("channel_yield",         "Channel yield split",          "Revenue share by payment channel",             Icons.Default.CreditCard,      MetricCategory.PAYMENTS, MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "MTN 58%"),
    MetricItem("donor_concentration",   "Donor concentration risk",     "Top-5 donors as % of total funds",            Icons.Default.Warning,         MetricCategory.RISK,     MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "Top 5 = 41%"),
    MetricItem("projected_completion",  "Projected goal completion",    "Estimated date goal will be fully funded",     Icons.Default.CalendarMonth,   MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "14 Oct 2026"),
    MetricItem("recency_distribution",  "Contribution recency",         "When donors last gave (1d / 7d / 30d)",        Icons.Default.WorkHistory,     MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "52% within 7d"),
    MetricItem("weekend_vs_weekday",    "Weekend vs. weekday volume",   "Total donations by weekend vs. weekday",       Icons.Default.BarChart,        MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "Weekday +38%"),
    MetricItem("active_donors",         "Active donors",                "Unique people who gave this campaign",         Icons.Default.Favorite,        MetricCategory.MEMBERS,  MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "148 donors"),
    MetricItem("new_donors",            "New donors",                   "First-time contributors",                      Icons.Default.PersonAdd,       MetricCategory.MEMBERS,  MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "34 new"),
    MetricItem("total_raised",          "Total raised",                 "Grand total collected so far",                 Icons.Default.MonetizationOn,  MetricCategory.FUNDRAISER,MetricGroupType.CONTRIBUTION_FUNDRAISER, currentValue = "UGX 8,240,000",trend = "+12%", trendUp = true)
)

// ──────────────────────────────────────────────────────────────────────────────
//  Aggregated list (all metrics together for the "All" tab)
// ──────────────────────────────────────────────────────────────────────────────

private data class MetricGraphSpec(
    val currentValue: Double,
    val unit: String,
    val chartType: ChartType
)

private val metricGraphSpecs = mapOf(
    "total_accumulation" to MetricGraphSpec(3_620_000.0, "UGX", ChartType.AREA),
    "target_met_rate" to MetricGraphSpec(91.0, "%", ChartType.LINE),
    "wow_change" to MetricGraphSpec(4.2, "%", ChartType.LINE),
    "avg_deposit" to MetricGraphSpec(10_500.0, "UGX", ChartType.BAR),
    "active_members" to MetricGraphSpec(49.0, "members", ChartType.LINE),
    "new_join_rate" to MetricGraphSpec(2.0, "members", ChartType.BAR),
    "activity_index" to MetricGraphSpec(87.0, "score", ChartType.LINE),
    "payout_progress" to MetricGraphSpec(2.0, "payouts", ChartType.BAR),
    "total_disbursed" to MetricGraphSpec(960_000.0, "UGX", ChartType.AREA),
    "next_payout" to MetricGraphSpec(3.0, "days", ChartType.LINE),
    "members_arrears" to MetricGraphSpec(11.0, "members", ChartType.BAR),
    "saving_streaks" to MetricGraphSpec(28.0, "days", ChartType.LINE),
    "missed_rate" to MetricGraphSpec(2.3, "%", ChartType.LINE),
    "payment_method_split" to MetricGraphSpec(72.0, "%", ChartType.PIE),
    "payment_reliability" to MetricGraphSpec(0.91, "score", ChartType.LINE),
    "stk_fail_rate" to MetricGraphSpec(1.4, "%", ChartType.LINE),
    "ghost_members" to MetricGraphSpec(1.0, "members", ChartType.BAR),
    "collective_health" to MetricGraphSpec(88.0, "score", ChartType.AREA),
    "chat_activity_index" to MetricGraphSpec(128.0, "messages/day", ChartType.BAR),
    "poll_participation_rate" to MetricGraphSpec(76.0, "%", ChartType.LINE),
    "voice_chat_usage" to MetricGraphSpec(42.0, "minutes/week", ChartType.BAR),
    "flagged_transactions" to MetricGraphSpec(2.0, "transactions", ChartType.BAR),
    "admin_intervention_rate" to MetricGraphSpec(3.5, "%", ChartType.LINE),
    "average_monthly_spend" to MetricGraphSpec(150_000.0, "UGX", ChartType.AREA),
    "bill_resolution_speed" to MetricGraphSpec(2.4, "days", ChartType.LINE),
    "overdue_invoices" to MetricGraphSpec(3.0, "invoices", ChartType.BAR),
    "active_loan_volume" to MetricGraphSpec(850_000.0, "UGX", ChartType.AREA),
    "interest_generated_roi" to MetricGraphSpec(8.5, "%", ChartType.LINE),
    "loan_default_rate" to MetricGraphSpec(4.2, "%", ChartType.LINE),
    "welfare_pool_balance" to MetricGraphSpec(2_400_000.0, "UGX", ChartType.AREA),
    "active_help_requests" to MetricGraphSpec(3.0, "requests", ChartType.BAR),
    "assistance_disbursed" to MetricGraphSpec(680_000.0, "UGX", ChartType.AREA),
    "welfare_replenishment_rate" to MetricGraphSpec(82.0, "%", ChartType.LINE),
    "claim_resolution_days" to MetricGraphSpec(1.8, "days", ChartType.LINE),
    "eligible_welfare_members" to MetricGraphSpec(46.0, "members", ChartType.LINE),
    "welfare_approval_rate" to MetricGraphSpec(88.0, "%", ChartType.LINE),
    "average_assistance_amount" to MetricGraphSpec(170_000.0, "UGX", ChartType.BAR),
    "event_budget" to MetricGraphSpec(12_000_000.0, "UGX", ChartType.AREA),
    "event_funds_raised" to MetricGraphSpec(8_240_000.0, "UGX", ChartType.AREA),
    "pledge_total_value" to MetricGraphSpec(10_500_000.0, "UGX", ChartType.AREA),
    "event_pledge_fulfillment" to MetricGraphSpec(78.0, "%", ChartType.LINE),
    "outstanding_pledges" to MetricGraphSpec(2_260_000.0, "UGX", ChartType.AREA),
    "event_budget_shortfall" to MetricGraphSpec(3_760_000.0, "UGX", ChartType.AREA),
    "pledge_payment_lag" to MetricGraphSpec(2.7, "days", ChartType.LINE),
    "active_event_contributors" to MetricGraphSpec(148.0, "contributors", ChartType.BAR),
    "sponsor_contribution_share" to MetricGraphSpec(18.0, "%", ChartType.LINE),
    "event_vendor_spend" to MetricGraphSpec(3_100_000.0, "UGX", ChartType.BAR),
    "shared_wallet_balance" to MetricGraphSpec(1_280_000.0, "UGX", ChartType.AREA),
    "shared_wallet_inflows" to MetricGraphSpec(2_400_000.0, "UGX", ChartType.BAR),
    "shared_wallet_outflows" to MetricGraphSpec(1_120_000.0, "UGX", ChartType.BAR),
    "shared_wallet_approval_rate" to MetricGraphSpec(94.0, "%", ChartType.LINE),
    "dues_collection_rate" to MetricGraphSpec(88.0, "%", ChartType.LINE),
    "dues_members_current" to MetricGraphSpec(44.0, "members", ChartType.BAR),
    "dues_members_in_arrears" to MetricGraphSpec(6.0, "members", ChartType.BAR),
    "dues_arrears_amount" to MetricGraphSpec(300_000.0, "UGX", ChartType.AREA),
    "dues_collected_total" to MetricGraphSpec(4_400_000.0, "UGX", ChartType.AREA),
    "capital_utilization" to MetricGraphSpec(84.0, "%", ChartType.AREA),
    "late_fee_pool" to MetricGraphSpec(48_000.0, "UGX", ChartType.AREA),
    "projected_dividend" to MetricGraphSpec(72_000.0, "UGX", ChartType.LINE),
    "seasonal_fluctuation" to MetricGraphSpec(12.0, "%", ChartType.LINE),
    "deposit_frequency" to MetricGraphSpec(68.0, "%", ChartType.PIE),
    "domestic_vs_diaspora" to MetricGraphSpec(66.0, "%", ChartType.PIE),
    "channel_yield" to MetricGraphSpec(58.0, "%", ChartType.PIE),
    "compound_trajectory" to MetricGraphSpec(5_100_000.0, "UGX", ChartType.AREA),
    "reserve_flow" to MetricGraphSpec(240_000.0, "UGX", ChartType.AREA),
    "monthly_volatility" to MetricGraphSpec(18_000.0, "UGX", ChartType.BAR),
    "telecom_latency" to MetricGraphSpec(18.0, "seconds", ChartType.LINE),
    "link_conversion" to MetricGraphSpec(8.4, "%", ChartType.LINE),
    "pledge_fulfillment" to MetricGraphSpec(78.0, "%", ChartType.LINE),
    "avg_donation" to MetricGraphSpec(28_500.0, "UGX", ChartType.BAR),
    "active_donors" to MetricGraphSpec(148.0, "donors", ChartType.BAR),
    "total_raised" to MetricGraphSpec(8_240_000.0, "UGX", ChartType.AREA),
    "tod_heatmap" to MetricGraphSpec(9.0, "hour", ChartType.HEATMAP)
)

private val metricChartSegments = mapOf(
    "payment_method_split" to listOf(
        MetricChartSegment("MTN Mobile Money", 72.0),
        MetricChartSegment("Airtel Money", 20.0),
        MetricChartSegment("Cash / other", 8.0)
    ),
    "deposit_frequency" to listOf(
        MetricChartSegment("Daily", 68.0),
        MetricChartSegment("Weekly", 24.0),
        MetricChartSegment("Other", 8.0)
    ),
    "domestic_vs_diaspora" to listOf(
        MetricChartSegment("Domestic", 66.0),
        MetricChartSegment("Diaspora", 34.0)
    ),
    "channel_yield" to listOf(
        MetricChartSegment("MTN", 58.0),
        MetricChartSegment("Airtel", 32.0),
        MetricChartSegment("Other", 10.0)
    )
)

private val metricHeatmapData = mapOf(
    "tod_heatmap" to listOf(
        listOf(0f, 0f, 0f, 0f, 0f, 1f, 2f, 4f),
        listOf(0f, 0f, 0f, 0f, 1f, 2f, 5f, 7f),
        listOf(0f, 0f, 0f, 1f, 2f, 3f, 6f, 8f),
        listOf(0f, 0f, 0f, 0f, 1f, 3f, 5f, 6f)
    )
)

val allMetrics: List<MetricItem> =
    (generalMetrics + fixedSavingsMetrics + roscaMetrics + sharedBillMetrics +
        lendingInvestmentMetrics + emergencyWelfareMetrics + eventPledgeMetrics +
        sharedWalletMetrics + associationDuesMetrics + fundraiserMetrics).map { metric ->
        val graphSpec = metricGraphSpecs[metric.id]
        metric.copy(
            rawValue = graphSpec?.currentValue,
            unit = graphSpec?.unit.orEmpty(),
            historicalData = metricSampleData[metric.id].orEmpty(),
            preferredChartType = graphSpec?.chartType ?: ChartType.LINE,
            chartSegments = metricChartSegments[metric.id].orEmpty(),
            heatmapData = metricHeatmapData[metric.id].orEmpty()
        )
    }

fun getGraphableMetrics(): List<MetricItem> =
    allMetrics.filter { it.rawValue != null && it.historicalData.isNotEmpty() }

// ──────────────────────────────────────────────────────────────────────────────
//  Sample group context (simulates what would come from the DB / ViewModel)
// ──────────────────────────────────────────────────────────────────────────────

data class SampleGroupContext(
    val groupName: String,
    val groupSubtitle: String,
    val groupType: MetricGroupType,
    val accentColor: Color
)

val sampleGroupContexts = listOf(
    SampleGroupContext(
        groupName     = "Ugandan Bibina",
        groupSubtitle = "ROSCA · Kampala",
        groupType     = MetricGroupType.ROTATING_ROSCA,
        accentColor   = Color(0xFF4ADE80)
    ),
    SampleGroupContext(
        groupName     = "Sunrise Vault",
        groupSubtitle = "Fixed savings · Long-term",
        groupType     = MetricGroupType.FIXED_SAVINGS,
        accentColor   = Color(0xFF60A5FA)
    ),
    SampleGroupContext(
        groupName     = "Kampala Home Utilities",
        groupSubtitle = "Shared bill · Utilities",
        groupType     = MetricGroupType.SHARED_BILL_UTILITY,
        accentColor   = Color(0xFF14B8A6)
    ),
    SampleGroupContext(
        groupName     = "Community Investment Club",
        groupSubtitle = "Lending & investment · Kampala",
        groupType     = MetricGroupType.LENDING_INVESTMENT,
        accentColor   = Color(0xFF8B5CF6)
    ),
    SampleGroupContext(
        groupName     = "Aisha & James' Wedding",
        groupSubtitle = "Wedding fundraiser · Kampala",
        groupType     = MetricGroupType.CONTRIBUTION_FUNDRAISER,
        accentColor   = Color(0xFFF97316)
    ),
    SampleGroupContext(
        groupName     = "Community Welfare Fund",
        groupSubtitle = "Emergency & welfare · Kampala",
        groupType     = MetricGroupType.EMERGENCY_WELFARE,
        accentColor   = Color(0xFFEC4899)
    ),
    SampleGroupContext(
        groupName     = "Community Celebration",
        groupSubtitle = "Event & pledge committee · Kampala",
        groupType     = MetricGroupType.EVENT_PLEDGE,
        accentColor   = Color(0xFFF59E0B)
    ),
    SampleGroupContext(
        groupName     = "Household Shared Wallet",
        groupSubtitle = "Shared wallet · Kampala",
        groupType     = MetricGroupType.SHARED_WALLET,
        accentColor   = Color(0xFF06B6D4)
    ),
    SampleGroupContext(
        groupName     = "Kampala Alumni Association",
        groupSubtitle = "Membership dues · Kampala",
        groupType     = MetricGroupType.ASSOCIATION_DUES,
        accentColor   = Color(0xFF6366F1)
    )
)
