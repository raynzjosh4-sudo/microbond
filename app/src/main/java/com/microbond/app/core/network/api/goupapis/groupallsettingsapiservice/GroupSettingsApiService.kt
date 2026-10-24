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

// ── Group Privacy Models ────────────────────────────────────────────────────

data class GroupPrivacyData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("hide_total_group_balance") val hideTotalGroupBalance: Boolean = false,
    @SerializedName("hide_member_contact_info") val hideMemberContactInfo: Boolean = true,
    @SerializedName("invite_only_discoverability") val inviteOnlyDiscoverability: Boolean = false,
    @SerializedName("vis_member_total_savings") val visMemberTotalSavings: Boolean = true
)

data class GroupPrivacyResponse(
    val status: String = "success",
    val message: String? = null,
    val data: GroupPrivacyData? = null
)

data class UpdateGroupPrivacyRequest(
    @SerializedName("hide_total_group_balance") val hideTotalGroupBalance: Boolean? = null,
    @SerializedName("hide_member_contact_info") val hideMemberContactInfo: Boolean? = null,
    @SerializedName("invite_only_discoverability") val inviteOnlyDiscoverability: Boolean? = null
)

// ── Goal Controls Models ───────────────────────────────────────────────────

data class GoalControlsResponse(
    val status: String = "success",
    val data: GoalControlsData? = null
)

data class GoalControlsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String? = null,
    @SerializedName("lock_funds", alternate = ["is_locked"]) val lockFunds: Boolean = false,
    @SerializedName("hide_leaderboard") val hideLeaderboard: Boolean = false,
    @SerializedName("let_members_view_total_saved", alternate = ["vis_member_total_savings"]) val letMembersViewTotalSaved: Boolean = true
)

data class UpdateGoalControlsRequest(
    @SerializedName("lock_funds") val lockFunds: Boolean? = null,
    @SerializedName("hide_leaderboard") val hideLeaderboard: Boolean? = null,
    @SerializedName("let_members_view_total_saved") val letMembersViewTotalSaved: Boolean? = null
)

// ── Governance & Audit Models ───────────────────────────────────────────────

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

data class AuditActionResponse(
    val status: String = "success",
    val message: String? = null,
    val data: GovernanceSettingsData? = null
)

// ── Financial Settings Models (Timelock Disbursals) ────────────────────────

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

// ── Audit Report Delivery & Compliance Models ──────────────────────────────

data class AuditDeliverySettingsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("enable_automated_audit_dispatch") val enableAutomatedAuditDispatch: Boolean = false,
    @SerializedName("audit_report_recipient_email") val auditReportRecipientEmail: String? = null,
    @SerializedName("dispatch_monthly_compliance_statement") val dispatchMonthlyComplianceStatement: Boolean = false,
    @SerializedName("export_file_format") val exportFileFormat: String = "PDF",
    @SerializedName("default_admin_email") val defaultAdminEmail: String? = null,
    @SerializedName("available_formats") val availableFormats: List<String> = listOf("PDF", "CSV")
)

data class AuditDeliverySettingsResponse(
    val status: String = "success",
    val message: String? = null,
    val data: AuditDeliverySettingsData? = null
)

data class UpdateAuditDeliverySettingsRequest(
    @SerializedName("enable_automated_audit_dispatch") val enableAutomatedAuditDispatch: Boolean? = null,
    @SerializedName("audit_report_recipient_email") val auditReportRecipientEmail: String? = null,
    @SerializedName("dispatch_monthly_compliance_statement") val dispatchMonthlyComplianceStatement: Boolean? = null,
    @SerializedName("export_file_format") val exportFileFormat: String? = null
)

data class ExportFinancialReportRequest(
    @SerializedName("export_format") val exportFormat: String = "PDF",
    @SerializedName("recipient_email") val recipientEmail: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null
)

data class AuditDeliveryActionResponse(
    val status: String = "success",
    val message: String? = null
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

// ── Group Settings API Service Interface ───────────────────────────────────

interface GroupSettingsApiService {

    // ── Privacy Settings ──────────────────────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/privacy")
    suspend fun getGroupPrivacy(
        @Path("group_id") groupId: String
    ): Response<GroupPrivacyResponse>

    @PATCH("api/v1/groups/{group_id}/settings/privacy")
    suspend fun updateGroupPrivacy(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGroupPrivacyRequest
    ): Response<GroupPrivacyResponse>

    // ── Goal Controls Settings ────────────────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/goal-controls")
    suspend fun getGoalControls(
        @Path("group_id") groupId: String
    ): Response<GoalControlsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/goal-controls")
    suspend fun updateGoalControls(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGoalControlsRequest
    ): Response<GoalControlsResponse>

    // ── Governance & Audit ────────────────────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/governance")
    suspend fun getGovernanceSettings(
        @Path("group_id") groupId: String
    ): Response<GovernanceSettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/governance")
    suspend fun updateGovernanceSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGovernanceRequest
    ): Response<GovernanceSettingsResponse>

    @POST("api/v1/groups/{group_id}/settings/governance/audit/start")
    suspend fun startAudit(
        @Path("group_id") groupId: String
    ): Response<AuditActionResponse>

    @POST("api/v1/groups/{group_id}/settings/governance/audit/complete")
    suspend fun completeAudit(
        @Path("group_id") groupId: String
    ): Response<AuditActionResponse>

    // Members filtered by signer flag (multisig roster)
    @GET("api/v1/groups/{group_id}/members")
    suspend fun getGroupSigners(
        @Path("group_id") groupId: String,
        @Query("is_signer") isSigner: Boolean = true
    ): Response<List<GroupMemberItemDto>>

    // ── Financial Settings (Timelock Disbursals) ──────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/financial")
    suspend fun getFinancialSettings(
        @Path("group_id") groupId: String
    ): Response<FinancialSettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/financial")
    suspend fun updateFinancialSettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateFinancialSettingsRequest
    ): Response<FinancialSettingsResponse>

    // ── Dedicated Multisig Roster Management ──────────────────────────────────
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

    @DELETE("api/v1/groups/{group_id}/settings/multisig-roster/signers/{user_id}")
    suspend fun removeSigner(
        @Path("group_id") groupId: String,
        @Path("user_id") userId: String
    ): Response<MultisigRosterResponse>

    // ── Audit Report Delivery & Compliance ────────────────────────────────────
    @GET("api/v1/groups/{group_id}/settings/audit-report-delivery")
    suspend fun getAuditReportDeliverySettings(
        @Path("group_id") groupId: String
    ): Response<AuditDeliverySettingsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/audit-report-delivery")
    suspend fun updateAuditReportDeliverySettings(
        @Path("group_id") groupId: String,
        @Body payload: UpdateAuditDeliverySettingsRequest
    ): Response<AuditDeliverySettingsResponse>

    @POST("api/v1/groups/{group_id}/financial-reports/export-and-email")
    suspend fun exportAndEmailFinancialReport(
        @Path("group_id") groupId: String,
        @Body payload: ExportFinancialReportRequest
    ): Response<AuditDeliveryActionResponse>

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
