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
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import com.microbond.app.core.data_classes_and_enums.CandlestickBar
import com.microbond.app.core.data_classes_and_enums.StatCandlestickData

private val MutedText    = Color(0xFF8696A0)
private val GridLine     = Color.White.copy(alpha = 0.07f)
private val BullishColor = Color(0xFF4ADE80)
private val BearishColor = Color(0xFFFF6B6B)
private val WickColor    = Color(0xFF8696A0)

/**
 * MicroBondStatCandlestickBubble
 *
 * OHLC candlestick chart. Green = close > open (gain). Red = close < open (loss).
 * Ideal for tracking savings-cycle performance or contribution trends over periods.
 */
@Composable
fun MicroBondStatCandlestickBubble(
    meta: MicroBondMeta,
    data: StatCandlestickData,
    modifier: Modifier = Modifier
) {
    val bullishColor = data.bullishColorValue?.let { Color(it.toInt()) } ?: BullishColor
    val bearishColor = data.bearishColorValue?.let { Color(it.toInt()) } ?: BearishColor
    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = BubbleAuthorType.USER,
        showTimestamp = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                    .background(Color(0xFFFFD166))
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShowChart, null, tint = Color(0xFFFFD166), modifier = Modifier.size(16.dp))
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
                    if (data.candles.isNotEmpty()) {
                        drawCandlestickChart(data.candles, textMeasurer, bullishColor, bearishColor)
                    }
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GridLine)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CandleStat(data.highLabel, data.highValue, bullishColor)
                    CandleStat(data.lowLabel, data.lowValue, bearishColor)
                }
                Spacer(Modifier.height(6.dp))
                Text(data.footerText, color = MutedText, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

private fun DrawScope.drawCandlestickChart(
    candles: List<CandlestickBar>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    bullishColor: Color,
    bearishColor: Color
) {
    val labelH    = 16f
    val chartTop  = 0f
    val chartBot  = size.height - labelH - 4f
    val chartH    = chartBot - chartTop
    val n         = candles.size
    val slotW     = size.width / n
    val allV      = candles.flatMap { listOf(it.high, it.low) }
    val minV      = allV.minOrNull() ?: 0f
    val maxV      = allV.maxOrNull() ?: 1f
    val range     = (maxV - minV).coerceAtLeast(1f)
    fun toY(v: Float) = chartBot - ((v - minV) / range) * chartH

    for (i in 0..2) {
        val y = chartTop + chartH * (i / 2f)
        drawLine(GridLine, Offset(0f, y), Offset(size.width, y), 1f)
    }
    candles.forEachIndexed { idx, c ->
        val cx     = slotW * idx + slotW / 2f
        val candleW = (slotW * 0.55f).coerceAtLeast(4f)
        val color  = if (c.close >= c.open) bullishColor else bearishColor
        val topBody = toY(maxOf(c.open, c.close))
        val botBody = toY(minOf(c.open, c.close))
        val bodyH   = (botBody - topBody).coerceAtLeast(2f)
        drawLine(WickColor, Offset(cx, toY(c.high)), Offset(cx, toY(c.low)), 1.5f, cap = StrokeCap.Round)
        drawRoundRect(color, Offset(cx - candleW / 2f, topBody), Size(candleW, bodyH), CornerRadius(2f, 2f))
        val lm = textMeasurer.measure(c.label, style = TextStyle(color = MutedText, fontSize = 8.sp))
        drawText(lm, topLeft = Offset(cx - lm.size.width / 2f, chartBot + 4f))
    }
}

@Composable
private fun CandleStat(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = MutedText, fontSize = 10.sp)
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
