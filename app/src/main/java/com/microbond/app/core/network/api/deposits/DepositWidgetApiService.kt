package com.microbond.app.core.network.api.deposits

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for Microbond Daily Deposit Status Widget:
 * - GET /api/v1/groups/{groupId}/deposits/daily-widget
 * - GET /api/v1/groups/{groupId}/deposits/daily-widget/members
 * - POST /api/v1/groups/{groupId}/deposits/daily-widget/nudge
 * - PATCH /api/v1/groups/{groupId}/settings/deposit-widget-visibility
 */
interface DepositWidgetApiService {

    @GET("api/v1/groups/{groupId}/deposits/daily-widget")
    suspend fun getDailyDepositWidget(
        @Path("groupId") groupId: String,
        @Query("date_ms") dateMs: Long? = null
    ): Response<DailyDepositWidgetResponse>

    @GET("api/v1/groups/{groupId}/deposits/daily-widget/members")
    suspend fun getDepositWidgetMembers(
        @Path("groupId") groupId: String,
        @Query("status") status: String,
        @Query("date_ms") dateMs: Long? = null
    ): Response<DepositMembersResponse>

    @POST("api/v1/groups/{groupId}/deposits/daily-widget/nudge")
    suspend fun nudgeMember(
        @Path("groupId") groupId: String,
        @Body request: NudgeMemberRequest
    ): Response<NudgeMemberResponse>

    @PATCH("api/v1/groups/{groupId}/settings/deposit-widget-visibility")
    suspend fun updateDepositWidgetVisibility(
        @Path("groupId") groupId: String,
        @Body request: UpdateDepositWidgetVisibilityRequest
    ): Response<Map<String, Any>>
}
