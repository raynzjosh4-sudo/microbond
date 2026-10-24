package com.microbond.app.core.components.groupintrocard.rotating

import com.google.gson.annotations.SerializedName
import com.microbond.app.core.network.DiscoverableContactGroup
import com.microbond.app.core.network.RotationSlotMember
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Raw API response payload models for Rotating Group (MERRY_GO_ROUND / ROSCA).
 * Returned by POST /api/v1/contacts/discover-groups or GET /api/v1/groups/{group_id}/settings/summary
 */
data class RotatingGroupApiData(
    @SerializedName("group_id", alternate = ["id"]) val groupId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("group_image_url") val groupImageUrl: String? = null,
    @SerializedName("type") val type: String = "MERRY_GO_ROUND",
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("member_count") val memberCount: Int = 0,
    @SerializedName("max_members") val maxMembers: Int = 10,
    @SerializedName("visibility") val visibility: String = "PUBLIC",
    @SerializedName("invite_code") val inviteCode: String? = null,
    @SerializedName("is_member") val isMember: Boolean? = null,
    @SerializedName("user_role") val userRole: String? = null,
    @SerializedName("created_by_user_id") val createdByUserId: String? = null,
    @SerializedName("rotating_details") val rotatingDetails: RotatingDetails = RotatingDetails(),
    @SerializedName("financial_rules") val financialRules: FinancialRules = FinancialRules(),
    @SerializedName("governance_gates", alternate = ["governance"]) val governanceGates: GovernanceGates = GovernanceGates(),
    @SerializedName("mutual_contacts") val mutualContacts: List<MutualContact> = emptyList()
)

data class RotatingDetails(
    @SerializedName("contribution_amount") val contributionAmount: Double = 0.0,
    @SerializedName("collection_frequency_days") val collectionFrequencyDays: Int = 0,
    @SerializedName("collection_time") val collectionTime: String = "",
    @SerializedName("total_expected_pool_per_cycle") val totalExpectedPoolPerCycle: Double = 0.0,
    @SerializedName("number_of_cycles") val numberOfCycles: Int = 1,
    @SerializedName("current_cycle") val currentCycle: Int = 1,
    @SerializedName("group_start_time") val groupStartTime: Long = 0L,
    @SerializedName("payout_order_strategy") val payoutOrderStrategy: String = "FIRST_COME_FIRST_SERVE",
    @SerializedName("is_payout_order_locked") val isPayoutOrderLocked: Boolean = false,
    @SerializedName("allow_partial_payments") val allowPartialPayments: Boolean = false,
    @SerializedName("allow_advance_deposits") val allowAdvanceDeposits: Boolean = true
)

data class FinancialRules(
    @SerializedName("enable_late_fees") val enableLateFees: Boolean = false,
    @SerializedName("late_fee_amount") val lateFeeAmount: Double = 0.0,
    @SerializedName("enable_grace_period") val enableGracePeriod: Boolean = false,
    @SerializedName("grace_period_hours") val gracePeriodHours: Int = 0,
    @SerializedName("arrears_policy") val arrearsPolicy: String = "NO_FEE",
    @SerializedName("late_fee_policy") val lateFeePolicy: String? = null,
    @SerializedName("allow_partial_payments") val allowPartialPayments: Boolean = false,
    @SerializedName("allow_advance_deposits") val allowAdvanceDeposits: Boolean = true,
    @SerializedName("enforce_payout_holds") val enforcePayoutHolds: Boolean = true,
    @SerializedName("interest_on_late_payment") val interestOnLatePayment: Double = 0.0
)

