package com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.widgets

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
import com.microbond.app.core.ui.theme.MicrobondInactiveGray

@Composable
fun ChartPreviewPlot(
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

    val yMax = yData.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    val xMax = xData.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val padL = 40f
            val padB = 30f
            val chartW = w - padL - 16f
            val chartH = h - padB - 16f

            // Grid lines
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
                    val pts = (0 until count).map { i ->
                        Offset(
                            padL + i * (chartW / (count - 1).coerceAtLeast(1)),
                            16f + chartH - chartH * (yData[i] / yMax)
                        )
                    }
                    for (i in 0 until pts.size - 1) {
                        drawLine(primaryColor, pts[i], pts[i + 1], 3f)
                    }
                    pts.forEach { drawCircle(primaryColor, 5f, it) }
                }
                "area" -> {
                    val pts = (0 until count).map { i ->
                        Offset(
                            padL + i * (chartW / (count - 1).coerceAtLeast(1)),
                            16f + chartH - chartH * (yData[i] / yMax)
                        )
                    }
                    val path = Path().apply {
                        moveTo(pts.first().x, 16f + chartH)
                        pts.forEach { lineTo(it.x, it.y) }
                        lineTo(pts.last().x, 16f + chartH)
                        close()
                    }
                    drawPath(
                        path,
                        Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.6f), primaryColor.copy(alpha = 0.05f)),
                            startY = 16f,
                            endY = 16f + chartH
                        )
                    )
                    for (i in 0 until pts.size - 1) drawLine(primaryColor, pts[i], pts[i + 1], 2.5f)
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
                    val total = yData.sum().coerceAtLeast(1f)
                    val cx = padL + chartW / 2f
                    val cy = 16f + chartH / 2f
                    val radius = minOf(chartW, chartH) / 2.5f
                    var startAngle = -90f
                    yData.forEachIndexed { i, v ->
                        val sweep = 360f * v / total
                        val col = if (i % 2 == 0) primaryColor else secondaryColor
                        drawArc(
                            col, startAngle, sweep, false,
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
                    // Waterfall / boxplot / others fallback
                    val barW = chartW / count * 0.65f
                    val gap = chartW / count
                    var running = 0f
                    for (i in 0 until count) {
                        val barH = chartH * (yData[i] / yMax)
                        val x = padL + i * gap + gap * 0.17f
                        val base = if (chartId == "waterfall") running else 0f
                        val col = if (yData[i] >= 0) primaryColor else secondaryColor
                        drawRect(
                            col.copy(alpha = 0.85f),
                            topLeft = Offset(x, 16f + chartH - base - barH),
                            size = Size(barW, barH)
                        )
                        running += yData[i] / yMax * (chartH / 4f)
                    }
                }
            }

            // Axis lines
            drawLine(Color.White.copy(0.15f), Offset(padL, 16f), Offset(padL, 16f + chartH), 1f)
            drawLine(Color.White.copy(0.15f), Offset(padL, 16f + chartH), Offset(w - 16f, 16f + chartH), 1f)
        }

        // Y / X labels
        Text(
            yLabel,
            color = MicrobondInactiveGray,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 2.dp, top = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            xLabel,
            color = MicrobondInactiveGray,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 2.dp, end = 2.dp),
            maxLines = 1
        )
    }
}
