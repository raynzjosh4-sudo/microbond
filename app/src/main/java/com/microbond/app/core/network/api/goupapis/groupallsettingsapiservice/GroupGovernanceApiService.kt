package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

import com.google.gson.annotations.SerializedName
import com.microbond.app.core.network.api.goupapis.GroupMemberItemDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ── Governance Models ───────────────────────────────────────────────────────

data class GovernanceSettingsResponse(
    val status: String = "success",
    val data: GovernanceSettingsData? = null
)

data class GovernanceSettingsData(
    @SerializedName("require_multi_signature") val requireMultiSignature: Boolean = true,
    @SerializedName("multi_sig_required_approvals") val multiSigRequiredApprovals: Int = 3,
    @SerializedName("signers_display") val signersDisplay: String = "3 of 5 Signers",
    @SerializedName("total_signers_count") val totalSignersCount: Int = 5,
    @SerializedName("quorum_percentage") val quorumPercentage: String = "60%",
    @SerializedName("quorum_display") val quorumDisplay: String = "3 of 5",
    @SerializedName("pending_disbursals_count") val pendingDisbursalsCount: Int = 2,
    @SerializedName("my_approval_authorization") val myApprovalAuthorization: String = "Authorized",
    @SerializedName("audit_status") val auditStatus: String = "Healthy",
    @SerializedName("audit_status_title") val auditStatusTitle: String = "No Active Audit",
    @SerializedName("audit_status_description") val auditStatusDescription: String = "The group's financial records are currently verified and clear.",
    @SerializedName("is_audit_active") val isAuditActive: Boolean = false
)

data class UpdateGovernanceRequest(
    @SerializedName("require_multi_signature") val requireMultiSignature: Boolean? = null,
    @SerializedName("multi_sig_required_approvals") val multiSigRequiredApprovals: Int? = null,
    @SerializedName("quorum_percentage") val quorumPercentage: String? = null,
    @SerializedName("designated_signer_ids") val designatedSignerIds: List<String>? = null
)

// ── Multisig Roster Models ──────────────────────────────────────────────────

data class MultisigRosterMemberDto(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("phone_number") val phoneNumber: String = "",
    @SerializedName("role") val role: String = "MEMBER",
    @SerializedName("is_signer") val isSigner: Boolean = false,
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null
)

data class MultisigRosterData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("require_multi_signature") val requireMultiSignature: Boolean = true,
    @SerializedName("required_approvals") val requiredApprovals: Int = 3,
    @SerializedName("total_signers") val totalSigners: Int = 5,
    @SerializedName("display") val display: String = "3 of 5 Signers",
    @SerializedName("signers") val signers: List<MultisigRosterMemberDto> = emptyList(),
    @SerializedName("eligible_members") val eligibleMembers: List<MultisigRosterMemberDto> = emptyList(),
    @SerializedName("all_members_count") val allMembersCount: Int = 0
)

data class MultisigRosterResponse(
    val status: String = "success",
    val message: String? = null,
    val data: MultisigRosterData? = null
)

data class UpdateMultisigRosterRequest(
    @SerializedName("required_approvals") val requiredApprovals: Int? = null,
    @SerializedName("signer_user_ids") val signerUserIds: List<String>? = null
)

// ── Quorum & Voting Requirements Models ────────────────────────────────────

data class QuorumSettingsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("quorum_percentage") val quorumPercentage: Int = 60,
    @SerializedName("quorum_percentage_display") val quorumPercentageDisplay: String = "60%",
    @SerializedName("minimum_participation") val minimumParticipation: String = "60%",
    @SerializedName("total_eligible_members") val totalEligibleMembers: Int = 1,
    @SerializedName("minimum_votes_required") val minimumVotesRequired: Int = 1,
    @SerializedName("approval_strategy") val approvalStrategy: String = "Majority Vote",
    @SerializedName("example_text") val exampleText: String = "",
    @SerializedName("enable_quorum_decay") val enableQuorumDecay: Boolean = true,
    @SerializedName("quorum_decay_hours") val quorumDecayHours: Int = 48,
    @SerializedName("quorum_decay_minimum_approvals") val quorumDecayMinimumApprovals: Int = 2,
    @SerializedName("allowed_options") val allowedOptions: List<Int> = listOf(50, 60, 66, 75, 80, 100),
    @SerializedName("auto_approve_small_requests") val autoApproveSmallRequests: Boolean = false,
    @SerializedName("auto_approve_max_amount") val autoApproveMaxAmount: Double = 0.0,
    @SerializedName("auto_approve_max_amount_formatted") val autoApproveMaxAmountFormatted: String = "0 UGX",
    @SerializedName("auto_approve_ceiling") val autoApproveCeiling: Long = 500000L,
    @SerializedName("auto_approve_ceiling_formatted") val autoApproveCeilingFormatted: String = "500,000 UGX"
)

