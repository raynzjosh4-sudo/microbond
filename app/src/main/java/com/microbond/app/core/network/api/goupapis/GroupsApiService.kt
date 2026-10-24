package com.microbond.app.core.network.api.goupapis

import com.google.gson.annotations.SerializedName
import com.microbond.app.core.network.api.userapiservice.CreateLinkedAccountRequest
import com.microbond.app.core.network.api.userapiservice.LinkedAccountResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import okhttp3.ResponseBody

// ── Financial Obligations & Dues Models ─────────────────────────────────────

data class RemoteObligation(
    val id: String,
    val status: String,
    val amountPaid: Long,
    val updatedAt: Long
)

// ── Group Membership Models ─────────────────────────────────────────────────

data class GroupJoinRequest(
    @SerializedName("group_id") val groupId: String,
    @SerializedName("guarantor_ids") val guarantorIds: List<String>? = null,
    @SerializedName("invite_code") val inviteCode: String? = null
)

data class GroupJoinResponse(
    val status: String = "success",
    val message: String = "Joined group successfully",
    @SerializedName("membership_status", alternate = ["admission_status"]) val membershipStatus: String? = null,
    @SerializedName("requires_admin_approval") val requiresAdminApproval: Boolean? = null,
    @SerializedName("requires_guarantor") val requiresGuarantor: Boolean? = null
)

// ── Dashboard Summaries, Last Message & Action Message Models ───────────────

data class LastMessageSummaryDto(
    @SerializedName("message_id") val messageId: String? = null,
    @SerializedName("sender_id") val senderId: String? = null,
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("raw_text") val rawText: String? = null,
    @SerializedName("message_type") val messageType: String = "TEXT",
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("timestamp") val timestamp: Long = 0L,
    @SerializedName("is_outgoing") val isOutgoing: Boolean = false,
    @SerializedName("status") val status: String = "SENT"
)

data class RingStatus(
    @SerializedName("total_expected_members") val totalExpectedMembers: Int = 0,
    @SerializedName("paid_on_time_count") val paidOnTimeCount: Int = 0,
    @SerializedName("missed_deadline_count") val missedDeadlineCount: Int = 0,
    @SerializedName("percentage") val percentage: Float = 0f,
    @SerializedName("color") val color: String? = null
)

data class ActionMessage(
    @SerializedName("text") val text: String = "",
    @SerializedName("type") val type: String = "GENERAL",
    @SerializedName("has_blue_ticks") val hasBlueTicks: Boolean = true,
    @SerializedName("is_alert") val isAlert: Boolean = false,
    @SerializedName("icon") val icon: String? = null
)

data class GroupListSummaryResponse(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String = "",
    @SerializedName("group_type") val groupType: String = "ROTATING",
    @SerializedName("avatar_url", alternate = ["group_image_url", "image_url", "icon_url", "group_avatar_url"]) val avatarUrl: String? = null,
    @SerializedName("ring_status") val ringStatus: RingStatus? = null,
    @SerializedName("action_messages") val actionMessages: List<ActionMessage> = emptyList(),
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("last_activity_timestamp") val lastActivityTimestamp: Long = 0L,
    @SerializedName("last_message") val lastMessage: LastMessageSummaryDto? = null
)

data class TotalUnreadCountResponse(
    @SerializedName("total_unread_count") val totalUnreadCount: Int = 0
)

// ── Group Creation & Permission Gateways Models ─────────────────────────────

data class GroupTypeStatus(
    val type: String,
    @SerializedName("is_allowed") val isAllowed: Boolean = false,
    val reason: String = ""
)

data class AllowedGroupsResponse(
    val status: String = "success",
    @SerializedName("allowed_groups") val allowedGroups: List<String> = emptyList(),
    val reasons: Map<String, String> = emptyMap(),
    val groups: List<GroupTypeStatus> = emptyList()
)

data class UnifiedGroupCreateRequest(
    val name: String,
    val description: String? = null,
    @SerializedName("group_image_url") val groupImageUrl: String? = null,
    val type: String = "GOAL_SAVINGS",
    @SerializedName("payment_mode") val paymentMode: String = "OBLIGATION",
    val visibility: String = "PRIVATE",
    val currency: String = "UGX",
    @SerializedName("max_members") val maxMembers: Int? = null,
    @SerializedName("enable_late_fees") val enableLateFees: Boolean = false,
    @SerializedName("requires_admin_approval") val requiresAdminApproval: Boolean = true
)

