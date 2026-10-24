package com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice

import com.google.gson.annotations.SerializedName
import com.microbond.app.core.components.groupintrocard.rotating.RotatingGroupApiData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// ── Rotating Group (ROSCA) Models ───────────────────────────────────────────

data class RotatingGroupSettingsSummary(
    @SerializedName("contribution_amount") val contributionAmount: Double = 0.0,
    @SerializedName("payouts_per_cycle") val payoutsPerCycle: Int = 1,
    @SerializedName("number_of_cycles") val numberOfCycles: Int = 10,
    @SerializedName("current_cycle") val currentCycle: Int = 1,
    @SerializedName("collection_frequency_days") val collectionFrequencyDays: Int = 7,
    @SerializedName("collection_time") val collectionTime: String? = null,
    @SerializedName("group_start_time") val groupStartTime: Long? = null,
    @SerializedName("payout_order_strategy") val payoutOrderStrategy: String? = "FIRST_COME_FIRST_SERVE",
    @SerializedName("is_payout_order_locked") val isPayoutOrderLocked: Boolean = false,
    @SerializedName("total_expected_pool_per_cycle") val totalExpectedPoolPerCycle: Double = 0.0,
    @SerializedName("minimum_kyc_tier") val minimumKycTier: Int = 1,
    @SerializedName("require_guarantor") val requireGuarantor: Boolean = false,
    @SerializedName("guarantor_default_action") val guarantorDefaultAction: String? = null,
    @SerializedName("allow_partial_payments") val allowPartialPayments: Boolean = false,
    @SerializedName("allow_advance_deposits") val allowAdvanceDeposits: Boolean = true,
    @SerializedName("enforce_payout_holds") val enforcePayoutHolds: Boolean = true
)

data class GroupSettingsSummaryResponse(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("group_type") val groupType: String = "ROTATING",
    @SerializedName("type_specific_settings") val typeSpecificSettings: RotatingGroupSettingsSummary? = null
)

data class RotationSlotMember(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("slot_number") val slotNumber: Int = 1,
    @SerializedName("profile_name", alternate = ["name", "display_name"]) val profileName: String = "",
    @SerializedName("profile_picture_url", alternate = ["avatar_url", "image_url"]) val profilePictureUrl: String? = null,
    @SerializedName("avatar_color") val avatarColor: String? = null,
    @SerializedName("status") val status: String = "SCHEDULED", // "PAID_OUT", "CURRENT", "SCHEDULED", "OPEN"
    @SerializedName("payout_date") val payoutDate: String? = null,
    @SerializedName("is_current_user") val isCurrentUser: Boolean = false
)

data class RotatingDepositInfoResponse(
    @SerializedName("required_contribution") val requiredContribution: Double = 0.0,
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("active_cycle") val activeCycle: Int = 1,
    @SerializedName("cycle_deadline") val cycleDeadline: String? = null,
    @SerializedName("grace_period_hours") val gracePeriodHours: Int = 24,
    @SerializedName("late_fee_amount") val lateFeeAmount: Double = 0.0,
    @SerializedName("allow_partial_payments") val allowPartialPayments: Boolean = false
)

data class TurnSwapRequest(
    @SerializedName("target_user_id") val targetUserId: String
)

data class TurnSwapResponse(
    val status: String = "success",
    val message: String = ""
)

// ── Rotating API Service Interface ──────────────────────────────────────────

interface RotatingApiService {

    @GET("api/v1/groups/{group_id}/settings/summary")
    suspend fun getGroupSettingsSummary(
        @Path("group_id") groupId: String
    ): Response<RotatingGroupApiData>

    @GET("api/v1/groups/{group_id}/deposit-info")
    suspend fun getRotatingDepositInfo(
        @Path("group_id") groupId: String
    ): Response<RotatingDepositInfoResponse>

    @GET("api/v1/groups/{group_id}/members")
    suspend fun getRotatingGroupMembers(
        @Path("group_id") groupId: String
    ): Response<List<RotationSlotMember>>

    @POST("api/v1/groups/{group_id}/swap-turn")
    suspend fun requestTurnSwap(
        @Path("group_id") groupId: String,
        @Body payload: TurnSwapRequest
    ): Response<TurnSwapResponse>
}
