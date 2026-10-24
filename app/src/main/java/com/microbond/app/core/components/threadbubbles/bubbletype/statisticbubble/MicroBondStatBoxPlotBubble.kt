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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.microbond.app.core.data_classes_and_enums.BoxPlotEntry
import com.microbond.app.core.data_classes_and_enums.StatBoxPlotData

private val MutedText  = Color(0xFF8696A0)
private val GridLine   = Color.White.copy(alpha = 0.07f)
private val WhiskerClr = Color.White.copy(alpha = 0.5f)
private val MedianClr  = Color.White

/**
 * MicroBondStatBoxPlotBubble
 *
 * Box-and-whisker chart. Displays the 5-number summary (min, Q1, median, Q3, max)
 * per member or time period. Great for comparing deposit spread and risk profiles.
 */
@Composable
fun MicroBondStatBoxPlotBubble(
    meta: MicroBondMeta,
    data: StatBoxPlotData,
    modifier: Modifier = Modifier
) {
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
                    .background(Color(0xFFA78BFA))
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Analytics, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text(data.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(data.subtitle, color = MutedText, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                val textMeasurer = rememberTextMeasurer()
                Canvas(
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111B21))
                ) {
                    if (data.entries.isNotEmpty()) drawBoxPlot(data.entries, textMeasurer)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GridLine)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BoxLegendItem("Box = IQR", Color(0xFFA78BFA).copy(alpha = 0.5f))
                    BoxLegendItem("Line = Median", MedianClr)
                    BoxLegendItem("Whiskers = Range", WhiskerClr)
                }
                Spacer(Modifier.height(6.dp))
                Text(data.footerText, color = MutedText, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

private fun DrawScope.drawBoxPlot(
    entries: List<BoxPlotEntry>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val labelH = 18f
    val bot    = size.height - labelH - 4f
    val top    = 8f
    val chartH = bot - top
    val n      = entries.size
    val slotW  = size.width / n
    val boxW   = (slotW * 0.45f).coerceAtLeast(8f)

    val minV  = entries.minOf { it.min }
    val maxV  = entries.maxOf { it.max }
    val range = (maxV - minV).coerceAtLeast(1f)
    fun toY(v: Float) = bot - ((v - minV) / range) * chartH

    for (i in 0..3) {
        val y = top + chartH * (i / 3f)
        drawLine(GridLine, Offset(0f, y), Offset(size.width, y), 1f)
    }

    entries.forEachIndexed { idx, entry ->
        val cx      = slotW * idx + slotW / 2f
        val color   = Color(entry.colorValue.toInt())
        val yMin    = toY(entry.min)
        val yQ1     = toY(entry.q1)
        val yMed    = toY(entry.median)
        val yQ3     = toY(entry.q3)
        val yMax    = toY(entry.max)

        // Lower whisker
        drawLine(WhiskerClr, Offset(cx, yMin), Offset(cx, yQ1), 1.5f, cap = StrokeCap.Round)
        drawLine(WhiskerClr, Offset(cx - boxW / 3f, yMin), Offset(cx + boxW / 3f, yMin), 1.5f)

        // Box fill (Q1→Q3)
        val bTop = yQ3; val bH = (yQ1 - yQ3).coerceAtLeast(2f)
        drawRect(color.copy(alpha = 0.35f), Offset(cx - boxW / 2f, bTop), Size(boxW, bH))
        // Box outline
        drawRect(
            color.copy(alpha = 0.8f),
            Offset(cx - boxW / 2f, bTop),
            Size(boxW, bH),
            style = Stroke(width = 1.5f)
        )

        // Median line
        drawLine(MedianClr, Offset(cx - boxW / 2f, yMed), Offset(cx + boxW / 2f, yMed), 2f)

        // Upper whisker
        drawLine(WhiskerClr, Offset(cx, yQ3), Offset(cx, yMax), 1.5f, cap = StrokeCap.Round)
        drawLine(WhiskerClr, Offset(cx - boxW / 3f, yMax), Offset(cx + boxW / 3f, yMax), 1.5f)

        val lm = textMeasurer.measure(entry.label, style = TextStyle(color = MutedText, fontSize = 8.sp))
        drawText(lm, topLeft = Offset(cx - lm.size.width / 2f, bot + 4f))
    }
}

@Composable
private fun BoxLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(width = 14.dp, height = 3.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, color = MutedText, fontSize = 9.sp)
    }
}