data class GroupCreatedData(
    @SerializedName("group_id") val groupId: String,
    @SerializedName("invite_code") val inviteCode: String,
    val name: String
)

data class GroupCreateResponse(
    val status: String = "success",
    val message: String = "",
    val data: GroupCreatedData? = null
)

// ── Group Membership Management & Settings Models ───────────────────────────

data class AddMemberRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("role") val role: String = "MEMBER"
)

data class AddMemberResponse(
    val status: String = "success",
    val message: String = "Member added successfully"
)

data class UpdateGroupProfileRequest(
    val description: String? = null,
    val name: String? = null,
    @SerializedName("group_image_url") val groupImageUrl: String? = null
)

data class UpdateGroupProfileResponse(
    val status: String = "success",
    val message: String = "Group profile updated",
    val data: Any? = null
)

data class GroupPermissionsData(
    @SerializedName("members_can_edit_settings") val membersCanEditSettings: Boolean = false,
    @SerializedName("members_can_send_messages") val membersCanSendMessages: Boolean = true,
    @SerializedName("members_can_invite_via_link") val membersCanInviteViaLink: Boolean = false,
    @SerializedName("members_can_manage_payout_roster") val membersCanManagePayoutRoster: Boolean = false,
    @SerializedName("admins_must_approve_new_members") val adminsMustApproveNewMembers: Boolean = false,
    @SerializedName("allow_turn_swaps") val allowTurnSwaps: Boolean = false
)

data class GroupPermissionsResponse(
    val status: String = "success",
    val data: GroupPermissionsData? = null
)

data class UpdateGroupPermissionsRequest(
    @SerializedName("members_can_edit_settings") val membersCanEditSettings: Boolean? = null,
    @SerializedName("members_can_send_messages") val membersCanSendMessages: Boolean? = null,
    @SerializedName("members_can_invite_via_link") val membersCanInviteViaLink: Boolean? = null,
    @SerializedName("members_can_manage_payout_roster") val membersCanManagePayoutRoster: Boolean? = null,
    @SerializedName("admins_must_approve_new_members") val adminsMustApproveNewMembers: Boolean? = null
)

typealias GroupPrivacyData = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GroupPrivacyData
typealias GroupPrivacyResponse = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GroupPrivacyResponse
typealias UpdateGroupPrivacyRequest = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGroupPrivacyRequest

// ── Your Settings — Membership Models ─────────────────────────────────────

data class MembershipMeResponse(
    val status: String = "success",
    val data: MembershipMeData? = null
)

data class MembershipMeData(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("role") val role: String = "MEMBER",
    @SerializedName("total_saved") val totalSaved: Long = 0L,
    @SerializedName("total_saved_formatted") val totalSavedFormatted: String = "0",
    @SerializedName("savings_hidden") val savingsHidden: Boolean = false,
    @SerializedName("mute_notifications") val muteNotifications: Boolean = false,
    @SerializedName("custom_notification_tone") val customNotificationTone: String? = null,
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("auto_approve_small_requests") val autoApproveSmallRequests: Boolean = false,
    @SerializedName("auto_approve_limit") val autoApproveLimit: String? = "50,000 UGX"
)

data class UpdateMembershipMeRequest(
    @SerializedName("mute_notifications") val muteNotifications: Boolean? = null,
    @SerializedName("custom_notification_tone") val customNotificationTone: String? = null,
    @SerializedName("auto_approve_small_requests") val autoApproveSmallRequests: Boolean? = null,
    @SerializedName("auto_approve_limit") val autoApproveLimit: String? = null
)

// ── Your Settings — Savings Growth Models ──────────────────────────────────

data class SavingsGrowthResponse(
    val status: String = "success",
    val meta: SavingsGrowthMeta? = null,
    val points: List<SavingsGrowthPoint> = emptyList()
)

data class SavingsGrowthMeta(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String? = null,
    @SerializedName("currency") val currency: String = "UGX",
    @SerializedName("total_saved") val totalSaved: Double = 0.0,
    @SerializedName("total_saved_formatted") val totalSavedFormatted: String? = null,
    @SerializedName("interest_gained") val interestGained: Double = 0.0,
    @SerializedName("period") val period: String = "monthly",
    @SerializedName("bucket_count") val bucketCount: Int = 0
)

