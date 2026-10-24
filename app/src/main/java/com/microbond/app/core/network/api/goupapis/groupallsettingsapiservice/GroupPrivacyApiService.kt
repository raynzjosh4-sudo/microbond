package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

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

// ── Group Privacy API Service Interface ─────────────────────────────────────

interface GroupPrivacyApiService {

    @GET("api/v1/groups/{group_id}/settings/privacy")
    suspend fun getGroupPrivacy(
        @Path("group_id") groupId: String
    ): Response<GroupPrivacyResponse>

    @PATCH("api/v1/groups/{group_id}/settings/privacy")
    suspend fun updateGroupPrivacy(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGroupPrivacyRequest
    ): Response<GroupPrivacyResponse>
}
