package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

// ── Goal Controls Models ───────────────────────────────────────────────────

data class GoalControlsData(
    @SerializedName("group_id") val groupId: String = "",
    @SerializedName("group_name") val groupName: String? = null,
    @SerializedName("lock_funds", alternate = ["is_locked"]) val lockFunds: Boolean = false,
    @SerializedName("hide_leaderboard") val hideLeaderboard: Boolean = false,
    @SerializedName("let_members_view_total_saved", alternate = ["vis_member_total_savings"]) val letMembersViewTotalSaved: Boolean = true
)

data class GoalControlsResponse(
    val status: String = "success",
    val data: GoalControlsData? = null
)

data class UpdateGoalControlsRequest(
    @SerializedName("lock_funds") val lockFunds: Boolean? = null,
    @SerializedName("hide_leaderboard") val hideLeaderboard: Boolean? = null,
    @SerializedName("let_members_view_total_saved") val letMembersViewTotalSaved: Boolean? = null
)

// ── Goal Controls API Service Interface ────────────────────────────────────

interface GoalControlsApiService {

    @GET("api/v1/groups/{group_id}/settings/goal-controls")
    suspend fun getGoalControls(
        @Path("group_id") groupId: String
    ): Response<GoalControlsResponse>

    @PATCH("api/v1/groups/{group_id}/settings/goal-controls")
    suspend fun updateGoalControls(
        @Path("group_id") groupId: String,
        @Body payload: UpdateGoalControlsRequest
    ): Response<GoalControlsResponse>
}