data class GovernanceGates(
    @SerializedName("requires_admin_approval") val requiresAdminApproval: Boolean = false,
    @SerializedName("require_guarantor", alternate = ["require_guarantors"]) val requireGuarantors: Boolean = false,
    @SerializedName("guarantor_count") val guarantorCount: Int = 0,
    @SerializedName("reputation_score_min", alternate = ["minimum_trust_score"]) val minimumTrustScore: Double = 0.0,
    @SerializedName("minimum_kyc_tier") val minimumKycTier: Int = 1,
    @SerializedName("allow_turn_swaps") val allowTurnSwaps: Boolean = true,
    @SerializedName("guarantor_action") val guarantorAction: String? = null,
    @SerializedName("members_can_invite_via_link") val membersCanInviteViaLink: Boolean = false,
    @SerializedName("members_can_edit_settings") val membersCanEditSettings: Boolean = false
)

data class MutualContact(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("display_name", alternate = ["profile_name", "name"]) val profileName: String = "",
    @SerializedName("phone_number") val phoneNumber: String = "",
    @SerializedName("profile_image_url", alternate = ["avatar_url", "image_url"]) val profileImageUrl: String? = null,
    @SerializedName("payout_position", alternate = ["slot_number"]) val payoutPosition: Int = 1
)

/**
 * Live turn roster item representation for the Rotating Group.
 */
data class RotationRosterSlot(
    val position: Int,
    val memberName: String?,
    val statusText: String, // "Paid Out", "Receiving this Friday", "OPEN SLOT", "Scheduled"
    val isPaidOut: Boolean = false,
    val isCurrentRecipient: Boolean = false,
    val isOpenSlot: Boolean = false,
    val isScheduled: Boolean = false
)

/**
 * Result of evaluating whether a user meets this Rotating Group's entry requirements.
 */
data class RotatingGroupConditionCheck(
    val meetsKyc: Boolean = true,
    val meetsTrustScore: Boolean = true,
    val hasRequiredGuarantor: Boolean = true,
    val canJoinImmediately: Boolean = true,
    val statusBadgeText: String = "You meet entry requirements",
    val statusDetailsText: String = "Tier 2 KYC & guarantor verified"
)

/**
 * Curated UI State parsed from the raw API data.
 */
