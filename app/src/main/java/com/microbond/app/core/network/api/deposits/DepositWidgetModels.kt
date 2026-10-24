package com.microbond.app.core.network.api.deposits

import com.google.gson.annotations.SerializedName

/**
 * Filter types for the deposit widget member drill-down list.
 */
enum class DepositStatusFilter(val apiKey: String, val displayName: String) {
    ALL("ALL", "All"),
    FULL("FULL", "Full Deposit"),
    PARTIAL("PARTIAL", "Half Deposit"),
    NOT_DEPOSITED("NOT_DEPOSITED", "Not Deposited");

    companion object {
        fun fromKey(key: String): DepositStatusFilter =
            entries.firstOrNull { 
                it.apiKey.equals(key, ignoreCase = true) || 
                (it == PARTIAL && key.equals("HALF", ignoreCase = true)) 
            } ?: ALL
    }
}

/**
 * Visibility permissions for who can view the daily deposit widget in chat.
 */
enum class DepositWidgetVisibility(val apiKey: String) {
    ALL_MEMBERS("ALL_MEMBERS"),
    ADMIN_ONLY("ADMIN_ONLY"),
    ADMIN_AND_TREASURER("ADMIN_AND_TREASURER")
}

// ── Widget Bubble Summary Models ──────────────────────────────────────────────

data class CategorySummary(
    val count: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("percentage_of_members") val percentageOfMembers: Double = 0.0
)

data class DepositSummaryGroup(
    val full: CategorySummary = CategorySummary(),
    val partial: CategorySummary = CategorySummary(),
    @SerializedName("not_deposited") val notDeposited: CategorySummary = CategorySummary()
)

data class OverallDepositSummary(
    @SerializedName("total_expected") val totalExpected: Double = 0.0,
    @SerializedName("total_collected") val totalCollected: Double = 0.0,
    @SerializedName("collection_rate") val collectionRate: Double = 0.0
)

data class DailyDepositWidgetData(
    @SerializedName("group_id") val groupId: String = "",
    val date: String = "",
    @SerializedName("target_amount_per_member") val targetAmountPerMember: Long = 0L,
    @SerializedName("total_members") val totalMembers: Int = 0,
    val summary: DepositSummaryGroup = DepositSummaryGroup(),
    val overall: OverallDepositSummary = OverallDepositSummary()
)

data class DailyDepositWidgetResponse(
    val status: String = "success",
    val data: DailyDepositWidgetData = DailyDepositWidgetData()
)

// ── Member Drill-Down Models ──────────────────────────────────────────────────

data class DepositMemberItem(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("membership_id") val membershipId: String = "",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null,
    @SerializedName("phone_number") val phoneNumber: String = "",
    val role: String = "MEMBER",
    @SerializedName("is_online") val isOnline: Boolean = false,
    @SerializedName("last_seen_at") val lastSeenAt: Long? = null,
    @SerializedName("amount_required") val amountRequired: Long = 0L,
    @SerializedName("amount_paid") val amountPaid: Long = 0L,
    @SerializedName("amount_remaining") val amountRemaining: Long = 0L,
    @SerializedName("percentage_paid") val percentagePaid: Double = 0.0,
    val status: String = "NOT_DEPOSITED",
    @SerializedName("settled_at") val settledAt: Long? = null,
    @SerializedName("can_nudge") val canNudge: Boolean = false
)

data class DepositMembersData(
    @SerializedName("group_id") val groupId: String = "",
    val date: String = "",
    @SerializedName("filter_status") val filterStatus: String = "",
    val count: Int = 0,
    val members: List<DepositMemberItem> = emptyList()
)

data class DepositMembersResponse(
    val status: String = "success",
    val data: DepositMembersData = DepositMembersData()
)

// ── Nudge & Visibility Requests / Responses ───────────────────────────────────

data class NudgeMemberRequest(
    @SerializedName("target_user_id") val targetUserId: String,
    @SerializedName("custom_message") val customMessage: String? = null
)

data class NudgeMemberResponse(
    val status: String = "success",
    val message: String = "",
    @SerializedName("target_user_id") val targetUserId: String = "",
    @SerializedName("sent_message") val sentMessage: String = ""
)

data class UpdateDepositWidgetVisibilityRequest(
    val visibility: String
)
