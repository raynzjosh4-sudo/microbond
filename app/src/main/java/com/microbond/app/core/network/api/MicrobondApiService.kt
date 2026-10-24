package com.microbond.app.core.network.api

import com.microbond.app.core.network.api.analytics.AnalyticsApiService
import com.microbond.app.core.network.api.authenticationapi.AuthApiService
import com.microbond.app.core.network.api.deposits.DepositWidgetApiService
import com.microbond.app.core.network.api.goupapis.GroupsApiService
import com.microbond.app.core.network.api.goupapis.chatapiservice.GroupChatApiService
import com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice.GroupSettingsApiService
import com.microbond.app.core.network.api.goupapis.rotatinggroupapiservice.RotatingApiService
import com.microbond.app.core.network.api.userapiservice.UserApiService
import retrofit2.Response
import retrofit2.http.GET

/**
 * Unified Microbond Retrofit API Service interface.
 * Combines all modular domain API services into a single unified contract for Retrofit.
 */
interface MicrobondApiService :
    AuthApiService,
    UserApiService,
    ContactsApiService,
    GroupsApiService,
    RotatingApiService,
    GroupChatApiService,
    AnalyticsApiService,
    DepositWidgetApiService,
    GroupSettingsApiService {

    // Health check endpoint
    @GET("healthz")
    suspend fun checkHealth(): Response<Map<String, String>>
}
