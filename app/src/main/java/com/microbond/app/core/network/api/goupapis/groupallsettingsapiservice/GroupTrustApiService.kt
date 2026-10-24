package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

import retrofit2.http.Path

// ── Trust & Member Settings Models ─────────────────────────────────────────

data class HealthMetricsDto(
    @SerializedName("on_time_repayment_rate") val onTimeRepaymentRate: String = "100%",
    @SerializedName("active_default_rate") val activeDefaultRate: String = "0%",
    @SerializedName("average_trust_score") val averageTrustScore: String = "100/100"
)

data class VouchingSummaryDto(
    @SerializedName("vouches_given_count") val vouchesGivenCount: Int = 0,
    @SerializedName("vouches_received_count") val vouchesReceivedCount: Int = 0,
    @SerializedName("vouch_action_subtitle") val vouchActionSubtitle: String = "Submit or review a member endorsement."
)

data class BenefitEligibilityDto(
    @SerializedName("is_eligible") val isEligible: Boolean = true,
    @SerializedName("status") val status: String = "APPROVED",
    @SerializedName("active_guarantors_count") val activeGuarantorsCount: Int = 0,
    @SerializedName("required_guarantors_count") val requiredGuarantorsCount: Int = 0,
    @SerializedName("rejection_reason") val rejectionReason: String? = null
)

data class TrustMembersData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("group_health_rating") val groupHealthRating: String = "Good",
    @SerializedName("group_health_grade") val groupHealthGrade: String = "A+",
    @SerializedName("group_health_status") val groupHealthStatus: String = "Excellent Standing",
    @SerializedName("health_metrics") val healthMetrics: HealthMetricsDto? = null,
    @SerializedName("vouching_summary") val vouchingSummary: VouchingSummaryDto? = null,
    @SerializedName("require_guarantors") val requireGuarantors: Boolean = false,
    @SerializedName("guarantor_count") val guarantorCount: Int = 2,
    @SerializedName("minimum_trust_score") val minimumTrustScore: Int = 70,
    @SerializedName("vis_guarantor_required") val visGuarantorRequired: Boolean = false,
    @SerializedName("visibility_label") val visibilityLabel: String = "Hidden from members",
    @SerializedName("my_benefit_eligibility") val myBenefitEligibility: BenefitEligibilityDto? = null,
    @SerializedName("is_admin") val isAdmin: Boolean = true
)

data class TrustMembersResponse(
    val status: String = "success",
    val message: String? = null,
    val data: TrustMembersData? = null
)

data class UpdateTrustMembersRequest(
    @SerializedName("require_guarantors") val requireGuarantors: Boolean? = null,
    @SerializedName("guarantor_count") val guarantorCount: Int? = null,
    @SerializedName("vis_guarantor_required") val visGuarantorRequired: Boolean? = null,
    @SerializedName("minimum_trust_score") val minimumTrustScore: Int? = null
)

// ── Vouching Models ────────────────────────────────────────────────────────

data class VouchRecordDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("target_member_id") val targetMemberId: String = "",
    @SerializedName("target_member_name") val targetMemberName: String = "",
    @SerializedName("voucher_member_id") val voucherMemberId: String = "",
    @SerializedName("voucher_member_name") val voucherMemberName: String = "",
    @SerializedName("note") val note: String? = null,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("status") val status: String = "ACTIVE"
)

data class MyVouchesData(
    @SerializedName("vouches_given") val vouchesGiven: List<VouchRecordDto> = emptyList(),
    @SerializedName("vouches_received") val vouchesReceived: List<VouchRecordDto> = emptyList()
)

data class MyVouchesResponse(
    val status: String = "success",
    val message: String? = null,
    val data: MyVouchesData? = null
)

data class SubmitVouchRequest(
    @SerializedName("target_member_id") val targetMemberId: String,
    @SerializedName("note") val note: String? = null
)

data class SubmitVouchResponse(
    val status: String = "success",
    val message: String? = null,
    val data: VouchRecordDto? = null
)

// ── Group Trust API Service Interface ──────────────────────────────────────

interface GroupTrustApiService {

    @GET("api/v1/groups/{group_id}/settings/trust-members")
    suspend fun getTrustMembersSettings(
        @Path("group_id") groupId: String
    ): Response<TrustMembersResponse>

    @PATCH("api/v1/groups/{group_id}/settings/trust-members")
    suspend fun updateTrustMembersSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateTrustMembersRequest
    ): Response<TrustMembersResponse>

    @GET("api/v1/groups/{group_id}/vouch/me")
    suspend fun getMyVouches(
        @Path("group_id") groupId: String
    ): Response<MyVouchesResponse>

    @POST("api/v1/groups/{group_id}/vouch")
    suspend fun submitVouch(
        @Path("group_id") groupId: String,
        @Body payload: SubmitVouchRequest
    ): Response<SubmitVouchResponse>
}

