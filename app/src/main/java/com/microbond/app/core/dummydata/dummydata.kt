package com.microbond.app.core.dummydata

import androidx.compose.ui.graphics.Color
import com.microbond.app.R
import com.microbond.app.core.data_classes_and_enums.ActionPromptData
import com.microbond.app.core.data_classes_and_enums.ChartPreview
import com.microbond.app.core.data_classes_and_enums.GroupSavingsType
import com.microbond.app.core.data_classes_and_enums.MemberSaveStatus
import com.microbond.app.core.data_classes_and_enums.MessageStatus
import com.microbond.app.core.network.ActionMessage
import com.microbond.app.core.data_classes_and_enums.ThreadEvent
import com.microbond.app.core.data_classes_and_enums.ThreadBubbleType
import com.microbond.app.core.data_classes_and_enums.WeeklyReportData
import com.microbond.app.core.data_classes_and_enums.SecurityRulesData
import com.microbond.app.core.data_classes_and_enums.SecurityPollData
import com.microbond.app.core.data_classes_and_enums.PollOption
import com.microbond.app.core.data_classes_and_enums.SavingsReceivedData
import com.microbond.app.core.data_classes_and_enums.SecurityRuleUpdateData
import com.microbond.app.core.data_classes_and_enums.StatChartPoint
import com.microbond.app.core.data_classes_and_enums.StatGroupProgressData
import com.microbond.app.core.data_classes_and_enums.StatDonutSlice
import com.microbond.app.core.data_classes_and_enums.StatGroupSnapshotData
import com.microbond.app.core.data_classes_and_enums.RadarChartPoint
import com.microbond.app.core.data_classes_and_enums.GroupHealthData
import com.microbond.app.core.data_classes_and_enums.HistogramBar
import com.microbond.app.core.data_classes_and_enums.StatHistogramData
import com.microbond.app.core.data_classes_and_enums.PaymentUpdateData
import com.microbond.app.core.data_classes_and_enums.DocumentData
import com.microbond.app.core.data_classes_and_enums.StandardPollData
import com.microbond.app.core.data_classes_and_enums.VideoData
// New chart data classes
import com.microbond.app.core.data_classes_and_enums.CandlestickBar
import com.microbond.app.core.data_classes_and_enums.StatCandlestickData
import com.microbond.app.core.data_classes_and_enums.AreaChartLayer
import com.microbond.app.core.data_classes_and_enums.StatAreaData
import com.microbond.app.core.data_classes_and_enums.WaterfallBar
import com.microbond.app.core.data_classes_and_enums.StatWaterfallData
import com.microbond.app.core.data_classes_and_enums.ScatterPoint
import com.microbond.app.core.data_classes_and_enums.StatScatterData
import com.microbond.app.core.data_classes_and_enums.BoxPlotEntry
import com.microbond.app.core.data_classes_and_enums.StatBoxPlotData
import com.microbond.app.core.data_classes_and_enums.HeatmapCell
import com.microbond.app.core.data_classes_and_enums.StatHeatmapData
import com.microbond.app.core.data_classes_and_enums.SavingsGroupType
import com.microbond.app.core.data_classes_and_enums.GroupMemberSummary
import com.microbond.app.core.data_classes_and_enums.GroupPrivateSettings
import com.microbond.app.core.data_classes_and_enums.GroupAdminSettings
import com.microbond.app.core.data_classes_and_enums.GroupPurposeData
import com.microbond.app.core.data_classes_and_enums.TargetSavingsGroupData
import com.microbond.app.core.data_classes_and_enums.RotatingRoscaGroupData
import com.microbond.app.core.data_classes_and_enums.SharedBillUtilityGroupData
import com.microbond.app.core.data_classes_and_enums.UtilityAccountData
import com.microbond.app.core.data_classes_and_enums.UtilityBillData
import com.microbond.app.core.data_classes_and_enums.UtilityPaymentRecord
import com.microbond.app.core.data_classes_and_enums.EmergencyWelfareGroupData
import com.microbond.app.core.data_classes_and_enums.WelfareRequestSummary
import com.microbond.app.core.data_classes_and_enums.WelfareAssistanceRecord
import com.microbond.app.core.data_classes_and_enums.EventPledgeGroupData
import com.microbond.app.core.data_classes_and_enums.GroupPledgeSummary
import com.microbond.app.core.data_classes_and_enums.LendingInvestmentGroupData
import com.microbond.app.core.data_classes_and_enums.ActiveGroupLoan
import com.microbond.app.core.data_classes_and_enums.ContributionFundraiserGroupData
import com.microbond.app.core.data_classes_and_enums.SharedWalletGroupData
import com.microbond.app.core.data_classes_and_enums.AssociationDuesGroupData


private val chatStatuses = listOf(
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED
    ),
    listOf(
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.PENDING
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.PENDING,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    ),
    listOf(
        MemberSaveStatus.SAVED,
        MemberSaveStatus.MISSED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED,
        MemberSaveStatus.SAVED
    )
)

public val sampleChats = listOf(
    ChartPreview("Reach out to us now (You)", "M", "Microbond", "Yesterday", avatarColor = Color(0xFF527E65), messageStatus = MessageStatus.DELIVERED, memberStatuses = chatStatuses[0]),
    ChartPreview("Justin", "J", "I don't know", "11:50 AM", isFavorite = true, avatarColor = Color(0xFF8D765F), messageStatus = MessageStatus.PENDING, memberStatuses = chatStatuses[1]),
    ChartPreview("Dijah", "D", "Have a nice sleeping day", "11:04 AM", unreadCount = 1, isFavorite = true, avatarColor = Color(0xFF9D746A), messageStatus = MessageStatus.READ, memberStatuses = chatStatuses[2]),
    ChartPreview("+255 755 610171", "7", "Yes papa", "Yesterday", avatarColor = Color(0xFF617F91), messageStatus = MessageStatus.SENT, memberStatuses = chatStatuses[3]),
    ChartPreview("Denes", "D", "Ok", "Yesterday", unreadCount = 2, avatarColor = Color(0xFF96665C), messageStatus = MessageStatus.FAILED, memberStatuses = chatStatuses[4]),
    ChartPreview("Naji Mc Bedings", "N", "Voice call", "9/24/26", avatarColor = Color(0xFF765A8D), messageStatus = MessageStatus.DELIVERED, memberStatuses = chatStatuses[5]),
    ChartPreview(
        "+255 752 466503",
        "7",
        "Voice message (0:04)",
        "9/24/26",
        isFavorite = true,
        avatarColor = Color(0xFF527B91), messageStatus = MessageStatus.READ, memberStatuses = chatStatuses[6]
    ),
    ChartPreview("JAKANANA MIKANDO-BEDINGS", "J", "Photo", "9/23/26", isGroup = true, avatarColor = Color(0xFF8A795A), messageStatus = MessageStatus.DELIVERED, memberStatuses = chatStatuses[7]),
    ChartPreview("Microbond Community", "M", "Welcome to the community", "9/22/26", isGroup = true, avatarColor = Color(0xFF557D70), messageStatus = MessageStatus.READ, memberStatuses = chatStatuses[8])
)

