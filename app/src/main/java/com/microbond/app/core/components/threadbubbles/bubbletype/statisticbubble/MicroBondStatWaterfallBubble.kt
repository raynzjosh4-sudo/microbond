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
import androidx.compose.material.icons.filled.CompareArrows
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.BubbleAuthorType
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.StatWaterfallData
import com.microbond.app.core.data_classes_and_enums.WaterfallBar

private val MutedText   = Color(0xFF8696A0)
private val GridLine    = Color.White.copy(alpha = 0.07f)
private val GainColor   = Color(0xFF4ADE80)
private val LossColor   = Color(0xFFFF6B6B)
private val TotalColor  = Color(0xFF60A5FA)
private val ConnectLine = Color.White.copy(alpha = 0.25f)

/**
 * MicroBondStatWaterfallBubble
 *
 * Waterfall (bridge) chart for cash-flow analysis. Shows how individual inflows
 * (deposits, interest) and outflows (payouts, fees) lead to a net position.
 * Green = gain, Red = loss, Blue = running total.
 */
@Composable
fun MicroBondStatWaterfallBubble(
    meta: MicroBondMeta,
    data: StatWaterfallData,
    modifier: Modifier = Modifier
) {
    val gainColor = data.gainColorValue?.let { Color(it.toInt()) } ?: GainColor
    val lossColor = data.lossColorValue?.let { Color(it.toInt()) } ?: LossColor
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
                    .background(gainColor)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CompareArrows, null, tint = gainColor, modifier = Modifier.size(16.dp))
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
                    if (data.bars.isNotEmpty()) drawWaterfallChart(data.bars, textMeasurer, gainColor, lossColor)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GridLine)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    LegendDot("Gain", gainColor)
                    LegendDot("Loss", lossColor)
                    LegendDot("Total", TotalColor)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(data.netLabel, color = MutedText, fontSize = 10.sp)
                        val isPositive = data.netValue.startsWith("+")
                        Text(data.netValue, color = if (isPositive) gainColor else lossColor,
                            fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(data.footerText, color = MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWaterfallChart(
    bars: List<WaterfallBar>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    gainColor: Color,
    lossColor: Color
) {
    val labelH   = 18f
    val chartBot = size.height - labelH - 4f
    val chartTop = 8f
    val chartH   = chartBot - chartTop
    val n        = bars.size
    val slotW    = size.width / n
    val barW     = (slotW * 0.6f).coerceAtLeast(6f)

    var running = 0f
    val baseLines = mutableListOf<Float>()
    val topLines  = mutableListOf<Float>()
    bars.forEach { bar ->
        if (bar.isTotal) {
            baseLines.add(0f); topLines.add(running)
        } else {
            if (bar.value >= 0) { baseLines.add(running); topLines.add(running + bar.value) }
            else { baseLines.add(running + bar.value); topLines.add(running) }
            running += bar.value
        }
    }

    val allV  = baseLines + topLines
    val minV  = allV.minOrNull() ?: 0f
    val maxV  = allV.maxOrNull() ?: 1f
    val range = (maxV - minV).coerceAtLeast(1f)
    fun toY(v: Float) = chartBot - ((v - minV) / range) * chartH

    for (i in 0..3) {
        val y = chartTop + chartH * (i / 3f)
        drawLine(GridLine, Offset(0f, y), Offset(size.width, y), 1f)
    }

    bars.forEachIndexed { idx, bar ->
        val cx     = slotW * idx + slotW / 2f
        val bClr   = when { bar.isTotal -> TotalColor; bar.value >= 0 -> gainColor; else -> lossColor }
        val bY     = toY(topLines[idx])
        val tY     = toY(baseLines[idx])
        val barH   = (tY - bY).coerceAtLeast(3f)
        drawRoundRect(bClr.copy(alpha = 0.85f), Offset(cx - barW / 2f, bY), Size(barW, barH), CornerRadius(3f, 3f))
        if (idx < n - 1 && !bar.isTotal) {
            val nCx = slotW * (idx + 1) + slotW / 2f
            val cY  = toY(topLines[idx])
            drawLine(ConnectLine, Offset(cx + barW / 2f, cY), Offset(nCx - barW / 2f, cY), 1f)
        }
        val lm = textMeasurer.measure(bar.label, style = TextStyle(color = MutedText, fontSize = 8.sp))
        drawText(lm, topLeft = Offset(cx - lm.size.width / 2f, chartBot + 4f))
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, color = MutedText, fontSize = 10.sp)
    }
}
