package com.microbond.app.feature_groups.data.mapper

import com.microbond.app.core.data_classes_and_enums.AreaChartLayer
import com.microbond.app.core.data_classes_and_enums.BoxPlotEntry
import com.microbond.app.core.data_classes_and_enums.CandlestickBar
import com.microbond.app.core.data_classes_and_enums.GroupHealthData
import com.microbond.app.core.data_classes_and_enums.HeatmapCell
import com.microbond.app.core.data_classes_and_enums.HistogramBar
import com.microbond.app.core.data_classes_and_enums.RadarChartPoint
import com.microbond.app.core.data_classes_and_enums.ScatterPoint
import com.microbond.app.core.data_classes_and_enums.StatAreaData
import com.microbond.app.core.data_classes_and_enums.StatBoxPlotData
import com.microbond.app.core.data_classes_and_enums.StatCandlestickData
import com.microbond.app.core.data_classes_and_enums.StatChartPoint
import com.microbond.app.core.data_classes_and_enums.StatCustomChartData
import com.microbond.app.core.data_classes_and_enums.StatDonutSlice
import com.microbond.app.core.data_classes_and_enums.StatGroupProgressData
import com.microbond.app.core.data_classes_and_enums.StatGroupSnapshotData
import com.microbond.app.core.data_classes_and_enums.StatHeatmapData
import com.microbond.app.core.data_classes_and_enums.StatHistogramData
import com.microbond.app.core.data_classes_and_enums.StatScatterData
import com.microbond.app.core.data_classes_and_enums.StatWaterfallData
import com.microbond.app.core.data_classes_and_enums.WaterfallBar

object ChatChartMessageMapper {

    fun parseHistogram(chartPayload: Map<*, *>): StatHistogramData {
        val rawBars = (chartPayload["bars"] as? List<*>) ?: emptyList<Any>()
        return StatHistogramData(
            title = (chartPayload["title"] as? String) ?: "Deposit history",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Total group savings by month",
            totalLabel = (chartPayload["total_label"] as? String) ?: "Total saved",
            totalValue = (chartPayload["total_value"] as? String) ?: "",
            highestLabel = (chartPayload["highest_label"] as? String) ?: "Highest month",
            highestValue = (chartPayload["highest_value"] as? String) ?: "",
            avgLabel = (chartPayload["avg_label"] as? String) ?: "Avg. monthly",
            avgValue = (chartPayload["avg_value"] as? String) ?: "",
            footerText = (chartPayload["footer_text"] as? String) ?: (chartPayload["footer"] as? String) ?: "Last 6 months",
            bars = if (rawBars.isNotEmpty()) {
                rawBars.mapNotNull { item ->
                    val barMap = item as? Map<*, *> ?: return@mapNotNull null
                    HistogramBar(
                        label = barMap["label"]?.toString() ?: "",
                        value = (barMap["value"] as? Number)?.toFloat() ?: 0f,
                        highlight = (barMap["highlight"] as? Boolean) ?: false,
                        colorValue = (barMap["color_value"] as? Number)?.toLong()
                    )
                }
            } else {
                listOf(
                    HistogramBar("May", 150000f, false),
                    HistogramBar("Jun", 200000f, false),
                    HistogramBar("Jul", 180000f, false),
                    HistogramBar("Aug", 350000f, true),
                    HistogramBar("Sep", 220000f, false)
                )
            }
        )
    }

    fun parseCandlestick(chartPayload: Map<*, *>): StatCandlestickData {
        val rawCandles = (chartPayload["candles"] as? List<*>) ?: emptyList<Any>()
        return StatCandlestickData(
            title = (chartPayload["title"] as? String) ?: "Price movement",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Open · High · Low · Close",
            currencySymbol = (chartPayload["currency_symbol"] as? String) ?: "UGX",
            highLabel = (chartPayload["high_label"] as? String) ?: "Period High",
            highValue = (chartPayload["high_value"] as? String) ?: "",
            lowLabel = (chartPayload["low_label"] as? String) ?: "Period Low",
            lowValue = (chartPayload["low_value"] as? String) ?: "",
            footerText = (chartPayload["footer_text"] as? String) ?: (chartPayload["footer"] as? String) ?: "Last 8 periods",
            candles = if (rawCandles.isNotEmpty()) {
                rawCandles.mapNotNull { item ->
                    val cMap = item as? Map<*, *> ?: return@mapNotNull null
                    CandlestickBar(
                        label = cMap["label"]?.toString() ?: "",
                        open = (cMap["open"] as? Number)?.toFloat() ?: 0f,
                        high = (cMap["high"] as? Number)?.toFloat() ?: 0f,
                        low = (cMap["low"] as? Number)?.toFloat() ?: 0f,
                        close = (cMap["close"] as? Number)?.toFloat() ?: 0f
                    )
                }
            } else {
                listOf(
                    CandlestickBar("W1", 90000f, 110000f, 85000f, 105000f),
                    CandlestickBar("W2", 105000f, 120000f, 98000f, 115000f),
                    CandlestickBar("W3", 115000f, 118000f, 92000f, 95000f)
                )
            }
        )
    }

