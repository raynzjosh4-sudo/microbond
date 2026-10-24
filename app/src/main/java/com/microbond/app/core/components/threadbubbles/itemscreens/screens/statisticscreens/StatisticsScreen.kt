package com.microbond.app.core.components.threadbubbles.itemscreens.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterfallChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondActivePillDark
import com.microbond.app.core.ui.theme.MicrobondChipNonSelectedColor
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.widgets.ChartPreviewPane
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.analytics.AnalyticsChartRepository
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.analytics.ParsedChartSeries
import com.microbond.app.core.data_classes_and_enums.StatCustomChartData
import com.microbond.app.core.dummydata.metricSampleData
import kotlinx.coroutines.launch
import java.util.Locale

// ─── Domain models ────────────────────────────────────────────────────────────
data class AxisMetric(
    val id: String, val label: String, val icon: ImageVector,
    val category: String, val isTime: Boolean = false
)
data class ChartTypeOpt(val id: String, val label: String, val icon: ImageVector)
data class TimeGranularity(val id: String, val label: String)

// ─── Static catalogues ────────────────────────────────────────────────────────
val timeGranularities = listOf(
    TimeGranularity("day", "Daily"), TimeGranularity("week", "Weekly"),
    TimeGranularity("month", "Monthly"), TimeGranularity("year", "Yearly")
)

val axisMetrics = listOf(
    AxisMetric("time",           "Time",                        Icons.Default.Schedule,      "All",     true),
    AxisMetric("daily_saving",   "Daily saving",                Icons.Default.LocalAtm,      "Saving"),
    AxisMetric("successful_days","Successful save-days",        Icons.Default.CalendarMonth, "Saving"),
    AxisMetric("full_save_days", "Full-save days",              Icons.Default.CalendarMonth, "Saving"),
    AxisMetric("partial_save",   "Partial-save days",           Icons.Default.BarChart,      "Saving"),
    AxisMetric("no_save_days",   "No-save days",                Icons.Default.Warning,       "Saving"),
    AxisMetric("saving_streak",  "Average saving streak",       Icons.Default.Star,          "Saving"),
    AxisMetric("missed_rate",    "Missed-saving rate",          Icons.Default.Percent,       "Saving"),
    AxisMetric("total_accum",    "Total accumulation",          Icons.Default.AccountBalance,"Saving"),
    AxisMetric("wow_change",     "Week-over-week change",       Icons.Default.SyncAlt,       "Saving"),
    AxisMetric("busiest_day",    "Busiest saving day",          Icons.Default.TrendingUp,    "Saving"),
    AxisMetric("target_met",     "Target met rate",             Icons.Default.Percent,       "Saving"),
    AxisMetric("group_activity", "Total group activity",        Icons.Default.QueryStats,    "Saving"),
    AxisMetric("active_members", "Active members",              Icons.Default.Groups,        "Members"),
    AxisMetric("new_members",    "New members",                 Icons.Default.PersonAdd,     "Members"),
    AxisMetric("members_balance","Members with balance",        Icons.Default.Warning,       "Members"),
    AxisMetric("members_cleared","Members who cleared balance", Icons.Default.CheckCircle,   "Members"),
    AxisMetric("members_paid",   "Members paid out",            Icons.Default.MonetizationOn,"Members"),
    AxisMetric("payouts_done",   "Payouts completed",           Icons.Default.TrendingUp,    "Payouts"),
    AxisMetric("payouts_pending","Payouts pending",             Icons.Default.Alarm,         "Payouts"),
    AxisMetric("next_payout",    "Next payout date",            Icons.Default.CalendarMonth, "Payouts"),
    AxisMetric("payout_progress","Payout progress",             Icons.Default.Speed,         "Payouts"),
    AxisMetric("avg_deposit",    "Average deposit size",        Icons.Default.LocalAtm,      "Payouts")
)

val statChartTypes = listOf(
    ChartTypeOpt("bar",         "Bar",         Icons.Default.BarChart),
    ChartTypeOpt("line",        "Line",        Icons.Default.TrendingUp),
    ChartTypeOpt("area",        "Area",        Icons.Default.AreaChart),
    ChartTypeOpt("scatter",     "Scatter",     Icons.Default.BubbleChart),
    ChartTypeOpt("waterfall",   "Waterfall",   Icons.Default.WaterfallChart),
    ChartTypeOpt("boxplot",     "Box Plot",    Icons.Default.QueryStats),
    ChartTypeOpt("heatmap",     "Heatmap",     Icons.Default.GridOn),
    ChartTypeOpt("donut",       "Donut",       Icons.Default.PieChart),
    ChartTypeOpt("radar",       "Radar",       Icons.Default.DataUsage),
    ChartTypeOpt("candlestick", "Candlestick", Icons.Default.CandlestickChart)
)

