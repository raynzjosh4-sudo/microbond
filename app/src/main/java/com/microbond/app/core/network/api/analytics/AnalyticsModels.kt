package com.microbond.app.core.network.api.analytics

import com.google.gson.annotations.SerializedName

// ── Catalog Models ──────────────────────────────────────────────────────────
data class CatalogResponse(
    val status: String = "success",
    val count: Int = 0,
    val metrics: List<MetricCatalogItem> = emptyList()
)

data class MetricCatalogItem(
    val id: String = "",
    val name: String = "",
    val category: String = "", // "SAVING", "MEMBERS", "PAYOUTS"
    val unit: String = "",     // "UGX", "days", "count", "percent"
    val icon: String = "",
    @SerializedName("allowed_charts") val allowedCharts: List<String> = emptyList(),
    @SerializedName("allowed_granularities") val allowedGranularities: List<String> = emptyList()
)

// ── Series Models ────────────────────────────────────────────────────────────
data class SeriesRange(
    @SerializedName("start_ms") val startMs: Long = 0L,
    @SerializedName("end_ms") val endMs: Long = 0L
)

data class SeriesSummary(
    val min: Double = 0.0,
    val max: Double = 0.0,
    val avg: Double = 0.0,
    val total: Double = 0.0
)

data class SeriesMeta(
    @SerializedName("group_id") val groupId: String = "",
    val x: String = "time",
    val y: String = "",
    val range: SeriesRange? = null,
    @SerializedName("bucket_days") val bucketDays: Int? = null,
    @SerializedName("calendar_unit") val calendarUnit: String? = null,
    @SerializedName("points_count") val pointsCount: Int = 0,
    val unit: String = "",
    val summary: SeriesSummary = SeriesSummary()
)

data class SeriesPoint(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val label: String = ""
)

data class SeriesResponse(
    val status: String = "success",
    val meta: SeriesMeta = SeriesMeta(),
    val points: List<SeriesPoint> = emptyList()
)