data class SavingsGrowthPoint(
    val label: String = "",
    @SerializedName("amount_saved_in_period") val amountSavedInPeriod: Double = 0.0,
    @SerializedName("cumulative_saved") val cumulativeSaved: Double = 0.0
)

// ── Group Settings Models (Delegated to groupallsettingsapiservice) ────────

typealias GoalControlsResponse = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GoalControlsResponse
typealias GoalControlsData = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GoalControlsData
typealias UpdateGoalControlsRequest = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGoalControlsRequest

typealias GovernanceSettingsResponse = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GovernanceSettingsResponse
typealias GovernanceSettingsData = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GovernanceSettingsData
typealias UpdateGovernanceRequest = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateGovernanceRequest
typealias AuditActionResponse = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.AuditActionResponse

typealias FinancialSettingsResponse = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.FinancialSettingsResponse
typealias FinancialSettingsData = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.FinancialSettingsData
typealias UpdateFinancialSettingsRequest = com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.UpdateFinancialSettingsRequest

// ── Your Settings — Payment Method Models ──────────────────────────────────

data class GroupPaymentMethodResponse(
    @SerializedName("payment_method_id") val paymentMethodId: String? = null,
    @SerializedName("provider_name") val providerName: String = "",
    @SerializedName("account_number") val accountNumber: String = "",
    @SerializedName("account_name") val accountName: String? = null,
    @SerializedName("verified_name") val verifiedName: String? = null,
    @SerializedName("is_verified") val isVerified: Boolean? = null,
    @SerializedName("verification_type") val verificationType: String? = null,
    @SerializedName("masked_number") val maskedNumber: String = "",
    @SerializedName("is_fallback") val isFallback: Boolean = false
)

data class UpdateGroupPaymentMethodRequest(
    @SerializedName("payment_method_id") val paymentMethodId: String?
)

data class UserPaymentMethodDto(
    @SerializedName("id", alternate = ["payment_method_id"]) val paymentMethodId: String = "",
    @SerializedName("provider_name", alternate = ["provider"]) val provider: String = "",
    @SerializedName("account_number") val accountNumber: String = "",
    @SerializedName("account_label", alternate = ["account_name", "verified_name"]) val accountLabel: String? = null,
    @SerializedName("is_default") val isDefault: Boolean = false,
    @SerializedName("masked_number") val maskedNumber: String? = null
)

// ── Your Settings — Report Group Models ────────────────────────────────────

data class ReportGroupRequest(
    @SerializedName("reason") val reason: String,
    @SerializedName("description") val description: String? = null
)

data class ReportGroupResponse(
    val status: String = "success",
    val message: String = "Report submitted"
)

// ── Your Settings — Leave Group Models ─────────────────────────────────────

data class LeaveGroupResponse(
    val status: String = "success",
    val message: String = "",
    @SerializedName("can_leave") val canLeave: Boolean = true,
    @SerializedName("block_reason") val blockReason: String? = null
)

data class GroupMemberItemDto(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("phone_number") val phoneNumber: String = "",
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("role") val role: String = "MEMBER",
    @SerializedName("is_signer") val isSigner: Boolean = false,
    @SerializedName("payout_position") val payoutPosition: Int? = null,
    @SerializedName("status") val status: String = "ACTIVE",
    @SerializedName("total_contributed") val totalContributed: Long = 0L,
    @SerializedName("trust_score") val trustScore: Int = 100,
    @SerializedName("joined_at") val joinedAt: Long = 0L,
    @SerializedName("outstanding_balance") val outstandingBalance: Double = 0.0
)

data class GroupImageUploadData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_image_url") val groupImageUrl: String = "",
    @SerializedName("updated_at") val updatedAt: Long = 0L
)

data class GroupImageUploadResponse(
    val status: String = "success",
    val message: String = "",
    val data: GroupImageUploadData? = null
)

data class GroupMediaSummaryCounts(
    val media: Int = 0,
    val docs: Int = 0,
    val links: Int = 0,
    val audio: Int = 0,
    val total: Int = 0
)

data class RecentMediaThumbnailDto(
    @SerializedName("item_id") val itemId: String = "",
    @SerializedName("media_url") val mediaUrl: String = "",
    @SerializedName("media_type") val mediaType: String = "IMAGE",
    @SerializedName("mime_type") val mimeType: String? = null
)

