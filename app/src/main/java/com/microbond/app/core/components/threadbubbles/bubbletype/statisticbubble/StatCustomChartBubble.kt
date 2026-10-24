package com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
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
import com.microbond.app.core.data_classes_and_enums.StatCustomChartData
import com.microbond.app.core.data_classes_and_enums.StatChartPoint
import com.microbond.app.core.data_classes_and_enums.StatDonutSlice
import com.microbond.app.core.data_classes_and_enums.StatGroupProgressData
import com.microbond.app.core.data_classes_and_enums.StatGroupSnapshotData
import com.microbond.app.core.data_classes_and_enums.StatHeatmapData
import com.microbond.app.core.data_classes_and_enums.StatHistogramData
import com.microbond.app.core.data_classes_and_enums.StatScatterData
import com.microbond.app.core.data_classes_and_enums.StatWaterfallData
import com.microbond.app.core.data_classes_and_enums.WaterfallBar

@Composable
fun StatCustomChartBubble(
    meta: MicroBondMeta,
    data: StatCustomChartData,
    modifier: Modifier = Modifier
) {
    val count = minOf(data.xValues.size, data.yValues.size)
    if (count == 0) return
    val xValues = data.xValues.take(count)
    val yValues = data.yValues.take(count)
    val xLabels = xValues.map { it.toChartLabel() }
    val nonNegativeY = yValues.map { it.coerceAtLeast(0f) }
    val positiveTotal = nonNegativeY.sum().coerceAtLeast(1f)
    val sortedY = yValues.sorted()

    when (data.chartId) {
        "bar" -> MicroBondStatHistogramBubble(
            meta = meta,
            data = StatHistogramData(
                title = data.title,
                subtitle = data.subtitle,
                bars = yValues.mapIndexed { index, value ->
                    HistogramBar(
                        label = xLabels[index],
                        value = value,
                        highlight = index == count - 1,
                        colorValue = if (index % 2 == 0) data.primaryColorValue else data.secondaryColorValue
                    )
                },
                highlightColorValue = data.primaryColorValue,
                totalLabel = "Total ${data.yLabel}",
                totalValue = yValues.sum().toChartLabel(),
                highestLabel = "Highest ${data.xLabel}",
                highestValue = xLabels[yValues.indices.maxBy { yValues[it] }],
                avgLabel = "Average ${data.yLabel}",
                avgValue = (yValues.average().toFloat()).toChartLabel(),
                footerText = "${count} ${data.xLabel.lowercase()} points"
            ),
            modifier = modifier
        )
        "line" -> MicroBondStatGroupProgressBubble(
            meta = meta,
            data = StatGroupProgressData(
                title = data.title,
                subtitle = data.subtitle,
                chartPoints = yValues.mapIndexed { index, value ->
                    val minValue = yValues.min()
                    val range = (yValues.max() - minValue).coerceAtLeast(1f)
                    StatChartPoint(xLabels[index], ((value - minValue) / range) * 100f)
                },
                chartColorValue = data.primaryColorValue,
                onTimePayoutsLabel = "First ${data.yLabel}",
                onTimePayoutsValue = yValues.first().toChartLabel(),
                activeMembersLabel = "Latest ${data.yLabel}",
                activeMembersValue = yValues.last().toChartLabel(),
                avgWaitLabel = "Average ${data.yLabel}",
                avgWaitValue = yValues.average().toFloat().toChartLabel()
            ),
            modifier = modifier
        )
        "area" -> MicroBondStatAreaBubble(
            meta = meta,
            data = StatAreaData(
                title = data.title,
                subtitle = data.subtitle,
                xLabels = xLabels,
                layers = listOf(AreaChartLayer(data.yLabel, data.primaryColorValue, nonNegativeY)),
                totalLabel = "Total ${data.yLabel}",
                totalValue = nonNegativeY.sum().toChartLabel(),
                footerText = "${count} ${data.xLabel.lowercase()} points"
            ),
            modifier = modifier
        )
        "scatter" -> MicroBondStatScatterBubble(
            meta = meta,
            data = StatScatterData(
                title = data.title,
                subtitle = data.subtitle,
                points = xValues.indices.map { index ->
                    ScatterPoint(xValues[index], yValues[index], xLabels[index])
                },
                primaryColorValue = data.primaryColorValue,
                secondaryColorValue = data.secondaryColorValue,
                xAxisLabel = data.xLabel,
                yAxisLabel = data.yLabel,
                correlationLabel = "Data points",
                correlationValue = count.toString()
            ),
            modifier = modifier
        )
        "waterfall" -> MicroBondStatWaterfallBubble(
            meta = meta,
            data = StatWaterfallData(
                title = data.title,
                subtitle = data.subtitle,
                bars = yValues.mapIndexed { index, value ->
                    WaterfallBar(xLabels[index], value)
                },
                gainColorValue = data.primaryColorValue,
                lossColorValue = data.secondaryColorValue,
                netLabel = "Net ${data.yLabel}",
                netValue = yValues.sum().let { total ->
                    val formatted = total.toChartLabel()
                    if (total > 0f) "+$formatted" else formatted
                },
                footerText = "${count} ${data.xLabel.lowercase()} points"
            ),
            modifier = modifier
        )
        "boxplot" -> MicroBondStatBoxPlotBubble(
            meta = meta,
            data = StatBoxPlotData(
                title = data.title,
                subtitle = data.subtitle,
                entries = listOf(
                    BoxPlotEntry(
                        label = data.yLabel,
                        min = sortedY.first(),
                        q1 = sortedY[(sortedY.lastIndex * 0.25f).toInt()],
                        median = sortedY[sortedY.lastIndex / 2],
                        q3 = sortedY[(sortedY.lastIndex * 0.75f).toInt()],
                        max = sortedY.last(),
                        colorValue = data.primaryColorValue
                    )
                ),
                footerText = "Distribution across $count ${data.xLabel.lowercase()} points"
            ),
            modifier = modifier
        )
        "heatmap" -> MicroBondStatHeatmapBubble(
            meta = meta,
            data = StatHeatmapData(
                title = data.title,
                subtitle = data.subtitle,
                rowLabels = xLabels,
                colLabels = listOf(data.yLabel),
                cells = yValues.mapIndexed { index, value ->
                    HeatmapCell(
                        rowLabel = xLabels[index],
                        colLabel = data.yLabel,
                        value = if (yValues.max() > 0f) (value / yValues.max()).coerceIn(0f, 1f) else 0f,
                        displayText = value.toChartLabel()
                    )
                },
                lowColorValue = data.secondaryColorValue,
                highColorValue = data.primaryColorValue,
                footerText = "${data.yLabel} by ${data.xLabel}"
            ),
            modifier = modifier
        )
        "donut" -> MicroBondStatGroupSnapshotBubble(
            meta = meta,
            data = StatGroupSnapshotData(
                title = data.title,
                subtitle = data.subtitle,
                slices = nonNegativeY.mapIndexed { index, value ->
                    StatDonutSlice(
                        label = xLabels[index],
                        percentage = (value / positiveTotal * 100f).toInt(),
                        colorValue = if (index % 2 == 0) data.primaryColorValue else data.secondaryColorValue
                    )
                },
                membersLabel = "Points",
                membersValue = count.toString(),
                collectedLabel = "Total ${data.yLabel}",
                collectedValue = yValues.sum().toChartLabel(),
                nextPayoutLabel = "Latest ${data.xLabel}",
                nextPayoutValue = xLabels.last()
            ),
            modifier = modifier
        )
        "radar" -> MicroBondGroupHealthBubble(
            meta = meta,
            data = GroupHealthData(
                title = data.title,
                subtitle = data.subtitle,
                radarPoints = nonNegativeY.mapIndexed { index, value ->
                    RadarChartPoint(
                        xLabels[index],
                        if (nonNegativeY.max() > 0f) value / nonNegativeY.max() else 0f
                    )
                },
                chartColorValue = data.primaryColorValue,
                strongestText = data.yLabel,
                focusNextText = data.xLabel,
                footerText = "$count data points"
            ),
            modifier = modifier
        )
        "candlestick" -> MicroBondStatCandlestickBubble(
            meta = meta,
            data = StatCandlestickData(
                title = data.title,
                subtitle = data.subtitle,
                candles = yValues.mapIndexed { index, close ->
                    val open = yValues.getOrElse(index - 1) { close }
                    CandlestickBar(
                        label = xLabels[index],
                        open = open,
                        high = maxOf(open, close),
                        low = minOf(open, close),
                        close = close
                    )
                },
                bullishColorValue = data.primaryColorValue,
                bearishColorValue = data.secondaryColorValue,
                highValue = yValues.max().toChartLabel(),
                lowValue = yValues.min().toChartLabel(),
                footerText = "${count} ${data.xLabel.lowercase()} points"
            ),
            modifier = modifier
        )
        else -> MicroBondStatHistogramBubble(
            meta = meta,
            data = StatHistogramData(
                title = data.title,
                subtitle = data.subtitle,
                bars = yValues.mapIndexed { index, value ->
                    HistogramBar(
                        label = xLabels[index],
                        value = value,
                        highlight = index == count - 1,
                        colorValue = if (index % 2 == 0) data.primaryColorValue else data.secondaryColorValue
                    )
                },
                highlightColorValue = data.primaryColorValue
            ),
            modifier = modifier
        )
    }
}

