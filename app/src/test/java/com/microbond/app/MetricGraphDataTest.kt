package com.microbond.app

import com.microbond.app.core.dummydata.ChartType
import com.microbond.app.core.dummydata.MetricCategory
import com.microbond.app.core.dummydata.MetricGroupType
import com.microbond.app.core.dummydata.allMetrics
import com.microbond.app.core.dummydata.getGraphableMetrics
import com.microbond.app.core.dummydata.sampleGroupContexts
import com.microbond.app.core.dummydata.sampleSavingsGroups
import com.microbond.app.core.data_classes_and_enums.ContributionFundraiserGroupData
import com.microbond.app.core.data_classes_and_enums.AssociationDuesGroupData
import com.microbond.app.core.data_classes_and_enums.EmergencyWelfareGroupData
import com.microbond.app.core.data_classes_and_enums.EventPledgeGroupData
import com.microbond.app.core.data_classes_and_enums.LendingInvestmentGroupData
import com.microbond.app.core.data_classes_and_enums.RotatingRoscaGroupData
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.data_classes_and_enums.SharedWalletGroupData
import com.microbond.app.core.data_classes_and_enums.SharedBillUtilityGroupData
import com.microbond.app.core.data_classes_and_enums.TargetSavingsGroupData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MetricGraphDataTest {
    @Test
    fun graphableMetricsHaveNumericValuesAndHistoricalSeries() {
        val graphableMetrics = getGraphableMetrics()

        assertTrue(graphableMetrics.isNotEmpty())
        assertTrue(graphableMetrics.all { it.rawValue != null && it.unit.isNotBlank() })
        assertTrue(graphableMetrics.all { it.historicalData.size == 8 })
    }

    @Test
    fun requestedMetricDomainsAreIncludedInCatalog() {
        val metricsById = allMetrics.associateBy { it.id }

        assertEquals(allMetrics.size, metricsById.size)
        assertEquals(
            MetricGroupType.SHARED_BILL_UTILITY,
            metricsById.getValue("average_monthly_spend").groupType
        )
        assertEquals(
            MetricGroupType.LENDING_INVESTMENT,
            metricsById.getValue("active_loan_volume").groupType
        )
        assertEquals(MetricCategory.SOCIAL, metricsById.getValue("chat_activity_index").category)
        assertEquals(MetricCategory.DISPUTES, metricsById.getValue("flagged_transactions").category)
    }

    @Test
    fun pieAndHeatmapMetricsHaveStructuredChartValues() {
        val metricsById = allMetrics.associateBy { it.id }
        val paymentSplit = metricsById.getValue("payment_method_split")
        val donationTimeHeatmap = metricsById.getValue("tod_heatmap")

        assertEquals(ChartType.PIE, paymentSplit.preferredChartType)
        assertEquals(100.0, paymentSplit.chartSegments.sumOf { it.value }, 0.001)
        assertEquals(ChartType.HEATMAP, donationTimeHeatmap.preferredChartType)
        assertFalse(donationTimeHeatmap.heatmapData.isEmpty())
        assertTrue(donationTimeHeatmap.heatmapData.all { it.size == 8 })
        assertNotNull(donationTimeHeatmap.rawValue)
    }

    @Test
    fun everySavingsGroupTypeHasContextAndGraphableMetrics() {
        val expectedGroupTypes = setOf(
            MetricGroupType.FIXED_SAVINGS,
            MetricGroupType.ROTATING_ROSCA,
            MetricGroupType.CONTRIBUTION_FUNDRAISER,
            MetricGroupType.SHARED_BILL_UTILITY,
            MetricGroupType.LENDING_INVESTMENT,
            MetricGroupType.EMERGENCY_WELFARE,
            MetricGroupType.EVENT_PLEDGE,
            MetricGroupType.SHARED_WALLET,
            MetricGroupType.ASSOCIATION_DUES
        )
        val contextGroupTypes = sampleGroupContexts.map { it.groupType }.toSet()
        val graphableGroupTypes = getGraphableMetrics()
            .map { it.groupType }
            .filter { it != MetricGroupType.ALL }
            .toSet()

        assertEquals(expectedGroupTypes, contextGroupTypes)
        assertTrue(graphableGroupTypes.containsAll(expectedGroupTypes))
    }

    @Test
    fun emergencyAndEventMetricsHaveCompleteGraphSamples() {
        val newGroupMetrics = allMetrics.filter {
            it.groupType == MetricGroupType.EMERGENCY_WELFARE ||
                it.groupType == MetricGroupType.EVENT_PLEDGE
        }

        assertTrue(newGroupMetrics.isNotEmpty())
        assertTrue(newGroupMetrics.all {
            it.rawValue != null &&
                it.unit.isNotBlank() &&
                it.historicalData.size == 8
        })
    }

    @Test
    fun sampleGroupsHaveTypedPurposeDataAndUniversalGroupFields() {
        val expectedTypes = SavingsGroupType.values().toSet()

        assertEquals(expectedTypes, sampleSavingsGroups.map { it.type }.toSet())
        assertEquals(sampleSavingsGroups.size, sampleSavingsGroups.map { it.id }.toSet().size)
        assertTrue(sampleSavingsGroups.all {
            it.name.isNotBlank() &&
                it.description.isNotBlank() &&
                it.currencyCode == "UGX" &&
                it.memberCount > 0 &&
                it.totalBalance >= 0L &&
                it.sharedMediaCount > 0 &&
                it.sharedLedgerEntryCount > 0 &&
                it.members.isNotEmpty() &&
                it.adminSettings.assignedAdmins.isNotEmpty()
        })
        assertTrue(sampleSavingsGroups.all { group ->
            when (group.type) {
                SavingsGroupType.GOAL_SAVINGS ->
                    (group.purposeData as? TargetSavingsGroupData)?.let {
                        it.goalAmount > 0L && it.progressPercent in 0.0..100.0
                    } == true
                SavingsGroupType.MERRY_GO_ROUND ->
                    (group.purposeData as? RotatingRoscaGroupData)?.let {
                        it.contributionAmount > 0L && it.payoutOrder.isNotEmpty()
                    } == true
                SavingsGroupType.BILL_SPLIT ->
                    (group.purposeData as? SharedBillUtilityGroupData)?.let {
                        it.connectedUtilities.isNotEmpty() &&
                            it.memberSplitPercentages.values.sum() in 99.9..100.1
                    } == true
                SavingsGroupType.WELFARE_FUND ->
                    (group.purposeData as? EmergencyWelfareGroupData)?.let {
                        it.poolBalance == group.totalBalance && it.replenishmentTarget > it.poolBalance
                    } == true
                SavingsGroupType.EVENT_COMMITTEE ->
                    (group.purposeData as? EventPledgeGroupData)?.let {
                        it.totalRaised == group.totalBalance &&
                            it.totalRaised + it.remainingShortfall == it.totalBudget
                    } == true
                SavingsGroupType.INVESTMENT_CLUB ->
                    (group.purposeData as? LendingInvestmentGroupData)?.let {
                        it.totalGroupCapital == group.totalBalance && it.shareValue > 0L
                    } == true
                SavingsGroupType.FUNDRAISER ->
                    (group.purposeData as? ContributionFundraiserGroupData)?.let {
                        it.raisedAmount == group.totalBalance && it.targetAmount >= it.raisedAmount
                    } == true
                SavingsGroupType.SHARED_WALLET ->
                    (group.purposeData as? SharedWalletGroupData)?.let {
                        it.walletBalance == group.totalBalance &&
                            it.approverNames.isNotEmpty() &&
                            it.spendingCategories.values.sum() > 0L
                    } == true
                SavingsGroupType.ASSOCIATION_DUES ->
                    (group.purposeData as? AssociationDuesGroupData)?.let {
                        it.membersCurrent + it.membersInArrears == group.memberCount &&
                            it.arrearsAmount > 0L &&
                            it.duesAmount > 0L
                    } == true
            }
        })
    }

    @Test
    fun newGroupMetricsHaveGraphSamples() {
        val newTypes = setOf(MetricGroupType.SHARED_WALLET, MetricGroupType.ASSOCIATION_DUES)
        val metrics = allMetrics.filter { it.groupType in newTypes }

        assertTrue(metrics.isNotEmpty())
        assertTrue(metrics.all { it.historicalData.size == 8 && it.rawValue != null })
    }
}