data class GroupMediaSummaryResponse(
    val status: String = "success",
    @SerializedName("group_id") val groupId: String = "",
    val counts: GroupMediaSummaryCounts = GroupMediaSummaryCounts(),
    @SerializedName("recent_media") val recentMedia: List<RecentMediaThumbnailDto> = emptyList()
)

data class GroupMediaItemDto(
    @SerializedName("item_id") val itemId: String = "",
    @SerializedName("message_id") val messageId: String = "",
    val category: String = "MEDIA",
    @SerializedName("media_url") val mediaUrl: String? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    val filename: String? = null,
    @SerializedName("size_bytes") val sizeBytes: Long = 0L,
    @SerializedName("text_content") val textContent: String? = null,
    @SerializedName("link_url") val linkUrl: String? = null,
    @SerializedName("link_domain") val linkDomain: String? = null,
    @SerializedName("sender_id") val senderId: String = "",
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("is_outgoing") val isOutgoing: Boolean = false,
    @SerializedName("created_at") val createdAt: Long = 0L,
    val width: Int? = null,
    val height: Int? = null,
    @SerializedName("aspect_ratio") val aspectRatio: Float? = null,
    @SerializedName("duration_seconds") val durationSeconds: Int? = null
)

data class GroupMediaListResponse(
    val status: String = "success",
    @SerializedName("group_id") val groupId: String = "",
    val category: String = "MEDIA",
    val counts: GroupMediaSummaryCounts? = null,
    val data: List<GroupMediaItemDto> = emptyList(),
    @SerializedName("has_more") val hasMore: Boolean = false,
    @SerializedName("next_cursor") val nextCursor: Long? = null
)

// ── Groups API Service Interface ────────────────────────────────────────────

interface GroupsApiService {

    // Join a group
    @POST("api/v1/groups/join")
    suspend fun joinGroup(
        @Body payload: GroupJoinRequest
    ): Response<GroupJoinResponse>

    // Add member directly (strictly ADMIN only)
    @POST("api/v1/groups/{group_id}/members/add")
    suspend fun addMember(
        @retrofit2.http.Path("group_id") groupId: String,
        @Body payload: AddMemberRequest
    ): Response<AddMemberResponse>

    // Update group profile (e.g. description, name)
    @PATCH("api/v1/groups/{group_id}/settings/profile")
    suspend fun updateGroupProfile(
        @retrofit2.http.Path("group_id") groupId: String,
        @Body payload: UpdateGroupProfileRequest
    ): Response<UpdateGroupProfileResponse>

    // Upload group icon via Cloudflare R2
    @Multipart
    @POST("api/v1/groups/{group_id}/settings/image")
    suspend fun uploadGroupIcon(
        @retrofit2.http.Path("group_id") groupId: String,
        @Part file: MultipartBody.Part
    ): Response<GroupImageUploadResponse>

    // Remove group icon
    @DELETE("api/v1/groups/{group_id}/settings/image")
    suspend fun deleteGroupIcon(
        @retrofit2.http.Path("group_id") groupId: String
    ): Response<Map<String, Any>>

    // Get Group Media Summary (counts + preview thumbnails for Group Settings)
    @GET("api/v1/groups/{group_id}/media/summary")
    suspend fun getGroupMediaSummary(
        @retrofit2.http.Path("group_id") groupId: String
    ): Response<GroupMediaSummaryResponse>

    // Get Categorized Media Gallery (MEDIA, DOCS, LINKS, AUDIO)
    @GET("api/v1/groups/{group_id}/media")
    suspend fun getGroupMedia(
        @retrofit2.http.Path("group_id") groupId: String,
        @retrofit2.http.Query("category") category: String = "MEDIA",
        @retrofit2.http.Query("limit") limit: Int = 50,
        @retrofit2.http.Query("before_created_at") beforeCreatedAt: Long? = null,
        @retrofit2.http.Query("include_counts") includeCounts: Boolean = true
    ): Response<GroupMediaListResponse>

    // List & filter group members
    @GET("api/v1/groups/{group_id}/members")
    suspend fun getGroupMembers(
        @retrofit2.http.Path("group_id") groupId: String
    ): Response<List<GroupMemberItemDto>>

    // Get User Group Summaries & Animated Tickers for Dashboard
    @GET("api/v1/groups/dashboard/summaries")
    suspend fun getGroupDashboardSummaries(): Response<List<GroupListSummaryResponse>>

