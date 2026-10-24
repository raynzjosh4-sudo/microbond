package com.microbond.app.core.components.threadbubbles.bubbletype.statisticbubble

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.BubbleAuthorType
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.ScatterPoint
import com.microbond.app.core.data_classes_and_enums.StatScatterData

private val MutedText  = Color(0xFF8696A0)
private val GridLine   = Color.White.copy(alpha = 0.07f)
private val DotColor   = Color(0xFF818CF8)
private val TrendColor = Color(0xFF60A5FA).copy(alpha = 0.6f)

/**
 * MicroBondStatScatterBubble
 *
 * Scatter plot with a linear-regression trend line. Reveals correlation
 * between two financial variables, e.g. deposit frequency vs. on-time rate.
 */
@Composable
fun MicroBondStatScatterBubble(
    meta: MicroBondMeta,
    data: StatScatterData,
    modifier: Modifier = Modifier
) {
    val dotColor = data.primaryColorValue?.let { Color(it.toInt()) } ?: DotColor
    val trendColor = data.secondaryColorValue?.let { Color(it.toInt()) } ?: TrendColor
    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = BubbleAuthorType.USER,
        showTimestamp = false
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(3.dp).fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                    .background(dotColor)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BubbleChart, null, tint = dotColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text(data.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(data.subtitle, color = MutedText, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                val textMeasurer = rememberTextMeasurer()
                Canvas(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111B21))
                ) {
                    if (data.points.isNotEmpty())
                        drawScatterChart(data.points, data.xAxisLabel, textMeasurer, dotColor, trendColor)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GridLine)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(data.correlationLabel, color = MutedText, fontSize = 10.sp)
                        Text(data.correlationValue, color = dotColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.width(18.dp).height(2.dp).background(trendColor))
                        Text("Trend", color = MutedText, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawScatterChart(
    points: List<ScatterPoint>,
    xLabel: String,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    dotColor: Color,
    trendColor: Color
) {
    val axisOff  = 32f
    val labelH   = 16f
    val plotL    = axisOff
    val plotR    = size.width
    val plotTop  = 0f
    val plotBot  = size.height - labelH - 4f
    val plotW    = plotR - plotL
    val plotH    = plotBot - plotTop

    val xMin = points.minOf { it.x }
    val xMax = points.maxOf { it.x }.coerceAtLeast(xMin + 1f)
    val yMin = points.minOf { it.y }
    val yMax = points.maxOf { it.y }.coerceAtLeast(yMin + 1f)

    fun toX(v: Float) = plotL + ((v - xMin) / (xMax - xMin)) * plotW
    fun toY(v: Float) = plotBot - ((v - yMin) / (yMax - yMin)) * plotH

    // Grid
    for (i in 0..3) {
        val y = plotTop + plotH * (i / 3f)
        drawLine(GridLine, Offset(plotL, y), Offset(plotR, y), 1f)
        val x = plotL + plotW * (i / 3f)
        drawLine(GridLine, Offset(x, plotTop), Offset(x, plotBot), 1f)
    }

    // Axes
    drawLine(MutedText.copy(alpha = 0.3f), Offset(plotL, plotTop), Offset(plotL, plotBot), 1.5f)
    drawLine(MutedText.copy(alpha = 0.3f), Offset(plotL, plotBot), Offset(plotR, plotBot), 1.5f)

    // Regression trend line
    if (points.size >= 2) {
        val n   = points.size.toFloat()
        val sx  = points.sumOf { it.x.toDouble() }.toFloat()
        val sy  = points.sumOf { it.y.toDouble() }.toFloat()
        val sxy = points.sumOf { (it.x * it.y).toDouble() }.toFloat()
        val sx2 = points.sumOf { (it.x * it.x).toDouble() }.toFloat()
        val den = n * sx2 - sx * sx
        if (den != 0f) {
            val slope = (n * sxy - sx * sy) / den
            val intercept = (sy - slope * sx) / n
            drawLine(
                trendColor,
                Offset(toX(xMin), toY(slope * xMin + intercept)),
                Offset(toX(xMax), toY(slope * xMax + intercept)),
                2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f))
            )
        }
    }

    // Points
    points.forEach { pt ->
        drawCircle(dotColor.copy(alpha = 0.9f), 5f, Offset(toX(pt.x), toY(pt.y)))
        drawCircle(Color.Black.copy(alpha = 0.4f), 2.5f, Offset(toX(pt.x), toY(pt.y)))
    }

    // X label
    val lm = textMeasurer.measure(xLabel, style = TextStyle(color = MutedText, fontSize = 8.sp))
    drawText(lm, topLeft = Offset(plotL + plotW / 2f - lm.size.width / 2f, plotBot + 4f))
}