/**
 * Local demo group record. [memberCount] is the complete group size while
 * [members] contains representative sample members, not necessarily the full roster.
 */
public data class SampleGroup(
    val id: String,
    val name: String,
    val initials: String,
    val latestMessage: String,
    val time: String,
    val description: String,
    val currencyCode: String,
    val totalBalance: Long,
    val memberCount: Int,
    val type: SavingsGroupType,
    val purposeData: GroupPurposeData,
    val members: List<GroupMemberSummary>,
    val privateSettings: GroupPrivateSettings,
    val adminSettings: GroupAdminSettings,
    val groupIconUri: String? = null,
    val sharedMediaCount: Int = 0,
    val sharedLedgerEntryCount: Int = 0,
    val unreadCount: Int = 0,
    val isOutgoingMessage: Boolean = true,
    val isFavorite: Boolean = false,
    val isGroup: Boolean = true,
    val avatarColor: Color,
    val messageStatus: MessageStatus = MessageStatus.DELIVERED,
    val groupType: GroupSavingsType = GroupSavingsType.LONG_TERM,
    val isPublic: Boolean = false,
    val memberStatuses: List<MemberSaveStatus> = emptyList(),
    val actionMessages: List<ActionMessage> = emptyList(),
    val userRole: String? = null,
    val isMember: Boolean = true
)
val members = listOf(
    GroupMemberSummary("Peter O.",phoneNumber="6785558656", "Treasurer", 97, MemberSaveStatus.SAVED, ),
    GroupMemberSummary("Ruth N.",phoneNumber="6785558656", "Secretary", 92, MemberSaveStatus.SAVED),
    GroupMemberSummary("John K.", phoneNumber="6785558656","Member", 72, MemberSaveStatus.MISSED)
)
public val sampleSavingsGroups = listOf(
    SampleGroup(
        id = "sunrise-vault",
        name = "Sunrise Vault",
        initials = "S",
        latestMessage = "Monthly contribution approved",
        time = "9:41 AM",
        description = "A long-term target savings circle for a shared community goal.",
        currencyCode = "UGX",
        totalBalance = 3_620_000L,
        sharedMediaCount = 28,
        sharedLedgerEntryCount = 184,
        memberCount = 50,
        type = SavingsGroupType.GOAL_SAVINGS,
        purposeData = TargetSavingsGroupData(
            goalAmount = 5_000_000L,
            targetDate = "2027-06-30",
            progressPercent = 72.4,
            savingsPace = "On track",
            milestonesPercent = listOf(25, 50, 75, 100),
            lockFunds = true,
            hideLeaderboard = false,
            topUpMatchingAmount = 250_000L,
            overagePolicy = "Keep in group wallet",
            allowEarlyExits = false
        ),
        members = listOf(
            GroupMemberSummary("Joseph K.", trustScore = 91, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Grace N.", trustScore = 88, contributionStatus = MemberSaveStatus.PENDING)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 77•••1234",
            personalSavedAmount = 185_000L,
            autoRoundupEnabled = false
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Amina N.", "Joseph K."),
            requireMultiSignature = true,
            minimumTrustScore = 60
        ),
        unreadCount = 2,
        avatarColor = Color(0xFF3E8C6C),
        messageStatus = MessageStatus.READ,
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = false,
        memberStatuses = listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING
        ),
        actionMessages = listOf(
            ActionMessage(
                text = "Total collected: 3,620,000 UGX",
                type = "GOAL_PROGRESS",
                hasBlueTicks = false,
                isAlert = false
            )
        )
    ),
    SampleGroup(
        id = "mtaa-payout-circle",
        name = "Mtaa Payout Circle",
        initials = "M",
        latestMessage = "Your payout turn is this Friday",
        time = "Yesterday",
        description = "A weekly rotating payout circle with a fixed member order.",
        currencyCode = "UGX",
        totalBalance = 1_200_000L,
        sharedMediaCount = 12,
        sharedLedgerEntryCount = 76,
        memberCount = 12,
        type = SavingsGroupType.MERRY_GO_ROUND,
        purposeData = RotatingRoscaGroupData(
            contributionAmount = 100_000L,
            paymentFrequency = "Weekly",
            payoutOrder = listOf(listOf("Mariam K."), listOf("Denis O."), listOf("John B."), listOf("Asha M.")),
            nextPayoutDate = "2026-10-09",
            contributionStreaks = mapOf("Mariam K." to 6, "Denis O." to 4),
            strikeCounts = mapOf("John B." to 1),
            latePaymentPenaltyFee = 5_000L,
            requirePaymentProof = true,
            gracePeriodHours = 24,
            enforcePayoutHold = true
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Denis O.", trustScore = 90, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("John B.", trustScore = 76, contributionStatus = MemberSaveStatus.MISSED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "Airtel Money",
            maskedPaymentDestination = "+256 70•••4567",
            autoPayEnabled = true
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = true,
            requireMemberApproval = true,
            assignedAdmins = listOf("Mariam K."),
            requireMultiSignature = true
        ),
        unreadCount = 1,
        avatarColor = Color(0xFFB57A2A),
        messageStatus = MessageStatus.DELIVERED,
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = true,
        memberStatuses = listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED
        ),
        actionMessages = listOf(
            ActionMessage(
                text = "Your payout turn is scheduled for this round",
                type = "USER_TURN_REMINDER",
                hasBlueTicks = true,
                isAlert = false
            )
        )
    ),
    SampleGroup(
        id = "growth-vault",
        name = "Growth Vault",
        initials = "G",
        latestMessage = "Goal target reached at 86%",
        time = "Mon",
        description = "A locked savings goal for a community land purchase.",
        currencyCode = "UGX",
        totalBalance = 4_300_000L,
        sharedMediaCount = 35,
        sharedLedgerEntryCount = 212,
        memberCount = 24,
        type = SavingsGroupType.GOAL_SAVINGS,
        purposeData = TargetSavingsGroupData(
            goalAmount = 5_000_000L,
            targetDate = "2027-03-31",
            progressPercent = 86.0,
            savingsPace = "Ahead of schedule",
            milestonesPercent = listOf(25, 50, 75, 100),
            lockFunds = true,
            hideLeaderboard = true,
            overagePolicy = "Return excess to contributors",
            allowEarlyExits = false
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Dijah", trustScore = 86, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Naji M.", trustScore = 82, contributionStatus = MemberSaveStatus.SAVED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 77•••8001",
            personalSavedAmount = 320_000L,
            autoRoundupEnabled = true
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Peter S.", "Dijah"),
            requireMultiSignature = true
        ),
        unreadCount = 0,
        avatarColor = Color(0xFF2F7D6C),
        messageStatus = MessageStatus.SENT,
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = true,
        memberStatuses = listOf(
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING,
            MemberSaveStatus.SAVED
        )
    ),
    SampleGroup(
        id = "harbor-rosca",
        name = "Harbor ROSCA",
        initials = "H",
        latestMessage = "Next round begins next week",
        time = "Sun",
        description = "A short-term monthly merry-go-round savings group.",
        currencyCode = "UGX",
        totalBalance = 600_000L,
        sharedMediaCount = 9,
        sharedLedgerEntryCount = 42,
        memberCount = 8,
        type = SavingsGroupType.MERRY_GO_ROUND,
        purposeData = RotatingRoscaGroupData(
            contributionAmount = 75_000L,
            paymentFrequency = "Monthly",
            payoutOrder = listOf(listOf("Hellen A."), listOf("Sam K."), listOf("Amina N.")),
            nextPayoutDate = "2026-10-12",
            contributionStreaks = mapOf("Hellen A." to 3, "Sam K." to 5),
            strikeCounts = mapOf("Amina N." to 1),
            latePaymentPenaltyFee = 3_000L,
            requirePaymentProof = true,
            gracePeriodHours = 48,
            enforcePayoutHold = false
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Sam K.", trustScore = 88, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Amina N.", trustScore = 80, contributionStatus = MemberSaveStatus.MISSED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 78•••2233"
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Hellen A."),
            requireMultiSignature = false
        ),
        unreadCount = 3,
        avatarColor = Color(0xFFD18A32),
        messageStatus = MessageStatus.DELIVERED,
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = false,
        memberStatuses = listOf(
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.MISSED,
            MemberSaveStatus.SAVED,
            MemberSaveStatus.PENDING
        )
    ),
    SampleGroup(
        id = "kampala-home-utilities",
        name = "Kampala Home Utilities",
        initials = "K",
        latestMessage = "UMEME bill due on 12 Oct",
        time = "8:15 AM",
        description = "Roommates pool funds to pay electricity, water, and internet bills.",
        currencyCode = "UGX",
        totalBalance = 420_000L,
        sharedMediaCount = 18,
        sharedLedgerEntryCount = 31,
        memberCount = 4,
        type = SavingsGroupType.BILL_SPLIT,
        purposeData = SharedBillUtilityGroupData(
            connectedUtilities = listOf(
                UtilityAccountData("UMEME", "Meter ••••4831", "Electricity"),
                UtilityAccountData("NWSC", "Account ••••2094", "Water")
            ),
            upcomingBills = listOf(
                UtilityBillData("UMEME", 180_000L, "2026-10-12", "Due soon"),
                UtilityBillData("NWSC", 95_000L, "2026-10-18", "Upcoming")
            ),
            averageMonthlySpend = 275_000L,
            paymentHistory = listOf(
                UtilityPaymentRecord("UMEME", 165_000L, "2026-09-11", "Yaka ••1902"),
                UtilityPaymentRecord("NWSC", 88_000L, "2026-09-17", "NWSC ••6841")
            ),
            memberSplitPercentages = mapOf("Amina N." to 40.0, "Joseph K." to 20.0, "Dijah" to 20.0, "Peter S." to 20.0),
            autoPayEnabled = false,
            approvedVendors = listOf("UMEME", "NWSC", "SafeNet Internet"),
            disputeResolutionAuthority = "Group admins"
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Joseph K.", trustScore = 91),
            GroupMemberSummary("Dijah", trustScore = 88),
            GroupMemberSummary("Peter S.", trustScore = 86)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 77•••1234",
            personalSplitPercent = 40.0,
            personalSavedAmount = 110_000L
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = true,
            requireMemberApproval = false,
            assignedAdmins = listOf("Amina N."),
            requireMultiSignature = true
        ),
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = false,
        avatarColor = Color(0xFF14B8A6),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.PENDING, MemberSaveStatus.SAVED)
    ),
    SampleGroup(
        id = "community-welfare-fund",
        name = "Community Welfare Fund",
        initials = "W",
        latestMessage = "Help request approved by group vote",
        time = "Yesterday",
        description = "A community emergency pool for verified member hardship claims.",
        currencyCode = "UGX",
        totalBalance = 2_400_000L,
        sharedMediaCount = 22,
        sharedLedgerEntryCount = 97,
        memberCount = 50,
        type = SavingsGroupType.WELFARE_FUND,
        purposeData = EmergencyWelfareGroupData(
            poolBalance = 2_400_000L,
            activeHelpRequests = listOf(WelfareRequestSummary("REQ-204", 350_000L, "Awaiting vote", "2026-10-03")),
            assistanceHistory = listOf(WelfareAssistanceRecord("Sarah K.", 200_000L, "Hospital bill", "2026-09-20")),
            replenishmentTarget = 3_000_000L,
            trustedVendorLinks = listOf("Kampala Community Clinic"),
            memberStanding = mapOf("Amina N." to "Active", "Joseph K." to "Active", "Peter S." to "Suspended"),
            approvalProcess = "Group vote",
            maximumPayout = 500_000L,
            directToVendorPayouts = true,
            hardshipWaiversAllowed = true
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Joseph K.", trustScore = 91, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Peter S.", trustScore = 62, contributionStatus = MemberSaveStatus.MISSED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "Airtel Money",
            maskedPaymentDestination = "+256 70•••4567",
            personalSavedAmount = 75_000L,
            allowConfidentialClaims = true
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Amina N.", "Grace N."),
            requireMultiSignature = true,
            minimumTrustScore = 55
        ),
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = false,
        avatarColor = Color(0xFFEC4899),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.MISSED, MemberSaveStatus.PENDING, MemberSaveStatus.SAVED)
    ),
    SampleGroup(
        id = "community-celebration",
        name = "Community Celebration",
        initials = "C",
        latestMessage = "Sponsor pledge received",
        time = "Tue",
        description = "A short-term event committee collecting wedding and celebration pledges.",
        currencyCode = "UGX",
        totalBalance = 8_240_000L,
        sharedMediaCount = 54,
        sharedLedgerEntryCount = 148,
        memberCount = 148,
        type = SavingsGroupType.EVENT_COMMITTEE,
        purposeData = EventPledgeGroupData(
            eventDate = "2026-12-20",
            totalBudget = 12_000_000L,
            totalRaised = 8_240_000L,
            pledges = listOf(
                GroupPledgeSummary("John B.", "Venue contribution", 500_000L, 500_000L),
                GroupPledgeSummary("Mary N.", "Catering", 350_000L, 150_000L),
                GroupPledgeSummary("Sponsor", "Community match", 1_000_000L, 1_000_000L, anonymous = true)
            ),
            remainingShortfall = 3_760_000L,
            sponsorBadges = listOf("Community Sponsor", "Gold Supporter"),
            allowAnonymousContributions = true,
            vendorPaymentsEnabled = true,
            pledgesLocked = false,
            pledgeCap = 1_500_000L,
            milestoneAmounts = listOf(3_000_000L, 6_000_000L, 9_000_000L, 12_000_000L)
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("John B.", trustScore = 86, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Community Sponsor", "Sponsor", contributionStatus = MemberSaveStatus.SAVED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 77•••1234",
            personalPledges = listOf("UGX 100,000 venue support")
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = true,
            requireMemberApproval = false,
            assignedAdmins = listOf("Mary N.", "John B."),
            requireMultiSignature = true
        ),
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = true,
        avatarColor = Color(0xFFF59E0B),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.PENDING, MemberSaveStatus.SAVED, MemberSaveStatus.MISSED)
    ),
    SampleGroup(
        id = "community-investment-club",
        name = "Community Investment Club",
        initials = "I",
        latestMessage = "Quarterly dividend estimate is ready",
        time = "Mon",
        description = "Members contribute share capital and issue peer-guaranteed loans.",
        currencyCode = "UGX",
        totalBalance = 4_850_000L,
        sharedMediaCount = 17,
        sharedLedgerEntryCount = 126,
        memberCount = 32,
        type = SavingsGroupType.INVESTMENT_CLUB,
        purposeData = LendingInvestmentGroupData(
            shareValue = 10_000L,
            totalGroupCapital = 4_850_000L,
            activeLoans = listOf(ActiveGroupLoan("Denis O.", 850_000L, "2026-11-01", "Current")),
            groupRoiPercent = 8.5,
            defaultRatePercent = 4.2,
            interestRatePercent = 5.0,
            guarantorsRequired = 2,
            maximumBorrowingMultiplier = 3.0,
            earlyRepaymentDiscountPercent = 1.0,
            defaultAssetSeizureEnabled = false,
            dividendReinvestmentEnabled = true
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Denis O.", trustScore = 87, contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Asha M.", trustScore = 92, contributionStatus = MemberSaveStatus.SAVED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "Airtel Money",
            maskedPaymentDestination = "+256 70•••4567",
            personalShareCount = 18,
            personalSavedAmount = 180_000L,
            autoPayEnabled = false
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Grace N.", "Asha M."),
            requireMultiSignature = true,
            minimumTrustScore = 70
        ),
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = false,
        avatarColor = Color(0xFF8B5CF6),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.PENDING, MemberSaveStatus.SAVED, MemberSaveStatus.SAVED)
    ),
    SampleGroup(
        id = "aisha-james-wedding-fundraiser",
        name = "Aisha & James' Wedding",
        initials = "A",
        latestMessage = "Campaign is 68% funded",
        time = "Fri",
        description = "A community fundraiser for wedding expenses, with local and diaspora support.",
        currencyCode = "UGX",
        totalBalance = 8_240_000L,
        sharedMediaCount = 54,
        sharedLedgerEntryCount = 148,
        memberCount = 148,
        type = SavingsGroupType.FUNDRAISER,
        purposeData = ContributionFundraiserGroupData(
            targetAmount = 12_000_000L,
            raisedAmount = 8_240_000L,
            targetDate = "2026-12-20",
            activeDonorCount = 148,
            pledgeFulfillmentPercent = 78.0,
            paymentChannelSplitPercent = mapOf("MTN Mobile Money" to 58.0, "Airtel Money" to 32.0, "Other" to 10.0),
            allowAnonymousContributions = true,
            vendorPaymentsEnabled = true
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Community Sponsor", "Sponsor", contributionStatus = MemberSaveStatus.SAVED)
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 77•••1234",
            personalPledges = listOf("UGX 250,000 wedding contribution")
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = true,
            requireMemberApproval = false,
            assignedAdmins = listOf("Aisha K.", "James M."),
            requireMultiSignature = true
        ),
        groupType = GroupSavingsType.SHORT_TERM,
        isPublic = true,
        avatarColor = Color(0xFFF97316),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.PENDING, MemberSaveStatus.SAVED)
    ),
    SampleGroup(
        id = "household-shared-wallet",
        name = "Kato Household Wallet",
        initials = "K",
        latestMessage = "Water bill paid from wallet",
        time = "10:15 AM",
        description = "A shared household wallet for recurring bills and agreed family expenses.",
        currencyCode = "UGX",
        totalBalance = 1_280_000L,
        sharedMediaCount = 12,
        sharedLedgerEntryCount = 38,
        memberCount = 6,
        type = SavingsGroupType.SHARED_WALLET,
        purposeData = SharedWalletGroupData(
            walletPurpose = "Household bills and shared expenses",
            walletBalance = 1_280_000L,
            recentInflows = listOf(300_000L, 250_000L, 200_000L),
            recentOutflows = listOf(180_000L, 95_000L, 120_000L),
            spendingCategories = mapOf("Utilities" to 480_000L, "Groceries" to 320_000L, "Repairs" to 140_000L),
            requiresApprovalAbove = 200_000L,
            approverNames = listOf("Mariam K.", "David K.")
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "MTN Mobile Money",
            maskedPaymentDestination = "+256 77•••4521",
            personalPledges = listOf("UGX 250,000 monthly contribution")
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Mariam K.", "David K."),
            requireMultiSignature = true
        ),
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = false,
        avatarColor = Color(0xFF06B6D4),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.PENDING)
    ),
    SampleGroup(
        id = "kampala-alumni-association-dues",
        name = "Kampala Alumni Association",
        initials = "KA",
        latestMessage = "April dues collection is open",
        time = "Yesterday",
        description = "Monthly alumni membership dues for events, welfare, and association activities.",
        currencyCode = "UGX",
        totalBalance = 4_400_000L,
        sharedMediaCount = 21,
        sharedLedgerEntryCount = 67,
        memberCount = 50,
        type = SavingsGroupType.ASSOCIATION_DUES,
        purposeData = AssociationDuesGroupData(
            duesAmount = 100_000L,
            paymentFrequency = "Monthly",
            nextDueDate = "2026-04-30",
            membersCurrent = 44,
            membersInArrears = 6,
            arrearsAmount = 300_000L,
            lateFeeAmount = 5_000L,
            gracePeriodDays = 7,
            allowInstallments = true
        ),
        members = listOf(
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
            GroupMemberSummary("Amina N.", trustScore = 91, role = "Admin",  contributionStatus = MemberSaveStatus.SAVED),
        ),
        privateSettings = GroupPrivateSettings(
            linkedPaymentMethod = "Airtel Money",
            maskedPaymentDestination = "+256 75•••8802",
            personalPledges = listOf("UGX 100,000 monthly dues")
        ),
        adminSettings = GroupAdminSettings(
            membersCanEditInfo = false,
            membersCanSendMessages = true,
            membersCanAddMembers = false,
            requireMemberApproval = true,
            assignedAdmins = listOf("Peter O.", "Ruth N.")
        ),
        groupType = GroupSavingsType.LONG_TERM,
        isPublic = false,
        avatarColor = Color(0xFF6366F1),
        memberStatuses = listOf(MemberSaveStatus.SAVED, MemberSaveStatus.SAVED, MemberSaveStatus.MISSED, MemberSaveStatus.PENDING)
    )
)