    // Get Total Unread Badge Count Across All User Groups
    @GET("api/v1/groups/dashboard/unread-count")
    suspend fun getTotalUnreadCount(): Response<TotalUnreadCountResponse>

    // Evaluate allowed group creation archetypes based on KYC Tier, Trust Score, and Health
    @GET("api/v1/users/me/allowed-groups")
    suspend fun getAllowedGroups(): Response<AllowedGroupsResponse>

    // Atomic group creation wizard (provisions group, wallet, governance, financial rules, and chat)
    @POST("api/v1/groups/create")
    suspend fun createGroup(
        @Body payload: UnifiedGroupCreateRequest
    ): Response<GroupCreateResponse>

    // Payment / Group Association Dues
    @POST("api/v1/group/association-dues/pay")
    suspend fun payObligation(
        @retrofit2.http.Path("obligationId") obligationId: String,
        @Body payloadJson: String
    ): Response<RemoteObligation>

    // Get group permissions settings
    @GET("api/v1/groups/{group_id}/settings/permissions")
    suspend fun getGroupPermissions(
        @retrofit2.http.Path("group_id") groupId: String
    ): Response<GroupPermissionsResponse>

    // Update group permissions settings (admin only)
    @PATCH("api/v1/groups/{group_id}/settings/permissions")
    suspend fun updateGroupPermissions(
        @retrofit2.http.Path("group_id") groupId: String,
        @Body payload: UpdateGroupPermissionsRequest
    ): Response<GroupPermissionsResponse>

    // ── Your Settings ─────────────────────────────────────────────────────────

    // Get my membership data (total saved, mute flags, etc.)
    @GET("api/v1/groups/{group_id}/membership/me")
    suspend fun getMyMembership(
        @Path("group_id") groupId: String
    ): Response<MembershipMeResponse>

    // Update my membership settings (mute, notification tone)
    @PATCH("api/v1/groups/{group_id}/membership/me")
    suspend fun updateMyMembership(
        @Path("group_id") groupId: String,
        @Body payload: UpdateMembershipMeRequest
    ): Response<MembershipMeResponse>

    // Get my group-specific payment method
    @GET("api/v1/groups/{group_id}/membership/me/payment-method")
    suspend fun getMyGroupPaymentMethod(
        @Path("group_id") groupId: String
    ): Response<GroupPaymentMethodResponse>

    // Set my group-specific payment method
    @PATCH("api/v1/groups/{group_id}/membership/me/payment-method")
    suspend fun updateMyGroupPaymentMethod(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGroupPaymentMethodRequest
    ): Response<GroupPaymentMethodResponse>

    // List all user payment methods (for picker)
    @GET("api/v1/users/me/payment-methods")
    suspend fun getMyPaymentMethods(): Response<List<LinkedAccountResponse>>

    // Add user payment method
    @POST("api/v1/users/me/payment-methods")
    suspend fun addPaymentMethod(
        @Body payload: CreateLinkedAccountRequest
    ): Response<LinkedAccountResponse>

    // Export my records as a downloadable ZIP
    @Streaming
    @GET("api/v1/groups/{group_id}/membership/me/export")
    suspend fun exportMyRecords(
        @Path("group_id") groupId: String,
        @Query("include_chat_history") includeChatHistory: Boolean = false
    ): Response<ResponseBody>

    // Report a group
    @POST("api/v1/groups/{group_id}/report")
    suspend fun reportGroup(
        @Path("group_id") groupId: String,
        @Body payload: ReportGroupRequest
    ): Response<ReportGroupResponse>

    // View savings growth chart for current member
    @GET("api/v1/groups/{group_id}/membership/me/savings-growth")
    suspend fun getSavingsGrowth(
        @Path("group_id") groupId: String,
        @Query("period") period: String = "monthly"
    ): Response<SavingsGrowthResponse>

    // Download official savings statement (CSV)
    @Streaming
    @GET("api/v1/groups/{group_id}/statement/download")
    suspend fun downloadSavingsStatement(
        @Path("group_id") groupId: String,
        @Query("period_days") periodDays: Int = 30,
        @Query("format") format: String = "csv"
    ): Response<ResponseBody>

    // Leave / exit a group
    @DELETE("api/v1/groups/{group_id}/membership/me")
    suspend fun leaveGroup(
        @Path("group_id") groupId: String
    ): Response<LeaveGroupResponse>
}
