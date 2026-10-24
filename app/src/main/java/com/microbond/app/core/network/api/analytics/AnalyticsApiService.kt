package com.microbond.app.core.network.api.analytics

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for Microbond Analytics endpoints:
 * - GET /api/v1/groups/{groupId}/analytics/catalog
 * - GET /api/v1/groups/{groupId}/analytics/series
 */
interface AnalyticsApiService {

    @GET("api/v1/groups/{groupId}/analytics/catalog")
    suspend fun getAnalyticsCatalog(
        @Path("groupId") groupId: String
    ): Response<CatalogResponse>

    @GET("api/v1/groups/{groupId}/analytics/series")
    suspend fun getAnalyticsSeries(
        @Path("groupId") groupId: String,
        @Query("y") yMetric: String,
        @Query("x") xMetric: String = "time",
        @Query("days") days: Int? = null,
        @Query("bucket_days") bucketDays: Int? = null,
        @Query("calendar_unit") calendarUnit: String? = null,
        @Query("start_ms") startMs: Long? = null,
        @Query("end_ms") endMs: Long? = null
    ): Response<SeriesResponse>
}