data class QuorumSettingsResponse(
    val status: String = "success",
    val message: String? = null,
    val data: QuorumSettingsData? = null
)

data class UpdateQuorumSettingsRequest(
    @SerializedName("quorum_percentage") val quorumPercentage: Int? = null,
    @SerializedName("approval_strategy") val approvalStrategy: String? = null,
    @SerializedName("enable_quorum_decay") val enableQuorumDecay: Boolean? = null,
    @SerializedName("auto_approve_small_requests") val autoApproveSmallRequests: Boolean? = null,
    @SerializedName("auto_approve_max_amount") val autoApproveMaxAmount: Double? = null
)

// ── My Authorization & Auto-Approve Settings Models ───────────────────────

data class MyAuthorizationData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("role") val role: String = "MEMBER",
    @SerializedName("is_admin") val isAdmin: Boolean = false,
    @SerializedName("is_signer") val isSigner: Boolean = false,
    @SerializedName("is_authorized") val isAuthorized: Boolean = false,
    @SerializedName("authorization_status") val authorizationStatus: String = "Authorized",
    @SerializedName("auto_approve_small_requests") val autoApproveSmallRequests: Boolean = false,
    @SerializedName("auto_approve_max_amount") val autoApproveMaxAmount: Double = 50000.0,
    @SerializedName("auto_approve_max_amount_formatted") val autoApproveMaxAmountFormatted: String = "50,000 UGX",
    @SerializedName("max_allowed_limit") val maxAllowedLimit: Long = 500000L,
    @SerializedName("max_allowed_limit_formatted") val maxAllowedLimitFormatted: String = "500,000 UGX",
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("description") val description: String? = null
)

data class MyAuthorizationResponse(
    val status: String = "success",
    val message: String? = null,
    val data: MyAuthorizationData? = null
)

data class UpdateMyAuthorizationRequest(
    @SerializedName("auto_approve_small_requests") val autoApproveSmallRequests: Boolean? = null,
    @SerializedName("auto_approve_max_amount") val autoApproveMaxAmount: Double? = null
)

// ── Group Governance API Service Interface ─────────────────────────────────

interface GroupGovernanceApiService {

    @GET("api/v1/groups/{group_id}/settings/governance")
    suspend fun getGovernanceSettings(
        @Path("group_id") groupId: String
    ): Response<GovernanceSettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/governance")
    suspend fun updateGovernanceSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGovernanceRequest
    ): Response<GovernanceSettingsResponse>

    // ── Dedicated Multisig Roster Settings ────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/multisig-roster")
    suspend fun getMultisigRoster(
        @Path("group_id") groupId: String
    ): Response<MultisigRosterResponse>

    @PATCH("api/v1/groups/{group_id}/settings/multisig-roster")
    suspend fun updateMultisigRoster(
        @Path("group_id") groupId: String,
        @Body payload: UpdateMultisigRosterRequest
    ): Response<MultisigRosterResponse>

    @POST("api/v1/groups/{group_id}/settings/multisig-roster/signers/{user_id}")
    suspend fun addSigner(
        @Path("group_id") groupId: String,
        @Path("user_id") userId: String
    ): Response<MultisigRosterResponse>

    @POST("api/v1/groups/{group_id}/settings/multisig-roster/signers")
    suspend fun addSigners(
        @Path("group_id") groupId: String,
        @Body payload: UpdateMultisigRosterRequest
    ): Response<MultisigRosterResponse>

    @DELETE("api/v1/groups/{group_id}/settings/multisig-roster/signers/{user_id}")
    suspend fun removeSigner(
        @Path("group_id") groupId: String,
        @Path("user_id") userId: String
    ): Response<MultisigRosterResponse>

    // ── Dedicated Approval Quorum Settings ────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/quorum")
    suspend fun getQuorumSettings(
        @Path("group_id") groupId: String
    ): Response<QuorumSettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/quorum")
    suspend fun updateQuorumSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateQuorumSettingsRequest
    ): Response<QuorumSettingsResponse>

    // ── Dedicated My Authorization & Auto-Approve Settings ────────────────────
    @GET("api/v1/groups/{group_id}/settings/my-authorization")
    suspend fun getMyAuthorization(
        @Path("group_id") groupId: String
    ): Response<MyAuthorizationResponse>

    @PATCH("api/v1/groups/{group_id}/settings/my-authorization")
    suspend fun updateMyAuthorization(
        @Path("group_id") groupId: String,
        @Body payload: UpdateMyAuthorizationRequest
    ): Response<MyAuthorizationResponse>
}