val chartAccentColors = listOf(
    Color(0xFF25D366), Color(0xFF00A884), Color(0xFF34B7F1),
    Color(0xFF4FC3F7), Color(0xFF1565C0), Color(0xFF7C4DFF),
    Color(0xFFAB47BC), Color(0xFFE91E63), Color(0xFFFF5252),
    Color(0xFFE53935), Color(0xFFF57C00), Color(0xFFFFCA28),
    Color(0xFF66BB6A), Color(0xFF26A69A), Color(0xFF78909C),
    Color(0xFFFFFFFF)
)

val statCategories = listOf("All", "Saving", "Members", "Payouts")

// ─── Main screen ─────────────────────────────────────────────────────────────
@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    onPreview: (String) -> Unit,
    groupId: String = "",
    onSendChart: ((StatCustomChartData) -> Unit)? = null
) {
    val coroutineScope   = rememberCoroutineScope()
    var liveSeriesData   by remember { mutableStateOf<ParsedChartSeries?>(null) }
    var isLoadingSeries  by remember { mutableStateOf(false) }
    var showPreview      by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedXId      by remember { mutableStateOf("time") }
    var selectedYId      by remember { mutableStateOf("successful_days") }
    var selectedTimeGran by remember { mutableStateOf("week") }
    var customTimePeriod by remember { mutableIntStateOf(0) } // 0 = none
    var selectedChartId  by remember { mutableStateOf(statChartTypes[0].id) }
    val selectedColors   = remember { mutableStateListOf(chartAccentColors[0]) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customInput      by remember { mutableStateOf("") }

    val filtered = remember(selectedCategory) {
        if (selectedCategory == "All") axisMetrics
        else axisMetrics.filter { it.category == selectedCategory || it.isTime }
    }

    val xLabel     = axisMetrics.find { it.id == selectedXId }?.label ?: "-"
    val yLabel     = axisMetrics.find { it.id == selectedYId }?.label ?: "-"
    val granLabel  = if ((selectedXId == "time" || selectedYId == "time") && customTimePeriod == 0)
        " · ${timeGranularities.find { it.id == selectedTimeGran }?.label ?: ""}"
    else if (customTimePeriod > 0) " · Last $customTimePeriod periods" else ""
    val chartLabel = statChartTypes.find { it.id == selectedChartId }?.label ?: ""

    // Intercept system back: if in preview → back to builder; otherwise normal back
    BackHandler(enabled = showPreview) {
        showPreview = false
    }

    AnimatedContent(
        targetState = showPreview,
        transitionSpec = {
            if (targetState) {
                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
            } else {
                (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
            }
        },
        label = "stats_nav"
    ) { preview ->
        if (preview) {
            ChartPreviewPane(
                xId = selectedXId, yId = selectedYId,
                xLabel = xLabel, yLabel = yLabel,
                chartId = selectedChartId, chartLabel = chartLabel,
                colors = selectedColors.toList(),
                granLabel = granLabel,
                liveSeries = liveSeriesData,
                onBack = { showPreview = false },
                onSend = {
                    if (onSendChart == null) {
                        onPreview("$chartLabel: $yLabel · $xLabel$granLabel")
                    } else {
                        val primary = selectedColors.firstOrNull() ?: MicrobondActiveGreen
                        val secondary = selectedColors.getOrNull(1) ?: primary
                        val xVals = liveSeriesData?.xValues
                            ?: metricSampleData[selectedXId]
                            ?: listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f)
                        val yVals = liveSeriesData?.yValues
                            ?: metricSampleData[selectedYId]
                            ?: listOf(10f, 20f, 15f, 30f, 25f, 40f, 35f, 45f)
                        onSendChart(
                            StatCustomChartData(
                                chartId = selectedChartId,
                                title = yLabel,
                                subtitle = "$xLabel$granLabel",
                                xLabel = xLabel,
                                yLabel = yLabel,
                                xValues = xVals,
                                yValues = yVals,
                                primaryColorValue = primary.toArgb().toLong(),
                                secondaryColorValue = secondary.toArgb().toLong()
                            )
                        )
                    }
                }
            )
        } else {
            Surface(modifier = Modifier.fillMaxSize(), color = MicrobondDarkBackground) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // ── Top bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MicrobondDividerDark)
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MicrobondTextPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "$yLabel  ·  $xLabel$granLabel",
                                    color = MicrobondTextPrimary, fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium, maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("$chartLabel chart builder", color = MicrobondInactiveGray, fontSize = 12.sp)
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                selectedColors.forEach { c ->
                                    Box(
                                        modifier = Modifier.size(16.dp).clip(CircleShape)
                                            .background(c).border(1.5.dp, MicrobondDarkBackground, CircleShape)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = MicrobondDividerDark)

                        // ── Chart type strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MicrobondDarkBackground)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            statChartTypes.forEach { ct ->
                                val sel = ct.id == selectedChartId
                                Column(
                                    modifier = Modifier
                                        .width(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) MicrobondActivePillDark else MicrobondChipNonSelectedColor)
                                        .border(if (sel) 1.dp else 0.dp,
                                            if (sel) MicrobondActiveGreen else Color.Transparent,
                                            RoundedCornerShape(10.dp))
                                        .clickable { selectedChartId = ct.id }
                                        .padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(ct.icon, null,
                                        tint = if (sel) MicrobondActiveGreen else MicrobondInactiveGray,
                                        modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.height(4.dp))
                                    Text(ct.label,
                                        color = if (sel) MicrobondActiveGreen else MicrobondInactiveGray,
                                        fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }

                        HorizontalDivider(color = MicrobondDividerDark, thickness = 6.dp)

                        // ── Color picker
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MicrobondDarkBackground)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Colors", color = MicrobondInactiveGray, fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            chartAccentColors.forEach { color ->
                                val isSel = selectedColors.contains(color)
                                Box(
                                    modifier = Modifier.size(26.dp).clip(CircleShape)
                                        .background(color)
                                        .border(if (isSel) 2.dp else 0.dp,
                                            MicrobondTextPrimary, CircleShape)
                                        .clickable {
                                            if (isSel) {
                                                if (selectedColors.size > 1) selectedColors.remove(color)
                                            } else {
                                                if (selectedColors.size < 2) selectedColors.add(color)
                                                else selectedColors[1] = color
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSel) Icon(Icons.Default.Check, null,
                                        tint = Color.Black, modifier = Modifier.size(13.dp))
                                }
                            }
                        }

                        HorizontalDivider(color = MicrobondDividerDark)

                        // ── Category chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MicrobondDarkBackground)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            statCategories.forEach { cat ->
                                val sel = cat == selectedCategory
                                Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .clip(CircleShape)
                                        .background(if (sel) MicrobondActivePillDark else MicrobondDarkBackground)
                                        .then(if (!sel) Modifier.border(1.dp, MicrobondDividerDark, CircleShape) else Modifier)
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat,
                                        color = if (sel) MicrobondActiveGreen else MicrobondTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = if (sel) FontWeight.Medium else FontWeight.Normal)
                                }
                            }
                        }

                        HorizontalDivider(color = MicrobondDividerDark)

                        // ── X / Y headers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MicrobondDividerDark)
                                .padding(vertical = 6.dp)
                        ) {
                            Text("X — axis", color = MicrobondActiveGreen, fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f).padding(start = 20.dp))
                            Text("Y — axis", color = MicrobondInactiveGray, fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f).padding(start = 16.dp))
                        }
                        HorizontalDivider(color = MicrobondDividerDark)

                        // ── Dual-column metric list
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(filtered, key = { it.id }) { metric ->
                                val xSel = selectedXId == metric.id
                                val ySel = selectedYId == metric.id
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                                        MetricRowItem(metric, xSel, MicrobondActiveGreen,
                                            { selectedXId = metric.id }, Modifier.weight(1f))
                                        VerticalDivider(color = MicrobondDividerDark, thickness = 1.dp,
                                            modifier = Modifier.fillMaxHeight())
                                        MetricRowItem(metric, ySel,
                                            selectedColors.firstOrNull() ?: MicrobondActiveGreen,
                                            { selectedYId = metric.id }, Modifier.weight(1f))
                                    }

                                    // Time granularity expander
                                    AnimatedVisibility(
                                        visible = metric.isTime && (xSel || ySel),
                                        enter = expandVertically(), exit = shrinkVertically()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MicrobondChipNonSelectedColor)
                                                .horizontalScroll(rememberScrollState())
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            timeGranularities.forEach { gran ->
                                                val g = selectedTimeGran == gran.id && customTimePeriod == 0
                                                Box(
                                                    modifier = Modifier
                                                        .height(30.dp)
                                                        .clip(CircleShape)
                                                        .background(if (g) MicrobondActivePillDark else MicrobondDarkBackground)
                                                        .border(1.dp, if (g) MicrobondActiveGreen else MicrobondDividerDark, CircleShape)
                                                        .clickable { selectedTimeGran = gran.id; customTimePeriod = 0 }
                                                        .padding(horizontal = 14.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(gran.label,
                                                        color = if (g) MicrobondActiveGreen else MicrobondTextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (g) FontWeight.Medium else FontWeight.Normal)
                                                }
                                            }

                                            // Custom period chip with + icon
                                            val customActive = customTimePeriod > 0
                                            Box(
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .clip(CircleShape)
                                                    .background(if (customActive) MicrobondActivePillDark else MicrobondDarkBackground)
                                                    .border(1.dp, if (customActive) MicrobondActiveGreen else MicrobondDividerDark, CircleShape)
                                                    .clickable { showCustomDialog = true }
                                                    .padding(horizontal = 12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(Icons.Default.Add, null,
                                                        tint = if (customActive) MicrobondActiveGreen else MicrobondInactiveGray,
                                                        modifier = Modifier.size(13.dp))
                                                    Text(
                                                        if (customActive) "$customTimePeriod periods" else "Custom",
                                                        color = if (customActive) MicrobondActiveGreen else MicrobondTextPrimary,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    HorizontalDivider(color = MicrobondDividerDark, thickness = 0.5.dp)
                                }
                            }
                        }
                    }

                    // ── Box-shaped FAB (rounded rectangle like arrow button)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(MicrobondActiveGreen)
                            .clickable(enabled = !isLoadingSeries) {
                                if (groupId.isNotBlank()) {
                                    isLoadingSeries = true
                                    coroutineScope.launch {
                                        val result = AnalyticsChartRepository.fetchSeries(
                                            groupId = groupId,
                                            yMetric = selectedYId,
                                            xMetric = selectedXId,
                                            granularityId = selectedTimeGran,
                                            customDays = if (customTimePeriod > 0) customTimePeriod else null
                                        )
                                        liveSeriesData = result.getOrNull()
                                        isLoadingSeries = false
                                        showPreview = true
                                    }
                                } else {
                                    showPreview = true
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        if (isLoadingSeries) {
                            CircularProgressIndicator(
                                color = MicrobondDarkBackground,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Preview",
                                tint = MicrobondDarkBackground, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }

    // ── Custom period dialog
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false; customInput = "" },
            title = { Text("Custom time period", color = MicrobondTextPrimary) },
            text = {
                OutlinedTextField(
                    value = customInput,
                    onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) customInput = it },
                    label = { Text("Number of periods") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MicrobondActiveGreen,
                        focusedLabelColor = MicrobondActiveGreen,
                        focusedTextColor = MicrobondTextPrimary,
                        unfocusedTextColor = MicrobondTextPrimary,
                        unfocusedBorderColor = MicrobondDividerDark
                    )
                )
            },
            confirmButton = {
                TextButton(enabled = customInput.isNotBlank(), onClick = {
                    customTimePeriod = customInput.toIntOrNull() ?: 0
                    customInput = ""
                    showCustomDialog = false
                }) { Text("Set", color = MicrobondActiveGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false; customInput = "" }) {
                    Text("Cancel", color = MicrobondInactiveGray)
                }
            },
            containerColor = MicrobondChipNonSelectedColor,
            titleContentColor = MicrobondTextPrimary,
            textContentColor = MicrobondTextPrimary
        )
    }
}

// ─── Shared composables ───────────────────────────────────────────────────────
@Composable
private fun MetricRowItem(
    metric: AxisMetric, isSelected: Boolean,
    accentColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) accentColor.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(metric.icon, null,
            tint = if (isSelected) accentColor else MicrobondInactiveGray,
            modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(metric.label,
            color = if (isSelected) MicrobondTextPrimary else MicrobondInactiveGray,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f), maxLines = 2, lineHeight = 16.sp)
        Spacer(Modifier.width(4.dp))
        Icon(if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            null,
            tint = if (isSelected) accentColor else MicrobondDividerDark,
            modifier = Modifier.size(18.dp))
    }
}