package com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.analytics

import android.util.Log
import com.microbond.app.core.network.NetworkClient
import com.microbond.app.core.network.api.analytics.CatalogResponse
import com.microbond.app.core.network.api.analytics.SeriesResponse
import com.microbond.app.core.network.api.analytics.SeriesSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Parsed and prepared series data ready for rendering on Compose Chart Canvas
 * and passing to [StatCustomChartData].
 */
data class ParsedChartSeries(
    val xValues: List<Float>,
    val yValues: List<Float>,
    val labels: List<String>,
    val summary: SeriesSummary,
    val unit: String
)

/**
 * Repository responsible for calling the Microbond Analytics API:
 * - GET /api/v1/groups/{groupId}/analytics/catalog
 * - GET /api/v1/groups/{groupId}/analytics/series
 */
object AnalyticsChartRepository {

    private const val TAG = "AnalyticsChartRepo"

    /**
     * Fetch dynamic metric catalog from backend.
     */
    suspend fun fetchCatalog(groupId: String): Result<CatalogResponse> = withContext(Dispatchers.IO) {
        if (groupId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID must not be blank"))
        }
        try {
            val response = NetworkClient.apiService.getAnalyticsCatalog(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Catalog request failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching catalog for group $groupId", e)
            Result.failure(e)
        }
    }

    /**
     * Query dynamic time series or correlation points with arbitrary day ranges.
     */
    suspend fun fetchSeries(
        groupId: String,
        yMetric: String,
        xMetric: String = "time",
        granularityId: String = "week",
        customDays: Int? = null,
        customBucketDays: Int? = null,
        startMs: Long? = null,
        endMs: Long? = null
    ): Result<ParsedChartSeries> = withContext(Dispatchers.IO) {
        if (groupId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Group ID must not be blank"))
        }

        val (daysParam, bucketDaysParam, calendarUnitParam) = when (granularityId.lowercase()) {
            "custom" -> Triple(customDays ?: 30, customBucketDays ?: 1, null)
            "day", "daily" -> Triple(30, 1, null)
            "week", "weekly" -> Triple(84, 7, null)         // 12 weeks
            "month", "monthly" -> Triple(null, null, "month") // Calendar months
            "year", "yearly" -> Triple(null, null, "year")   // Calendar years
            else -> Triple(30, 1, null)
        }

        try {
            val response = NetworkClient.apiService.getAnalyticsSeries(
                groupId = groupId,
                yMetric = yMetric,
                xMetric = xMetric,
                days = daysParam,
                bucketDays = bucketDaysParam,
                calendarUnit = calendarUnitParam,
                startMs = startMs,
                endMs = endMs
            )

            if (response.isSuccessful && response.body() != null) {
                val body: SeriesResponse = response.body()!!
                val rawPoints = body.points

                val xVals = mutableListOf<Float>()
                val yVals = mutableListOf<Float>()
                val lbls = mutableListOf<String>()

                if (rawPoints.isNotEmpty()) {
                    rawPoints.forEachIndexed { index, pt ->
                        xVals.add(if (xMetric == "time") index.toFloat() else pt.x.toFloat())
                        yVals.add(pt.y.toFloat())
                        lbls.add(pt.label)
                    }
                } else {
                    // Graceful zero-fill if group has no transaction history yet
                    for (i in 0..6) {
                        xVals.add(i.toFloat())
                        yVals.add(0f)
                        lbls.add("P$i")
                    }
                }

                Result.success(
                    ParsedChartSeries(
                        xValues = xVals,
                        yValues = yVals,
                        labels = lbls,
                        summary = body.meta.summary,
                        unit = body.meta.unit
                    )
                )
            } else {
                val err = response.errorBody()?.string() ?: response.message()
                Log.w(TAG, "Series request returned error: ${response.code()} $err")
                Result.failure(Exception("Series error ${response.code()}: $err"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching series for group $groupId, metric $yMetric", e)
            Result.failure(e)
        }
    }
}