    fun parseArea(chartPayload: Map<*, *>): StatAreaData {
        val rawLayers = (chartPayload["layers"] as? List<*>) ?: emptyList<Any>()
        val rawXLabels = (chartPayload["x_labels"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        return StatAreaData(
            title = (chartPayload["title"] as? String) ?: "Contribution breakdown",
            subtitle = (chartPayload["subtitle"] as? String) ?: "How savings grew over time",
            totalLabel = (chartPayload["total_label"] as? String) ?: "Total accumulated",
            totalValue = (chartPayload["total_value"] as? String) ?: "",
            footerText = (chartPayload["footer_text"] as? String) ?: "Last 6 months",
            xLabels = if (rawXLabels.isNotEmpty()) rawXLabels else listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
            layers = if (rawLayers.isNotEmpty()) {
                rawLayers.mapNotNull { item ->
                    val lMap = item as? Map<*, *> ?: return@mapNotNull null
                    val rawValues = (lMap["values"] as? List<*>) ?: emptyList<Any>()
                    AreaChartLayer(
                        label = lMap["label"]?.toString() ?: "",
                        colorValue = (lMap["color_value"] as? Number)?.toLong() ?: 0xFF4ADE80,
                        values = rawValues.mapNotNull { (it as? Number)?.toFloat() }
                    )
                }
            } else {
                listOf(
                    AreaChartLayer("Regular Savings", 0xFF00A884, listOf(200000f, 450000f, 700000f, 1100000f, 1500000f, 2000000f)),
                    AreaChartLayer("Welfare Fund", 0xFF3B82F6, listOf(50000f, 100000f, 150000f, 220000f, 300000f, 400000f))
                )
            }
        )
    }

    fun parseWaterfall(chartPayload: Map<*, *>): StatWaterfallData {
        val rawBars = (chartPayload["bars"] as? List<*>) ?: emptyList<Any>()
        return StatWaterfallData(
            title = (chartPayload["title"] as? String) ?: "Cash flow",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Inflows & outflows this cycle",
            currencySymbol = (chartPayload["currency_symbol"] as? String) ?: "UGX",
            netLabel = (chartPayload["net_label"] as? String) ?: "Net change",
            netValue = (chartPayload["net_value"] as? String) ?: "",
            footerText = (chartPayload["footer_text"] as? String) ?: "Current savings cycle",
            bars = if (rawBars.isNotEmpty()) {
                rawBars.mapNotNull { item ->
                    val bMap = item as? Map<*, *> ?: return@mapNotNull null
                    WaterfallBar(
                        label = bMap["label"]?.toString() ?: "",
                        value = (bMap["value"] as? Number)?.toFloat() ?: 0f,
                        isTotal = (bMap["is_total"] as? Boolean) ?: false
                    )
                }
            } else {
                listOf(
                    WaterfallBar("Opening", 500000f, false),
                    WaterfallBar("Deposits", 450000f, false),
                    WaterfallBar("Payouts", -600000f, false),
                    WaterfallBar("Penalties", 30000f, false),
                    WaterfallBar("Net Closing", 380000f, true)
                )
            }
        )
    }

    fun parseScatter(chartPayload: Map<*, *>): StatScatterData {
        val rawPoints = (chartPayload["points"] as? List<*>) ?: emptyList<Any>()
        return StatScatterData(
            title = (chartPayload["title"] as? String) ?: "Member correlation",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Deposit rate vs. on-time rate",
            xAxisLabel = (chartPayload["x_axis_label"] as? String) ?: (chartPayload["x_label"] as? String) ?: "Deposit rate",
            yAxisLabel = (chartPayload["y_axis_label"] as? String) ?: (chartPayload["y_label"] as? String) ?: "On-time %",
            correlationLabel = (chartPayload["correlation_label"] as? String) ?: "Correlation",
            correlationValue = (chartPayload["correlation_value"] as? String) ?: "0.82 (strong)",
            points = if (rawPoints.isNotEmpty()) {
                rawPoints.mapNotNull { item ->
                    val pMap = item as? Map<*, *> ?: return@mapNotNull null
                    ScatterPoint(
                        x = (pMap["x"] as? Number)?.toFloat() ?: 0f,
                        y = (pMap["y"] as? Number)?.toFloat() ?: 0f,
                        label = pMap["label"]?.toString() ?: ""
                    )
                }
            } else {
                listOf(
                    ScatterPoint(95f, 98f, "Member A"),
                    ScatterPoint(80f, 85f, "Member B"),
                    ScatterPoint(60f, 70f, "Member C")
                )
            }
        )
    }

    fun parseBoxPlot(chartPayload: Map<*, *>): StatBoxPlotData {
        val rawEntries = (chartPayload["entries"] as? List<*>) ?: emptyList<Any>()
        return StatBoxPlotData(
            title = (chartPayload["title"] as? String) ?: "Deposit distribution",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Min · Q1 · Median · Q3 · Max",
            currencySymbol = (chartPayload["currency_symbol"] as? String) ?: "UGX",
            footerText = (chartPayload["footer_text"] as? String) ?: "Last 6 months per member",
            entries = if (rawEntries.isNotEmpty()) {
                rawEntries.mapNotNull { item ->
                    val eMap = item as? Map<*, *> ?: return@mapNotNull null
                    BoxPlotEntry(
                        label = eMap["label"]?.toString() ?: "",
                        min = (eMap["min"] as? Number)?.toFloat() ?: 0f,
                        q1 = (eMap["q1"] as? Number)?.toFloat() ?: 0f,
                        median = (eMap["median"] as? Number)?.toFloat() ?: 0f,
                        q3 = (eMap["q3"] as? Number)?.toFloat() ?: 0f,
                        max = (eMap["max"] as? Number)?.toFloat() ?: 0f,
                        colorValue = (eMap["color_value"] as? Number)?.toLong() ?: 0xFF4ADE80
                    )
                }
            } else {
                listOf(
                    BoxPlotEntry("Cycle 1", 50000f, 70000f, 85000f, 100000f, 120000f),
                    BoxPlotEntry("Cycle 2", 40000f, 60000f, 80000f, 95000f, 110000f)
                )
            }
        )
    }

    fun parseHeatmap(chartPayload: Map<*, *>): StatHeatmapData {
        val rawCells = (chartPayload["cells"] as? List<*>) ?: emptyList<Any>()
        val rawRowLabels = (chartPayload["row_labels"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        val rawColLabels = (chartPayload["col_labels"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        return StatHeatmapData(
            title = (chartPayload["title"] as? String) ?: "Member payment heatmap",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Deposit activity per member per month",
            footerText = (chartPayload["footer_text"] as? String) ?: "Green = paid · Red = missed",
            rowLabels = if (rawRowLabels.isNotEmpty()) rawRowLabels else listOf("Member 1", "Member 2", "Member 3"),
            colLabels = if (rawColLabels.isNotEmpty()) rawColLabels else listOf("May", "Jun", "Jul", "Aug", "Sep"),
            cells = if (rawCells.isNotEmpty()) {
                rawCells.mapNotNull { item ->
                    val cMap = item as? Map<*, *> ?: return@mapNotNull null
                    HeatmapCell(
                        rowLabel = cMap["row_label"]?.toString() ?: "",
                        colLabel = cMap["col_label"]?.toString() ?: "",
                        value = (cMap["value"] as? Number)?.toFloat() ?: 0f,
                        displayText = cMap["display_text"]?.toString() ?: ""
                    )
                }
            } else {
                listOf(
                    HeatmapCell("Member 1", "May", 1f, "Paid"),
                    HeatmapCell("Member 1", "Jun", 1f, "Paid"),
                    HeatmapCell("Member 2", "Jun", 0f, "Missed")
                )
            }
        )
    }

    fun parseGroupSnapshot(chartPayload: Map<*, *>): StatGroupSnapshotData {
        val rawSlices = (chartPayload["slices"] as? List<*>) ?: emptyList<Any>()
        return StatGroupSnapshotData(
            title = (chartPayload["title"] as? String) ?: "Savings group snapshot",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Where this month's money went",
            membersLabel = (chartPayload["members_label"] as? String) ?: "Members",
            membersValue = (chartPayload["members_value"] as? String) ?: "15",
            collectedLabel = (chartPayload["collected_label"] as? String) ?: "Collected",
            collectedValue = (chartPayload["collected_value"] as? String) ?: (chartPayload["total_amount"] as? String) ?: "UGX 3,450,000",
            nextPayoutLabel = (chartPayload["next_payout_label"] as? String) ?: "Next payout",
            nextPayoutValue = (chartPayload["next_payout_value"] as? String) ?: "Friday",
            slices = if (rawSlices.isNotEmpty()) {
                rawSlices.mapNotNull { item ->
                    val sMap = item as? Map<*, *> ?: return@mapNotNull null
                    StatDonutSlice(
                        label = sMap["label"]?.toString() ?: "",
                        percentage = (sMap["percentage"] as? Number)?.toInt() ?: 0,
                        colorValue = (sMap["color_value"] as? Number)?.toLong() ?: 0xFF4ADE80
                    )
                }
            } else {
                listOf(
                    StatDonutSlice("Paid out", 61, 0xFF00A884),
                    StatDonutSlice("In reserve", 26, 0xFF3B82F6),
                    StatDonutSlice("Welfare fund", 13, 0xFFF59E0B)
                )
            }
        )
    }

    fun parseGroupHealth(chartPayload: Map<*, *>): GroupHealthData {
        val rawPoints = (chartPayload["radar_points"] as? List<*>) ?: (chartPayload["points"] as? List<*>) ?: emptyList<Any>()
        val parsedPoints = if (rawPoints.isNotEmpty()) {
            rawPoints.mapNotNull { item ->
                val pMap = item as? Map<*, *> ?: return@mapNotNull null
                RadarChartPoint(
                    label = pMap["label"]?.toString() ?: "",
                    value = (pMap["value"] as? Number)?.toFloat() ?: 0.5f
                )
            }
        } else {
            listOf(
                RadarChartPoint("Trust", 0.9f),
                RadarChartPoint("Speed", 0.85f),
                RadarChartPoint("Participation", 0.92f),
                RadarChartPoint("Transparency", 0.88f)
            )
        }
        return GroupHealthData(
            title = (chartPayload["title"] as? String) ?: "Group health",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Current member experience",
            radarPoints = parsedPoints,
            chartColorValue = (chartPayload["chart_color_value"] as? Number)?.toLong(),
            strongestText = (chartPayload["strongest_text"] as? String) ?: "trust + support",
            focusNextText = (chartPayload["focus_next_text"] as? String) ?: "payout speed",
            footerText = (chartPayload["footer_text"] as? String) ?: "15 members · 12 responses"
        )
    }

    fun parseGroupProgress(chartPayload: Map<*, *>): StatGroupProgressData {
        val rawPoints = (chartPayload["chart_points"] as? List<*>) ?: emptyList<Any>()
        return StatGroupProgressData(
            title = (chartPayload["title"] as? String) ?: "Group statistics",
            subtitle = (chartPayload["subtitle"] as? String) ?: "Payout progress · last 6 months",
            onTimePayoutsLabel = (chartPayload["on_time_payouts_label"] as? String) ?: "On-time payouts",
            onTimePayoutsValue = (chartPayload["on_time_payouts_value"] as? String) ?: "91%",
            activeMembersLabel = (chartPayload["active_members_label"] as? String) ?: "Active members",
            activeMembersValue = (chartPayload["active_members_value"] as? String) ?: "15",
            avgWaitLabel = (chartPayload["avg_wait_label"] as? String) ?: "Avg. wait",
            avgWaitValue = (chartPayload["avg_wait_value"] as? String) ?: "3 days",
            updatedNote = (chartPayload["updated_note"] as? String) ?: "Updated today",
            chartPoints = if (rawPoints.isNotEmpty()) {
                rawPoints.mapNotNull { item ->
                    val cpMap = item as? Map<*, *> ?: return@mapNotNull null
                    StatChartPoint(
                        label = cpMap["label"]?.toString() ?: "",
                        value = (cpMap["value"] as? Number)?.toFloat() ?: 0f
                    )
                }
            } else {
                listOf(
                    StatChartPoint("M1", 80f),
                    StatChartPoint("M2", 90f),
                    StatChartPoint("M3", 95f),
                    StatChartPoint("M4", 88f),
                    StatChartPoint("M5", 92f)
                )
            }
        )
    }

    fun parseCustomChart(chartPayload: Map<*, *>, fallbackTitle: String): StatCustomChartData {
        val rawXValues = (chartPayload["x_values"] as? List<*>) ?: emptyList<Any>()
        val rawYValues = (chartPayload["y_values"] as? List<*>) ?: emptyList<Any>()
        return StatCustomChartData(
            chartId = (chartPayload["chart_id"] as? String) ?: (chartPayload["id"] as? String) ?: "custom_chart",
            title = (chartPayload["title"] as? String) ?: fallbackTitle.ifBlank { "Custom Chart" },
            subtitle = (chartPayload["subtitle"] as? String) ?: "",
            xLabel = (chartPayload["x_label"] as? String) ?: "",
            yLabel = (chartPayload["y_label"] as? String) ?: "",
            primaryColorValue = (chartPayload["primary_color_value"] as? Number)?.toLong() ?: 0xFF00A884,
            secondaryColorValue = (chartPayload["secondary_color_value"] as? Number)?.toLong() ?: 0xFF3B82F6,
            xValues = rawXValues.mapNotNull { (it as? Number)?.toFloat() },
            yValues = rawYValues.mapNotNull { (it as? Number)?.toFloat() }
        )
    }
}
