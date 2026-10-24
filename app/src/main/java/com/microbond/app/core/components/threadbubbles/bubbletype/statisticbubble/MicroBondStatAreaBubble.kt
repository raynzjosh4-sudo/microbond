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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.BubbleAuthorType
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.AreaChartLayer
import com.microbond.app.core.data_classes_and_enums.StatAreaData

private val MutedText = Color(0xFF8696A0)
private val GridLine  = Color.White.copy(alpha = 0.07f)

/**
 * MicroBondStatAreaBubble
 *
 * Stacked area chart. Each layer (e.g. Savings, Interest, Loans) is drawn
 * bottom-up with a translucent fill. Perfect for showing how multiple
 * contribution streams accumulate over time.
 */
@Composable
fun MicroBondStatAreaBubble(
    meta: MicroBondMeta,
    data: StatAreaData,
    modifier: Modifier = Modifier
) {
    val chartColor = data.layers.firstOrNull()?.colorValue?.let { Color(it.toInt()) } ?: Color(0xFF60A5FA)
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
                    .background(chartColor)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, null, tint = chartColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text(data.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(data.subtitle, color = MutedText, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                val textMeasurer = rememberTextMeasurer()
                Canvas(
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111B21))
                ) {
                    if (data.layers.isNotEmpty() && data.xLabels.isNotEmpty())
                        drawStackedAreaChart(data.layers, data.xLabels, textMeasurer)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GridLine)
                Spacer(Modifier.height(8.dp))
                // Legend
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    data.layers.forEach { layer ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(layer.colorValue.toInt())))
                            Spacer(Modifier.width(4.dp))
                            Text(layer.label, color = MutedText, fontSize = 10.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(data.totalLabel, color = MutedText, fontSize = 10.sp)
                        Text(data.totalValue, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(data.footerText, color = MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStackedAreaChart(
    layers: List<AreaChartLayer>,
    xLabels: List<String>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val labelH  = 16f
    val bot     = size.height - labelH - 4f
    val top     = 0f
    val chartH  = bot - top
    val n       = xLabels.size.coerceAtLeast(2)
    val stepX   = size.width / (n - 1).toFloat()

    // Compute true max of stacked values
    val cumulative = FloatArray(n) { 0f }
    val maxStack = layers.fold(FloatArray(n) { 0f }) { acc, layer ->
        layer.values.take(n).forEachIndexed { i, v -> acc[i] += v.coerceAtLeast(0f) }
        acc
    }.maxOrNull() ?: 1f

    for (i in 0..2) {
        val y = top + chartH * (i / 2f)
        drawLine(GridLine, Offset(0f, y), Offset(size.width, y), 1f)
    }

    fun toY(v: Float) = bot - (v / maxStack) * chartH

    val base = FloatArray(n) { 0f }
    layers.forEach { layer ->
        val vals    = layer.values.take(n).map { it.coerceAtLeast(0f) }
        val topVals = vals.mapIndexed { i, v -> base[i] + v }
        val clr     = Color(layer.colorValue.toInt())

        val fillPath = Path().apply {
            moveTo(0f, bot)
            topVals.forEachIndexed { i, v -> lineTo(i * stepX, toY(v)) }
            lineTo((n - 1) * stepX, bot)
            close()
        }
        drawPath(fillPath, Brush.verticalGradient(
            listOf(clr.copy(alpha = 0.55f), clr.copy(alpha = 0.12f)),
            startY = 0f, endY = bot
        ))

        val strokePath = Path().apply {
            topVals.forEachIndexed { i, v ->
                if (i == 0) moveTo(i * stepX, toY(v)) else lineTo(i * stepX, toY(v))
            }
        }
        drawPath(strokePath, clr, style = Stroke(width = 2f))

        topVals.forEachIndexed { i, v -> base[i] = v }
    }

    xLabels.take(n).forEachIndexed { i, lbl ->
        val lm = textMeasurer.measure(lbl, style = TextStyle(color = MutedText, fontSize = 8.sp))
        drawText(lm, topLeft = Offset(i * stepX - lm.size.width / 2f, bot + 4f))
    }
}