data class RotatingGroupPreviewUiState(
    val groupId: String,
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int = 0,
    val maxMembers: Int = 0,
    // Card & Header Display (Derived from API fields)
    val groupTypeLabel: String, // e.g. "Rotating Circle" derived from collectionFrequencyDays & type
    val createdDateText: String, // formatted from data.rotatingDetails.groupStartTime
    val headerSubtitle: String, // e.g. "8 of 10 members"
    val memberCountText: String, // "8 of 10 members • 2 open"
    val cardMemberText: String, // "8 of 10 members"
    
    // Financial Deal (Sheet display)
    val contributionFormatted: String, // "50,000 UGX"
    val frequencyFormatted: String, // "Weekly"
    val potPayoutFormatted: String, // "500,000 UGX Lump Sum"
    val potPayoutAmountOnly: String, // "500,000 UGX"
    val currentRoundFormatted: String, // "Round 3 of 10"
    val collectionTimeText: String, // "Weekly at 17:00"
    val cycleScheduleText: String, // "Every cycle at 17:00"
    val sheetHeaderSubtitle: String, // "Weekly Rotating Savings Circle • 8 of 10 members • 2 open"
    
    // Roster (Sheet display)
    val nextOpenSlot: Int, // 4
    val nextOpenSlotText: String, // "Slot #4 is open for you"
    val allowTurnSwaps: Boolean,
    val rosterSlots: List<RotationRosterSlot>,
    
    // Social Trust (Mutual Contacts)
    val mutualContacts: List<MutualContact>,
    val mutualContactsSnippet: String, // e.g. "Brenda and David are active"
    
    // Rules & Governance (Sheet display)
    val requiredKycText: String, // "Tier 2 (National ID Verified)"
    val latePenaltyText: String, // "24h grace period, then 5,000 UGX late fee"
    val arrearsPolicyText: String, // "Strike and Suspend"
    val guarantorRequirementText: String, // "1 mutual contact must vouch"
    
    // Card Qualification Status
    val conditionCheck: RotatingGroupConditionCheck
) {
    companion object {
        fun fromApiData(
            data: RotatingGroupApiData,
            members: List<RotationSlotMember>? = null,
            userKycTier: Int = 2,
            userTrustScore: Int = 75
        ): RotatingGroupPreviewUiState {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            val currency = data.currency

            val contributionText = "${formatter.format(data.rotatingDetails.contributionAmount.toLong())} $currency"
            val potPayoutAmountOnly = "${formatter.format(data.rotatingDetails.totalExpectedPoolPerCycle.toLong())} $currency"
            val potPayoutText = "$potPayoutAmountOnly Lump Sum"
            
            val frequencyText = when (data.rotatingDetails.collectionFrequencyDays) {
                1 -> "Daily"
                7 -> "Weekly"
                14 -> "Bi-Weekly"
                30, 31 -> "Monthly"
                in 2..365 -> "Every ${data.rotatingDetails.collectionFrequencyDays} days"
                else -> ""
            }

            val groupTypeLabel = if (frequencyText.isNotBlank()) {
                "$frequencyText Rotating Circle"
            } else {
                "Rotating Savings Circle"
            }

            val cycleScheduleText = if (frequencyText.isNotBlank() && data.rotatingDetails.collectionTime.isNotBlank()) {
                "$frequencyText at ${data.rotatingDetails.collectionTime}"
            } else if (data.rotatingDetails.collectionTime.isNotBlank()) {
                "Every cycle at ${data.rotatingDetails.collectionTime}"
            } else {
                "Every cycle"
            }

            val collectionTimeText = if (frequencyText.isNotBlank() && data.rotatingDetails.collectionTime.isNotBlank()) {
                "$frequencyText at ${data.rotatingDetails.collectionTime}"
            } else if (frequencyText.isNotBlank()) {
                frequencyText
            } else {
                "Every cycle"
            }

            val createdDateText = if (data.rotatingDetails.groupStartTime > 0L) {
                "created " + SimpleDateFormat("M/d/yy", Locale.getDefault()).format(Date(data.rotatingDetails.groupStartTime))
            } else {
                "created recently"
            }

            val remainingSpots = (data.maxMembers - data.memberCount).coerceAtLeast(0)
            val memberCountText = if (data.maxMembers > 0) {
                "${data.memberCount} of ${data.maxMembers} members • $remainingSpots open"
            } else {
                "${data.memberCount} members"
            }

            val cardMemberText = if (data.maxMembers > 0) {
                "${data.memberCount} of ${data.maxMembers} members"
            } else {
                "${data.memberCount} members"
            }

            val sheetHeaderSubtitle = if (frequencyText.isNotBlank()) {
                "$frequencyText Rotating Savings Circle • $memberCountText"
            } else {
                "Rotating Savings Circle • $memberCountText"
            }

            val currentRoundText = "Round ${data.rotatingDetails.currentCycle} of ${data.rotatingDetails.numberOfCycles}"

            // Mutual contacts formatting
            val mutualSnippet = when {
                data.mutualContacts.size >= 2 -> {
                    "${data.mutualContacts[0].profileName} and ${data.mutualContacts[1].profileName} are active"
                }
                data.mutualContacts.size == 1 -> {
                    "${data.mutualContacts[0].profileName} is active in this circle"
                }
                else -> "Verified circle members"
            }

            // Condition evaluation
            val meetsKyc = userKycTier >= data.governanceGates.minimumKycTier
            val meetsTrustScore = userTrustScore >= data.governanceGates.minimumTrustScore
            val hasGuarantorOption = !data.governanceGates.requireGuarantors || data.mutualContacts.isNotEmpty()
            val canJoin = meetsKyc && meetsTrustScore && hasGuarantorOption

            val conditionCheck = when {
                canJoin && data.governanceGates.requireGuarantors -> {
                    RotatingGroupConditionCheck(
                        meetsKyc = true,
                        meetsTrustScore = true,
                        hasRequiredGuarantor = true,
                        canJoinImmediately = true,
                        statusBadgeText = "✓ You meet entry requirements",
                        statusDetailsText = "Mutual contact available to vouch"
                    )
                }
                canJoin -> {
                    RotatingGroupConditionCheck(
                        meetsKyc = true,
                        meetsTrustScore = true,
                        hasRequiredGuarantor = true,
                        canJoinImmediately = true,
                        statusBadgeText = "✓ Pre-approved to join",
                        statusDetailsText = "Direct entry available"
                    )
                }
                !meetsKyc -> {
                    RotatingGroupConditionCheck(
                        meetsKyc = false,
                        meetsTrustScore = meetsTrustScore,
                        hasRequiredGuarantor = hasGuarantorOption,
                        canJoinImmediately = false,
                        statusBadgeText = "⚠️ KYC Tier ${data.governanceGates.minimumKycTier} Required",
                        statusDetailsText = "Verify your National ID before joining"
                    )
                }
                else -> {
                    RotatingGroupConditionCheck(
                        meetsKyc = meetsKyc,
                        meetsTrustScore = false,
                        hasRequiredGuarantor = hasGuarantorOption,
                        canJoinImmediately = false,
                        statusBadgeText = "⚠️ Trust score ${data.governanceGates.minimumTrustScore.toInt()}+ needed",
                        statusDetailsText = "Current score: $userTrustScore"
                    )
                }
            }

            // Roster slots: prioritize live member roster if supplied, otherwise derive from live group counts
            val rosterSlots: List<RotationRosterSlot> = if (!members.isNullOrEmpty()) {
                members.map { m ->
                    val isOpen = m.status.equals("OPEN", ignoreCase = true) || m.userId.isBlank()
                    val isPaid = m.status.equals("PAID_OUT", ignoreCase = true) || m.status.equals("PAID", ignoreCase = true)
                    val isCurrent = m.status.equals("CURRENT", ignoreCase = true) || m.status.equals("ACTIVE", ignoreCase = true)
                    val isScheduled = !isOpen && !isPaid && !isCurrent
                    RotationRosterSlot(
                        position = m.slotNumber,
                        memberName = if (isOpen) "OPEN FOR YOU" else m.profileName.ifBlank { "Member ${m.slotNumber}" },
                        statusText = when {
                            isOpen -> "Assigned on joining"
                            isPaid -> "Paid Out"
                            isCurrent -> "Active Turn"
                            else -> m.payoutDate?.let { "Scheduled $it" } ?: "Scheduled Round ${m.slotNumber}"
                        },
                        isPaidOut = isPaid,
                        isCurrentRecipient = isCurrent,
                        isOpenSlot = isOpen,
                        isScheduled = isScheduled
                    )
                }
            } else {
                val list = mutableListOf<RotationRosterSlot>()
                val activeCount = data.memberCount.coerceAtLeast(0)
                val totalSlots = if (data.maxMembers > 0) data.maxMembers else (activeCount + 2)
                for (pos in 1..activeCount) {
                    val contact = data.mutualContacts.getOrNull(pos - 1)
                    val name = contact?.profileName ?: "Member $pos"
                    val isCurrentTurn = pos == data.rotatingDetails.currentCycle
                    val isPaid = pos < data.rotatingDetails.currentCycle
                    list.add(
                        RotationRosterSlot(
                            position = pos,
                            memberName = name,
                            statusText = when {
                                isCurrentTurn -> "Active Turn"
                                isPaid -> "Paid Out"
                                else -> "Scheduled Round $pos"
                            },
                            isPaidOut = isPaid,
                            isCurrentRecipient = isCurrentTurn,
                            isOpenSlot = false,
                            isScheduled = !isPaid && !isCurrentTurn
                        )
                    )
                }
                if (activeCount < totalSlots) {
                    val nextPos = activeCount + 1
                    list.add(
                        RotationRosterSlot(
                            position = nextPos,
                            memberName = "OPEN FOR YOU",
                            statusText = "Assigned on joining",
                            isOpenSlot = true
                        )
                    )
                    for (pos in (nextPos + 1)..totalSlots.coerceAtMost(nextPos + 2)) {
                        list.add(
                            RotationRosterSlot(
                                position = pos,
                                memberName = null,
                                statusText = "Scheduled Round $pos",
                                isScheduled = true
                            )
                        )
                    }
                }
                list
            }

            val nextSlot = rosterSlots.firstOrNull { it.isOpenSlot }?.position ?: (data.memberCount + 1)

            val lateFeeText = when {
                data.financialRules.lateFeePolicy != null && data.financialRules.lateFeePolicy.equals("NO_FEE", ignoreCase = true) -> "No late fee"
                data.financialRules.enableLateFees && data.financialRules.lateFeeAmount > 0 -> {
                    val grace = if (data.financialRules.gracePeriodHours > 0) "${data.financialRules.gracePeriodHours}h grace period, then " else ""
                    "$grace${formatter.format(data.financialRules.lateFeeAmount.toLong())} $currency late fee"
                }
                else -> "No late fee"
            }

            val guarantorRequirementText = when {
                !data.governanceGates.requireGuarantors -> "No guarantor required"
                data.governanceGates.guarantorCount > 0 -> "${data.governanceGates.guarantorCount} mutual contact must vouch"
                else -> "Mutual contact vouching"
            }

            val arrearsPolicyText = data.financialRules.lateFeePolicy?.takeIf { it.isNotBlank() }?.replace('_', ' ')?.lowercase()?.replaceFirstChar { it.uppercase() }
                ?: data.financialRules.arrearsPolicy.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }

            return RotatingGroupPreviewUiState(
                groupId = data.groupId,
                name = data.name,
                description = data.description,
                avatarUrl = data.groupImageUrl,
                memberCount = data.memberCount,
                maxMembers = data.maxMembers,
                groupTypeLabel = groupTypeLabel,
                createdDateText = createdDateText,
                headerSubtitle = cardMemberText,
                memberCountText = memberCountText,
                cardMemberText = cardMemberText,
                contributionFormatted = contributionText,
                frequencyFormatted = frequencyText.ifBlank { "Per cycle" },
                potPayoutFormatted = potPayoutText,
                potPayoutAmountOnly = potPayoutAmountOnly,
                currentRoundFormatted = currentRoundText,
                collectionTimeText = collectionTimeText,
                cycleScheduleText = cycleScheduleText,
                sheetHeaderSubtitle = sheetHeaderSubtitle,
                nextOpenSlot = nextSlot,
                nextOpenSlotText = "Slot #$nextSlot is open for you",
                allowTurnSwaps = data.governanceGates.allowTurnSwaps,
                rosterSlots = rosterSlots,
                mutualContacts = data.mutualContacts,
                mutualContactsSnippet = mutualSnippet,
                requiredKycText = "Tier ${data.governanceGates.minimumKycTier} (National ID Verified)",
                latePenaltyText = lateFeeText,
                arrearsPolicyText = arrearsPolicyText,
                guarantorRequirementText = guarantorRequirementText,
                conditionCheck = conditionCheck
            )
        }

        /**
         * Builds UI state directly from real backend DiscoverableContactGroup metadata.
         * Guarantees exact member count, real mutual contacts, and strict frequency mapping (NO default to Weekly).
         */
        fun fromDiscoverableGroup(
            group: DiscoverableContactGroup,
            userKycTier: Int = 2,
            userTrustScore: Int = 75
        ): RotatingGroupPreviewUiState {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            val currency = group.currency.ifBlank { "UGX" }

            val contributionText = if (group.contributionAmount != null && group.contributionAmount > 0) {
                "${formatter.format(group.contributionAmount.toLong())} $currency"
            } else {
                "-- $currency"
            }

            val totalPool = if (group.contributionAmount != null && group.maxMembers > 0) {
                group.contributionAmount * group.maxMembers
            } else null

            val potPayoutAmountOnly = if (totalPool != null && totalPool > 0) {
                "${formatter.format(totalPool.toLong())} $currency"
            } else {
                "-- $currency"
            }
            val potPayoutText = "$potPayoutAmountOnly Lump Sum"

            // Frequency: STRICTLY from the API, NEVER assume Weekly!
            val rawFreq = group.contributionFrequency?.trim()?.uppercase()
            val frequencyText = when (rawFreq) {
                "DAILY" -> "Daily"
                "WEEKLY" -> "Weekly"
                "BI_WEEKLY", "BI-WEEKLY" -> "Bi-Weekly"
                "MONTHLY" -> "Monthly"
                null, "" -> ""
                else -> group.contributionFrequency.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            }

            val cycleScheduleText = if (frequencyText.isNotBlank()) {
                "Every $frequencyText cycle"
            } else {
                "Every cycle"
            }

            val groupTypeLabel = if (frequencyText.isNotBlank()) {
                "$frequencyText Rotating Circle"
            } else {
                "Rotating Savings Circle"
            }

            val remainingSpots = (group.maxMembers - group.memberCount).coerceAtLeast(0)
            val memberCountText = if (group.maxMembers > 0) {
                "${group.memberCount} of ${group.maxMembers} members • $remainingSpots open"
            } else {
                "${group.memberCount} members"
            }

            val cardMemberText = if (group.maxMembers > 0) {
                "${group.memberCount} of ${group.maxMembers} members"
            } else {
                "${group.memberCount} members"
            }

            val sheetHeaderSubtitle = if (frequencyText.isNotBlank()) {
                "$frequencyText Rotating Savings Circle • $memberCountText"
            } else {
                "Rotating Savings Circle • $memberCountText"
            }

            val mappedContacts = group.contactMembers.mapIndexed { index, cm ->
                MutualContact(
                    userId = cm.id,
                    profileName = cm.profileName,
                    phoneNumber = cm.phoneNumber.orEmpty(),
                    payoutPosition = index + 1
                )
            }

            val mutualSnippet = when {
                mappedContacts.size >= 2 -> "${mappedContacts[0].profileName} and ${mappedContacts[1].profileName} are active"
                mappedContacts.size == 1 -> "${mappedContacts[0].profileName} is active in this circle"
                else -> "Verified circle members"
            }

            val nextSlot = (group.memberCount + 1).coerceAtMost(group.maxMembers.coerceAtLeast(1))
            val nextSlotText = "Slot #$nextSlot is open for you"

            // Live line slots derived from real group members
            val roster = mutableListOf<RotationRosterSlot>()
            for (pos in 1..group.memberCount) {
                val contact = mappedContacts.getOrNull(pos - 1)
                val name = contact?.profileName ?: "Member $pos"
                roster.add(
                    RotationRosterSlot(
                        position = pos,
                        memberName = name,
                        statusText = "Paid Out",
                        isPaidOut = true
                    )
                )
            }
            if (group.memberCount < group.maxMembers) {
                roster.add(
                    RotationRosterSlot(
                        position = nextSlot,
                        memberName = "OPEN FOR YOU",
                        statusText = "Assigned on joining",
                        isOpenSlot = true
                    )
                )
            }
            for (pos in (nextSlot + 1)..group.maxMembers.coerceAtMost(nextSlot + 3)) {
                roster.add(
                    RotationRosterSlot(
                        position = pos,
                        memberName = null,
                        statusText = "Scheduled Round $pos",
                        isScheduled = true
                    )
                )
            }

            val kycText = if (!group.requiredKycTier.isNullOrBlank()) {
                "${group.requiredKycTier} (National ID Verified)"
            } else {
                "Tier 2 (National ID Verified)"
            }

            val guarantorText = if (group.requireGuarantors) {
                "1 mutual contact must vouch"
            } else {
                "No guarantor required"
            }

            return RotatingGroupPreviewUiState(
                groupId = group.id,
                name = group.name,
                description = group.description.ifBlank { "Rotating savings circle." },
                avatarUrl = group.groupImageUrl,
                memberCount = group.memberCount,
                maxMembers = group.maxMembers,
                groupTypeLabel = groupTypeLabel,
                createdDateText = "created recently",
                headerSubtitle = cardMemberText,
                memberCountText = memberCountText,
                cardMemberText = cardMemberText,
                contributionFormatted = contributionText,
                frequencyFormatted = frequencyText.ifBlank { "Per cycle" },
                potPayoutFormatted = potPayoutText,
                potPayoutAmountOnly = potPayoutAmountOnly,
                currentRoundFormatted = "Round 1 of ${group.maxMembers.coerceAtLeast(1)}",
                collectionTimeText = if (frequencyText.isNotBlank()) frequencyText else "Per cycle",
                cycleScheduleText = cycleScheduleText,
                sheetHeaderSubtitle = sheetHeaderSubtitle,
                nextOpenSlot = nextSlot,
                nextOpenSlotText = nextSlotText,
                allowTurnSwaps = true,
                rosterSlots = roster,
                mutualContacts = mappedContacts,
                mutualContactsSnippet = mutualSnippet,
                requiredKycText = kycText,
                latePenaltyText = "24h grace period, late fee policy applies",
                arrearsPolicyText = "Strike and suspend",
                guarantorRequirementText = guarantorText,
                conditionCheck = RotatingGroupConditionCheck(
                    meetsKyc = true,
                    meetsTrustScore = (group.minimumTrustScore ?: 0) <= userTrustScore,
                    hasRequiredGuarantor = !group.requireGuarantors || mappedContacts.isNotEmpty(),
                    canJoinImmediately = true,
                    statusBadgeText = "✓ Pre-approved to join",
                    statusDetailsText = "Direct entry available"
                )
            )
        }

        /**
         * Clean minimal fallback for group thread with only the entity name known.
         * Contains NO hardcoded 8 members, NO hardcoded 2 contacts, and NO hardcoded 'Weekly'.
         */
        fun fromGroupNameOnly(groupName: String): RotatingGroupPreviewUiState {
            return RotatingGroupPreviewUiState(
                groupId = "",
                name = groupName,
                description = "Rotating savings circle.",
                avatarUrl = null,
                memberCount = 0,
                maxMembers = 0,
                groupTypeLabel = "Rotating Savings Circle",
                createdDateText = "",
                headerSubtitle = "Rotating Savings Circle",
                memberCountText = "Open for new members",
                cardMemberText = "Rotating Circle",
                contributionFormatted = "-- UGX",
                frequencyFormatted = "Per cycle",
                potPayoutFormatted = "-- UGX Lump Sum",
                potPayoutAmountOnly = "-- UGX",
                currentRoundFormatted = "Round 1",
                collectionTimeText = "Per cycle",
                cycleScheduleText = "Every cycle",
                sheetHeaderSubtitle = "Rotating Savings Circle",
                nextOpenSlot = 1,
                nextOpenSlotText = "Slot #1 is open for you",
                allowTurnSwaps = true,
                rosterSlots = listOf(
                    RotationRosterSlot(1, "OPEN FOR YOU", "Assigned on joining", isOpenSlot = true)
                ),
                mutualContacts = emptyList(),
                mutualContactsSnippet = "Verified circle members",
                requiredKycText = "Tier 2 (National ID Verified)",
                latePenaltyText = "Grace period applies",
                arrearsPolicyText = "Strike and suspend",
                guarantorRequirementText = "Mutual contact vouching",
                conditionCheck = RotatingGroupConditionCheck()
            )
        }
    }
}