val dummyThreadHistory = listOf(
    ThreadEvent(1, "Sija kulimba", "9:44 PM", isCurrentUser = false, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(2, "St Mary's 😂", "9:46 PM", isCurrentUser = false, type = ThreadBubbleType.REPLY, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943, quotedSender = "~ Am Eli", quotedText = "Sija kulimba"),
    ThreadEvent(3, "Check out this place", "9:46 PM", isCurrentUser = false, type = ThreadBubbleType.IMAGE, imageRes = R.drawable.img_fidelis, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(4, "0:08", "9:47 PM", isCurrentUser = false, type = ThreadBubbleType.VOICE, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(5, "0:19", "9:50 PM", isCurrentUser = false, type = ThreadBubbleType.VOICE, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943),
    ThreadEvent(6, "Yoo", "6:53 PM", isCurrentUser = false, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(7, "Yes pap", "8:03 PM", isCurrentUser = true, status = MessageStatus.DELIVERED),
    ThreadEvent(8, "Plan kitty broda", "8:20 PM", isCurrentUser = false, type = ThreadBubbleType.REPLY, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943, quotedSender = "You", quotedText = "Yes pap"),
    ThreadEvent(9, "Wagana sente", "8:27 PM", isCurrentUser = true, status = MessageStatus.READ),
    ThreadEvent(10, "0:18", "8:29 PM", isCurrentUser = true, type = ThreadBubbleType.VOICE, status = MessageStatus.READ, senderAvatarRes = null),
    ThreadEvent(11, "Ok", "10:02 AM", isCurrentUser = false, senderName = "~ Am Eli", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFF75D7C7), senderAvatarRes = null),
    ThreadEvent(12, "See you at the meeting", "10:04 AM", isCurrentUser = false, senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943),
    ThreadEvent(13, "Looking good", "10:10 AM", isCurrentUser = true, type = ThreadBubbleType.IMAGE, imageResList = listOf(R.drawable.img_1086634216382568373, R.drawable.img_download), status = MessageStatus.READ),
    ThreadEvent(14, "Photos from the meeting", "10:11 AM", isCurrentUser = false, type = ThreadBubbleType.IMAGE, imageResList = listOf(R.drawable.img_fidelis, R.drawable.img_1086634216382568373, R.drawable.img_download, R.drawable.img_808185095673193834, R.drawable.img_896005288366686515, R.drawable.img_964896288911385882), senderName = "~ josephjonez256", senderPhone = "+256 708 870800", senderInitials = "J", senderColor = Color(0xFFE77DA2), senderAvatarRes = R.drawable.img_965037026417633943),
    ThreadEvent(15, "", "10:30 AM", isCurrentUser = false, type = ThreadBubbleType.DEPOSIT, senderName = "Amina N.", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFFE58B88), amount = "UGX 80,000", paymentMethod = "MTN Mobile Money", referenceId = "REF: #DEP-94821", cycleNote = "Weekly Saving Deposit", isVerified = true, fullDate = "Wednesday, 30 Sep 2026 · 10:30 AM", requiredAmount = 100000L, depositedAmount = 80000L),
    ThreadEvent(16, "", "11:15 AM", isCurrentUser = false, type = ThreadBubbleType.DEPOSIT, senderName = "Amina N.", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFFE58B88), amount = "UGX 100,000", paymentMethod = "MTN Mobile Money", referenceId = "REF: #DEP-94822", cycleNote = "Full Weekly Deposit", isVerified = true, fullDate = "Wednesday, 30 Sep 2026 · 11:15 AM", requiredAmount = 100000L, depositedAmount = 100000L),
    ThreadEvent(17, "", "12:00 PM", isCurrentUser = false, type = ThreadBubbleType.DEPOSIT, senderName = "Amina N.", senderPhone = "+256 704 438291", senderInitials = "A", senderColor = Color(0xFFE58B88), amount = "UGX 120,000", paymentMethod = "MTN Mobile Money", referenceId = "REF: #DEP-94823", cycleNote = "Extra Weekly Deposit", isVerified = true, fullDate = "Wednesday, 30 Sep 2026 · 12:00 PM", requiredAmount = 100000L, depositedAmount = 120000L),
    ThreadEvent(18, "", "12:15 PM", isCurrentUser = false, type = ThreadBubbleType.DEPOSIT_STATUS, fullDepositCount = 12, halfDepositCount = 5, notDepositedCount = 8, senderName = "System", senderPhone = "", senderInitials = "S", senderColor = Color.Gray),
    ThreadEvent(
        id = 19,
        text = "",
        time = "8:00 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.WEEKLY_REPORT,
        senderName = "System",
        senderPhone = "",
        senderInitials = "S",
        senderColor = Color.Gray,
        weeklyReport = WeeklyReportData(
            weekLabel = "WEEK 1 SAVING REPORT",
            groupName = "UGANDAN BIBINA",
            dateRange = "21 \u2013 27 Sep 2026",
            dailyTarget = "UGX 10,000",
            memberCount = 50,
            asOf = "28 Sep 2026 \u00b7 8:00 PM",
            membersStart = 48,
            membersJoined = 2,
            membersLeft = 0,
            membersNow = 50,
            eligibleSaveDays = 344,
            dailySavings = listOf(
                Pair("Mon", Triple(42, 4, 2)),
                Pair("Tue", Triple(44, 3, 1)),
                Pair("Wed", Triple(45, 2, 1)),
                Pair("Thu", Triple(45, 4, 1)),
                Pair("Fri", Triple(46, 3, 1)),
                Pair("Sat", Triple(47, 2, 1)),
                Pair("Sun", Triple(46, 3, 1))
            ),
            totalFullDays = 315,
            totalPartialDays = 21,
            totalNoSaveDays = 8,
            membersSavedAtLeastOnce = 49,
            successfulSaveDays = 336,
            fullEveryEligibleDay = 40,
            partialNoMissedDays = 6,
            missed1PlusDays = 3,
            noSavingAllWeek = 1,
            startedWithBalance = 4,
            clearedBalance = 2,
            newBalances = 9,
            endWithBalance = 11,
            busiestDays = "Thu\u2013Sun",
            saversPerDay = 49,
            quietestDay = "Mon",
            quietestCount = 46,
            payoutThisWeek = "2/2 completed (28 Sep): Grace N. + John B.",
            payoutNextWeek = "Mon 5 Oct: Mariam K. + Denis O.",
            remainAfterPayout = 46,
            baselineNote = "no prior-week comparison",
            footerTime = "8:00 PM"
        )
    ),
    ThreadEvent(
        id = 20,
        text = "",
        time = "9:41 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.SECURITY_RULES,
        senderName = "System",
        senderPhone = "",
        senderInitials = "S",
        senderColor = Color.Gray,
        securityRules = SecurityRulesData(
            title = "Group rules",
            rules = listOf(
                "Pay before payout day",
                "One member receives per turn",
                "New members join next cycle"
            ),
            pinnedLabel = "Pinned for everyone",
            viewAllLabel = "View all rules"
        )
    ),
    ThreadEvent(
        id = 21,
        text = "",
        time = "10:42 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.SECURITY_POLL,
        senderName = "Joseph",
        senderPhone = "+256 701 234567",
        senderInitials = "J",
        senderColor = Color(0xFFF96684),
        securityPoll = SecurityPollData(
            questionTitle = "Change the joining rule?",
            questionSubtitle = "Open the group to new members?",
            options = listOf(
                PollOption(id = "1", label = "Agree", percentage = 64, isSelected = true),
                PollOption(id = "2", label = "Keep closed", percentage = 36, isSelected = false)
            ),
            totalVotes = 12,
            footerText = "Majority: 8 of 12",
            replies = listOf(
                com.microbond.app.core.data_classes_and_enums.PollReply(
                    id = "r1",
                    senderName = "Am Eli",
                    senderInitials = "A",
                    senderColor = Color(0xFF1E5244),
                    time = "10:43 PM",
                    text = "Agree"
                ),
                com.microbond.app.core.data_classes_and_enums.PollReply(
                    id = "r2",
                    senderName = "Sami",
                    senderInitials = "S",
                    senderColor = Color(0xFFF96684),
                    time = "10:44 PM",
                    text = "Need faster payouts"
                ),
                com.microbond.app.core.data_classes_and_enums.PollReply(
                    id = "r3",
                    senderName = "Vicent",
                    senderInitials = "V",
                    senderColor = Color(0xFF7E57C2),
                    time = "10:44 PM",
                    text = "Let's wait"
                )
            )
        )
    ),
    ThreadEvent(
        id = 22,
        text = "",
        time = "10:24 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.SAVINGS_RECEIVED,
        senderName = "Joseph",
        senderPhone = "+256 701 234567",
        senderInitials = "J",
        senderColor = Color(0xFFF96684),
        savingsReceived = SavingsReceivedData(
            title = "Savings received",
            subtitle = "Your savings have been delivered",
            recipientName = "Joseph",
            status = "Completed",
            footerText = "Group payout · Confirmed"
        )
    ),
    ThreadEvent(
        id = 23,
        text = "",
        time = "10:46 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.SECURITY_RULE_UPDATE,
        senderName = "Amina",
        senderPhone = "+256 702 111222",
        senderInitials = "A",
        senderColor = Color(0xFF7ED7C4),
        status = MessageStatus.READ,
        securityRuleUpdate = SecurityRuleUpdateData(
            ruleText = "Payouts are sent in member order. New members join after the current cycle.",
            statusLabel = "Active rule",
            approvalNote = "Approved by the group",
            approvalDate = "Today"
        )
    ),
    ThreadEvent(
        id = 24,
        text = "",
        time = "10:47 PM",
        isCurrentUser = true,
        type = ThreadBubbleType.LOCATION,
        senderName = "You",
        senderPhone = "+256 701 000000",
        senderInitials = "Y",
        senderColor = Color(0xFF00A884),
        status = MessageStatus.READ,
        locationPlace = "Mtaa Payout Circle Hub",
        locationSubtitle = "Kampala, Uganda"
    ),
    ThreadEvent(
        id = 25,
        text = "",
        time = "10:48 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_GROUP_PROGRESS,
        senderName = "Amina",
        senderPhone = "+256 702 111222",
        senderInitials = "A",
        senderColor = Color(0xFF7ED7C4),
        status = MessageStatus.READ,
        statGroupProgress = StatGroupProgressData(
            chartPoints = listOf(
                StatChartPoint("May", 62f),
                StatChartPoint("Jun", 68f),
                StatChartPoint("Jul", 75f),
                StatChartPoint("Aug", 81f),
                StatChartPoint("Sep", 87f),
                StatChartPoint("Oct", 91f)
            )
        )
    ),
    ThreadEvent(
        id = 26,
        text = "",
        time = "10:50 PM",
        isCurrentUser = true,
        type = ThreadBubbleType.PAYMENT_UPDATE,
        senderName = "You",
        senderPhone = "+256 701 000000",
        senderInitials = "Y",
        senderColor = Color(0xFF00A884),
        paymentUpdate = PaymentUpdateData()
    ),
    ThreadEvent(
        id = 27,
        text = "",
        time = "10:51 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.DOCUMENT,
        senderName = "Joseph",
        senderPhone = "+256 701 234567",
        senderInitials = "J",
        senderColor = Color(0xFFF96684),
        documentData = DocumentData(
            fileName = "Mtaa_Rules.pdf",
            fileType = "PDF",
            fileSize = "1.2 MB",
            pageCount = 3
        )
    ),
    ThreadEvent(
        id = 28,
        text = "Admin added you to the group.",
        time = "10:52 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.SYSTEM,
        systemMessage = "Admin added you to the group."
    ),
    ThreadEvent(
        id = 29,
        text = "",
        time = "10:55 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.POLL,
        senderName = "Amina",
        senderInitials = "A",
        senderColor = Color(0xFF7ED7C4),
        pollData = StandardPollData(
            question = "What time should we meet tomorrow?",
            options = listOf(
                PollOption("id1", "10:00 AM", 30, true),
                PollOption("id2", "2:00 PM", 70, false)
            )
        )
    ),
    ThreadEvent(
        id = 30,
        text = "",
        time = "11:00 PM",
        isCurrentUser = true,
        type = ThreadBubbleType.VIDEO,
        senderName = "You",
        senderInitials = "Y",
        senderColor = Color(0xFF00A884),
        videoData = VideoData(
            duration = "0:45",
            caption = "Meeting recording"
        )
    ),
    ThreadEvent(
        id = 31,
        text = "",
        time = "10:35 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_GROUP_SNAPSHOT,
        senderName = "Joseph",
        senderPhone = "+256 701 234567",
        senderInitials = "J",
        senderColor = Color(0xFFF96684),
        status = MessageStatus.READ,
        statGroupSnapshot = StatGroupSnapshotData(
            slices = listOf(
                StatDonutSlice("Payouts", 54, 0xFF54B478), // Green
                StatDonutSlice("Saved", 28, 0xFFF07090),   // Pink
                StatDonutSlice("Fees", 18, 0xFFFF9030)     // Orange
            )
        )
    ),
    ThreadEvent(
        id = 32,
        text = "",
        time = "10:24 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.GROUP_HEALTH,
        senderName = "Sami",
        senderPhone = "+256 701 333444",
        senderInitials = "S",
        senderColor = Color(0xFF5CE1E6),
        status = MessageStatus.READ,
        groupHealth = GroupHealthData(
            radarPoints = listOf(
                RadarChartPoint("Trust", 1.0f),
                RadarChartPoint("Speed", 0.7f),
                RadarChartPoint("Attendance", 0.95f),
                RadarChartPoint("Consistency", 0.85f),
                RadarChartPoint("Support", 0.8f)
            )
        )
    ),
    ThreadEvent(
        id = 33,
        text = "",
        time = "10:15 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_HISTOGRAM,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        status = MessageStatus.READ,
        statHistogram = StatHistogramData(
            bars = listOf(
                HistogramBar("May", 150f),
                HistogramBar("Jun", 180f),
                HistogramBar("Jul", 220f),
                HistogramBar("Aug", 350f, highlight = true),
                HistogramBar("Sep", 290f),
                HistogramBar("Oct", 100f)
            )
        )
    ),
    ThreadEvent(
        id = 34,
        text = "",
        time = "10:20 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.ACTION_PROMPT,
        actionPromptData = ActionPromptData(
            message = "Make auto pay by dailing *156*2*6*1#",
            actionText = "DIAL NOW"
        )
    ),
    // ── New chart bubbles ────────────────────────────────────────────────────
    ThreadEvent(
        id = 35,
        text = "",
        time = "10:21 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_CANDLESTICK,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        statCandlestick = StatCandlestickData(
            title = "Savings cycle performance",
            subtitle = "Open · High · Low · Close per week",
            candles = listOf(
                CandlestickBar("W1",  80000f, 110000f,  70000f, 105000f),
                CandlestickBar("W2", 105000f, 120000f,  90000f,  95000f),
                CandlestickBar("W3",  95000f, 130000f,  85000f, 125000f),
                CandlestickBar("W4", 125000f, 135000f, 100000f, 108000f),
                CandlestickBar("W5", 108000f, 150000f, 105000f, 145000f),
                CandlestickBar("W6", 145000f, 155000f, 130000f, 138000f),
            ),
            highValue = "UGX 155,000",
            lowValue  = "UGX 70,000",
            footerText = "Last 6 weeks"
        )
    ),
    ThreadEvent(
        id = 36,
        text = "",
        time = "10:22 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_AREA,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        statArea = StatAreaData(
            title = "Contribution breakdown",
            subtitle = "How savings grew over 6 months",
            xLabels = listOf("May", "Jun", "Jul", "Aug", "Sep", "Oct"),
            layers = listOf(
                AreaChartLayer("Savings",  0xFF4ADE80, listOf(60f, 80f, 95f, 110f, 120f, 135f)),
                AreaChartLayer("Interest", 0xFF60A5FA, listOf(10f, 12f, 14f,  18f,  20f,  22f)),
                AreaChartLayer("Loans",    0xFFF97316, listOf(20f, 15f, 18f,  22f,  25f,  20f))
            ),
            totalValue = "UGX 3.6M",
            footerText = "Last 6 months"
        )
    ),
    ThreadEvent(
        id = 37,
        text = "",
        time = "10:23 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_WATERFALL,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        statWaterfall = StatWaterfallData(
            title = "Cash flow this cycle",
            subtitle = "Inflows & outflows breakdown",
            bars = listOf(
                WaterfallBar("Open",     500000f, isTotal = true),
                WaterfallBar("Deposits", 320000f),
                WaterfallBar("Interest",  18000f),
                WaterfallBar("Payouts", -280000f),
                WaterfallBar("Fees",     -12000f),
                WaterfallBar("Net",      546000f, isTotal = true)
            ),
            netValue = "+UGX 46,000",
            footerText = "Sep 2026 cycle"
        )
    ),
    ThreadEvent(
        id = 38,
        text = "",
        time = "10:24 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_SCATTER,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        statScatter = StatScatterData(
            title = "Member correlation",
            subtitle = "Deposit rate vs. on-time rate",
            points = listOf(
                ScatterPoint(92f, 95f, "Grace"),
                ScatterPoint(88f, 90f, "John"),
                ScatterPoint(75f, 80f, "Amina"),
                ScatterPoint(60f, 65f, "Denis"),
                ScatterPoint(55f, 50f, "Sami"),
                ScatterPoint(40f, 45f, "Lily"),
                ScatterPoint(30f, 30f, "Fred"),
                ScatterPoint(95f, 98f, "Rita"),
                ScatterPoint(70f, 72f, "Peter"),
                ScatterPoint(85f, 88f, "Mary")
            ),
            correlationValue = "r = 0.98 (very strong)"
        )
    ),
    ThreadEvent(
        id = 39,
        text = "",
        time = "10:25 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_BOXPLOT,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        statBoxPlot = StatBoxPlotData(
            title = "Deposit distribution",
            subtitle = "Min · Q1 · Median · Q3 · Max per member",
            entries = listOf(
                BoxPlotEntry("Grace", 60000f,  80000f, 100000f, 115000f, 140000f, 0xFF4ADE80),
                BoxPlotEntry("John",  50000f,  75000f,  90000f, 110000f, 130000f, 0xFF60A5FA),
                BoxPlotEntry("Amina", 40000f,  65000f,  85000f, 100000f, 120000f, 0xFFF97316),
                BoxPlotEntry("Denis", 20000f,  45000f,  70000f,  90000f, 115000f, 0xFFE77DA2),
                BoxPlotEntry("Sami",  10000f,  30000f,  55000f,  75000f, 100000f, 0xFFA78BFA)
            ),
            footerText = "Last 6 months per member"
        )
    ),
    ThreadEvent(
        id = 40,
        text = "",
        time = "10:26 PM",
        isCurrentUser = false,
        type = ThreadBubbleType.STAT_HEATMAP,
        senderName = "System",
        senderInitials = "S",
        senderColor = Color(0xFF8696A0),
        statHeatmap = StatHeatmapData(
            title = "Member payment heatmap",
            subtitle = "Deposit activity per member per month",
            rowLabels = listOf("Grace", "John", "Amina", "Denis", "Sami", "Lily"),
            colLabels = listOf("May", "Jun", "Jul", "Aug", "Sep", "Oct"),
            cells = listOf(
                HeatmapCell("Grace", "May", 1f), HeatmapCell("Grace", "Jun", 1f), HeatmapCell("Grace", "Jul", 1f),  HeatmapCell("Grace", "Aug", 0.5f), HeatmapCell("Grace", "Sep", 1f),  HeatmapCell("Grace", "Oct", 1f),
                HeatmapCell("John",  "May", 1f), HeatmapCell("John",  "Jun", 0f), HeatmapCell("John",  "Jul", 1f),  HeatmapCell("John",  "Aug", 1f),   HeatmapCell("John",  "Sep", 0.5f), HeatmapCell("John",  "Oct", 1f),
                HeatmapCell("Amina", "May", 0f), HeatmapCell("Amina", "Jun", 1f), HeatmapCell("Amina", "Jul", 0.5f),HeatmapCell("Amina", "Aug", 1f),   HeatmapCell("Amina", "Sep", 1f),  HeatmapCell("Amina", "Oct", 0f),
                HeatmapCell("Denis", "May", 1f), HeatmapCell("Denis", "Jun", 0f), HeatmapCell("Denis", "Jul", 0f),  HeatmapCell("Denis", "Aug", 0.5f), HeatmapCell("Denis", "Sep", 0f),  HeatmapCell("Denis", "Oct", 1f),
                HeatmapCell("Sami",  "May", 0f), HeatmapCell("Sami",  "Jun", 1f), HeatmapCell("Sami",  "Jul", 1f),  HeatmapCell("Sami",  "Aug", 0f),   HeatmapCell("Sami",  "Sep", 1f),  HeatmapCell("Sami",  "Oct", 0.5f),
                HeatmapCell("Lily",  "May", 1f), HeatmapCell("Lily",  "Jun", 1f), HeatmapCell("Lily",  "Jul", 0f),  HeatmapCell("Lily",  "Aug", 1f),   HeatmapCell("Lily",  "Sep", 0.5f), HeatmapCell("Lily",  "Oct", 1f)
            ),
            footerText = "Green = paid · Red = missed"
        )
    )
)
