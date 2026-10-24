package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

// ── Financial Settings Models (General) ────────────────────────────────────

data class FinancialSettingsResponse(
    val status: String = "success",
    val data: FinancialSettingsData? = null
)

data class FinancialSettingsData(
    @SerializedName("enable_timelock") val enableTimelock: Boolean = true,
    @SerializedName("timelock_duration_hours") val timelockDurationHours: Int = 24
)

data class UpdateFinancialSettingsRequest(
    @SerializedName("enable_timelock") val enableTimelock: Boolean? = null,
    @SerializedName("timelock_duration_hours") val timelockDurationHours: Int? = null
)

// ── Pending Disbursals Queue & Circuit Breaker Models ──────────────────────

data class PendingPayoutItemDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("recipient_name") val recipientName: String = "",
    @SerializedName("recipient_phone") val recipientPhone: String = "",
    @SerializedName("recipient_avatar") val recipientAvatar: String? = null,
    @SerializedName("cycle_number") val cycleNumber: Int = 1,
    @SerializedName("amount_expected") val amountExpected: Long = 0L,
    @SerializedName("amount_expected_formatted") val amountExpectedFormatted: String = "",
    @SerializedName("status") val status: String = "PENDING", // PENDING, PROCESSING, ON_HOLD
    @SerializedName("hold_reason") val holdReason: String? = null,
    @SerializedName("payout_date") val payoutDate: Long = 0L,
    @SerializedName("payout_date_formatted") val payoutDateFormatted: String = ""
)

data class PendingDisbursalsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("pending_disbursals_count") val pendingDisbursalsCount: Int = 0,
    @SerializedName("max_pending_payouts") val maxPendingPayouts: Int = 2,
    @SerializedName("is_queue_full") val isQueueFull: Boolean = false,
    @SerializedName("circuit_breaker_active") val circuitBreakerActive: Boolean = false,
    @SerializedName("circuit_breaker_status") val circuitBreakerStatus: String = "NORMAL (Safe)",
    @SerializedName("vis_pending_payout_queue") val visPendingPayoutQueue: Boolean = true,
    @SerializedName("payouts") val payouts: List<PendingPayoutItemDto> = emptyList()
)

data class PendingDisbursalsResponse(
    val status: String = "success",
    val message: String? = null,
    val data: PendingDisbursalsData? = null
)

data class UpdatePendingDisbursalsRequest(
    @SerializedName("max_pending_payouts") val maxPendingPayouts: Int? = null,
    @SerializedName("vis_pending_payout_queue") val visPendingPayoutQueue: Boolean? = null
)

// ── Escrow Lock Settings Models ───────────────────────────────────────────

data class EscrowLockData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("enable_escrow") val enableEscrow: Boolean = true,
    @SerializedName("hold_funds_in_escrow") val holdFundsInEscrow: Boolean = true,
    @SerializedName("escrow_release_condition") val escrowReleaseCondition: String = "Upon unanimous approval",
    @SerializedName("release_condition") val releaseCondition: String = "Upon unanimous approval",
    @SerializedName("available_release_conditions") val availableReleaseConditions: List<String> = listOf(
        "Upon unanimous approval",
        "Majority Vote",
        "Timelock or Consensus",
        "Milestone and Consensus"
    )
)

data class EscrowLockResponse(
    val status: String = "success",
    val message: String? = null,
    val data: EscrowLockData? = null
)

data class UpdateEscrowLockRequest(
    @SerializedName("enable_escrow") val enableEscrow: Boolean? = null,
    @SerializedName("escrow_release_condition") val escrowReleaseCondition: String? = null
)

// ── Timelock Disbursals Settings Models ─────────────────────────────────────

data class TimelockDisbursalsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("enable_payout_delay") val enablePayoutDelay: Boolean = true,
    @SerializedName("enable_timelock") val enableTimelock: Boolean = true,
    @SerializedName("cooling_duration_hours") val coolingDurationHours: Int = 24,
    @SerializedName("timelock_duration_hours") val timelockDurationHours: Int = 24,
    @SerializedName("cooling_duration") val coolingDuration: String = "24 Hours",
    @SerializedName("cooling_duration_display") val coolingDurationDisplay: String = "24 Hours",
    @SerializedName("allowed_options") val allowedOptions: List<Int> = listOf(6, 12, 24, 48, 72),
    @SerializedName("description") val description: String? = null
)

data class TimelockDisbursalsResponse(
    val status: String = "success",
    val message: String? = null,
    val data: TimelockDisbursalsData? = null
)

data class UpdateTimelockDisbursalsRequest(
    @SerializedName("enable_payout_delay") val enablePayoutDelay: Boolean? = null,
    @SerializedName("cooling_duration_hours") val coolingDurationHours: Int? = null,
    @SerializedName("cooling_duration") val coolingDuration: String? = null
)

// ── Group Financial API Service Interface ──────────────────────────────────

interface GroupFinancialApiService {

    @GET("api/v1/groups/{group_id}/settings/financial")
    suspend fun getFinancialSettings(
        @Path("group_id") groupId: String
    ): Response<FinancialSettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/financial")
    suspend fun updateFinancialSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateFinancialSettingsRequest
    ): Response<FinancialSettingsResponse>

    // ── Dedicated Pending Disbursals Queue Settings ───────────────────────────
    @GET("api/v1/groups/{group_id}/settings/pending-disbursals")
    suspend fun getPendingDisbursals(
        @Path("group_id") groupId: String
    ): Response<PendingDisbursalsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/pending-disbursals")
    suspend fun updatePendingDisbursals(
        @Path("group_id") groupId: String,
        @Body payload: UpdatePendingDisbursalsRequest
    ): Response<PendingDisbursalsResponse>

    // ── Dedicated Escrow Lock Settings ────────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/escrow")
    suspend fun getEscrowLockSettings(
        @Path("group_id") groupId: String
    ): Response<EscrowLockResponse>

    @PATCH("api/v1/groups/{group_id}/settings/escrow")
    suspend fun updateEscrowLockSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateEscrowLockRequest
    ): Response<EscrowLockResponse>

    // ── Dedicated Timelock Disbursals Settings ─────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/timelock")
    suspend fun getTimelockDisbursals(
        @Path("group_id") groupId: String
    ): Response<TimelockDisbursalsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/timelock")
    suspend fun updateTimelockDisbursals(
        @Path("group_id") groupId: String,
        @Body payload: UpdateTimelockDisbursalsRequest
    ): Response<TimelockDisbursalsResponse>
}