private fun Float.toChartLabel(): String = if (this % 1f == 0f) toInt().toString() else "%.1f".format(this)

@Composable
fun StatisticsChartPlot(
    chartId: String,
    xData: List<Float>,
    yData: List<Float>,
    primaryColor: Color,
    secondaryColor: Color,
    xLabel: String,
    yLabel: String,
    modifier: Modifier = Modifier
) {
    val gridColor = Color.White.copy(alpha = 0.06f)
    val count = minOf(xData.size, yData.size)
    if (count == 0) return

    val yMax = yData.take(count).max().coerceAtLeast(1f)
    val xMax = xData.take(count).max().coerceAtLeast(1f)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val padL = 40f
            val padB = 30f
            val chartW = w - padL - 16f
            val chartH = h - padB - 16f

            for (i in 0..4) {
                val y = 16f + chartH * (1f - i / 4f)
                drawLine(gridColor, Offset(padL, y), Offset(w - 16f, y), 1f)
            }

            when (chartId) {
                "bar" -> {
                    val barW = chartW / count * 0.65f
                    val gap = chartW / count
                    for (i in 0 until count) {
                        val barH = chartH * (yData[i] / yMax)
                        val x = padL + i * gap + gap * 0.17f
                        drawRect(
                            primaryColor.copy(alpha = 0.85f),
                            topLeft = Offset(x, 16f + chartH - barH),
                            size = Size(barW, barH)
                        )
                    }
                }
                "line" -> {
                    val points = (0 until count).map { i ->
                        Offset(
                            padL + i * (chartW / (count - 1).coerceAtLeast(1)),
                            16f + chartH - chartH * (yData[i] / yMax)
                        )
                    }
                    for (i in 0 until points.size - 1) {
                        drawLine(primaryColor, points[i], points[i + 1], 3f)
                    }
                    points.forEach { drawCircle(primaryColor, 5f, it) }
                }
                "area" -> {
                    val points = (0 until count).map { i ->
                        Offset(
                            padL + i * (chartW / (count - 1).coerceAtLeast(1)),
                            16f + chartH - chartH * (yData[i] / yMax)
                        )
                    }
                    val path = Path().apply {
                        moveTo(points.first().x, 16f + chartH)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, 16f + chartH)
                        close()
                    }
                    drawPath(
                        path,
                        Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.6f),
                                primaryColor.copy(alpha = 0.05f)
                            ),
                            startY = 16f,
                            endY = 16f + chartH
                        )
                    )
                    for (i in 0 until points.size - 1) {
                        drawLine(primaryColor, points[i], points[i + 1], 2.5f)
                    }
                }
                "scatter" -> {
                    for (i in 0 until count) {
                        val cx = padL + (xData[i] / xMax) * chartW
                        val cy = 16f + chartH - (yData[i] / yMax) * chartH
                        drawCircle(primaryColor.copy(alpha = 0.7f), 10f, Offset(cx, cy))
                        drawCircle(primaryColor, 4f, Offset(cx, cy))
                    }
                }
                "donut" -> {
                    val total = yData.take(count).sum().coerceAtLeast(1f)
                    val cx = padL + chartW / 2f
                    val cy = 16f + chartH / 2f
                    val radius = minOf(chartW, chartH) / 2.5f
                    var startAngle = -90f
                    yData.take(count).forEachIndexed { i, value ->
                        val sweep = 360f * value / total
                        val color = if (i % 2 == 0) primaryColor else secondaryColor
                        drawArc(
                            color,
                            startAngle,
                            sweep,
                            false,
                            topLeft = Offset(cx - radius, cy - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = radius * 0.45f)
                        )
                        startAngle += sweep
                    }
                }
                "heatmap" -> {
                    val cols = 4
                    val rows = (count + cols - 1) / cols
                    val cellW = chartW / cols
                    val cellH = chartH / rows
                    for (i in 0 until count) {
                        val col = i % cols
                        val row = i / cols
                        val alpha = (yData[i] / yMax).coerceIn(0.1f, 1f)
                        drawRect(
                            primaryColor.copy(alpha = alpha),
                            topLeft = Offset(padL + col * cellW + 2f, 16f + row * cellH + 2f),
                            size = Size(cellW - 4f, cellH - 4f)
                        )
                    }
                }
                else -> {
                    val barW = chartW / count * 0.65f
                    val gap = chartW / count
                    var running = 0f
                    for (i in 0 until count) {
                        val barH = chartH * (yData[i] / yMax)
                        val x = padL + i * gap + gap * 0.17f
                        val base = if (chartId == "waterfall") running else 0f
                        val color = if (yData[i] >= 0) primaryColor else secondaryColor
                        drawRect(
                            color.copy(alpha = 0.85f),
                            topLeft = Offset(x, 16f + chartH - base - barH),
                            size = Size(barW, barH)
                        )
                        running += yData[i] / yMax * (chartH / 4f)
                    }
                }
            }

            drawLine(Color.White.copy(alpha = 0.15f), Offset(padL, 16f), Offset(padL, 16f + chartH), 1f)
            drawLine(
                Color.White.copy(alpha = 0.15f),
                Offset(padL, 16f + chartH),
                Offset(w - 16f, 16f + chartH),
                1f
            )
        }
        Text(
            yLabel,
            color = MutedText,
            fontSize = 9.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 2.dp, top = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            xLabel,
            color = MutedText,
            fontSize = 9.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 2.dp, end = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val MutedText = Color(0xFF8696A0)
