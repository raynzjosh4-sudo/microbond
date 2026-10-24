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
import androidx.compose.material.icons.filled.GridOn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.BubbleAuthorType
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.StatHeatmapData

private val MutedText  = Color(0xFF8696A0)
private val GridLine   = Color.White.copy(alpha = 0.07f)
private val CellMissed = Color(0xFFFF6B6B)
private val CellPaid   = Color(0xFF4ADE80)
private val CellNone   = Color(0xFF1E3A30)

/**
 * MicroBondStatHeatmapBubble
 *
 * Color-intensity heatmap: members (rows) × months (columns).
 * Red = missed payment, Green = fully paid. Compact enough for
 * 10+ members across 6 months in one bubble.
 */
@Composable
fun MicroBondStatHeatmapBubble(
    meta: MicroBondMeta,
    data: StatHeatmapData,
    modifier: Modifier = Modifier
) {
    val lowColor = data.lowColorValue?.let { Color(it.toInt()) } ?: CellMissed
    val highColor = data.highColorValue?.let { Color(it.toInt()) } ?: CellPaid
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
                    .background(highColor)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GridOn, null, tint = highColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text(data.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(data.subtitle, color = MutedText, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))

                val rows = data.rowLabels.size.coerceAtLeast(1)
                val chartHeight = (rows * 22 + 20).dp

                val textMeasurer = rememberTextMeasurer()
                Canvas(modifier = Modifier.fillMaxWidth().height(chartHeight)) {
                    drawHeatmap(data, textMeasurer, lowColor, highColor)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GridLine)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(data.footerText, color = MutedText, fontSize = 10.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Low", color = lowColor, fontSize = 9.sp)
                        Box(
                            modifier = Modifier
                                .width(40.dp).height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Brush.horizontalGradient(listOf(lowColor, highColor)))
                        )
                        Text("High", color = highColor, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeatmap(
    data: StatHeatmapData,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    lowColor: Color,
    highColor: Color
) {
    val rowCount = data.rowLabels.size
    val colCount = data.colLabels.size
    if (rowCount == 0 || colCount == 0) return

    val rowLabelW = 52f
    val colLabelH = 16f
    val gap       = 2f
    val gridLeft  = rowLabelW
    val gridTop   = colLabelH
    val gridW     = size.width - rowLabelW
    val gridH     = size.height - colLabelH
    val cellW     = (gridW / colCount - gap).coerceAtLeast(4f)
    val cellH     = (gridH / rowCount - gap).coerceAtLeast(4f)

    // Column headers
    data.colLabels.forEachIndexed { cIdx, col ->
        val x  = gridLeft + cIdx * (cellW + gap) + cellW / 2f
        val lm = textMeasurer.measure(col, style = TextStyle(color = MutedText, fontSize = 7.sp))
        drawText(lm, topLeft = Offset(x - lm.size.width / 2f, 0f))
    }

    // Rows
    data.rowLabels.forEachIndexed { rIdx, row ->
        val y  = gridTop + rIdx * (cellH + gap)
        val rl = textMeasurer.measure(row, style = TextStyle(color = MutedText, fontSize = 7.sp))
        drawText(rl, topLeft = Offset(0f, y + cellH / 2f - rl.size.height / 2f))

        data.colLabels.forEachIndexed { cIdx, col ->
            val x     = gridLeft + cIdx * (cellW + gap)
            val cell  = data.cells.find { it.rowLabel == row && it.colLabel == col }
            val color = when {
                cell == null    -> CellNone
                cell.value <= 0 -> lowColor
                cell.value >= 1 -> highColor
                else            -> lerp(lowColor, highColor, cell.value)
            }
            drawRoundRect(color, Offset(x, y), Size(cellW, cellH), CornerRadius(3f, 3f))
        }
    }
}
