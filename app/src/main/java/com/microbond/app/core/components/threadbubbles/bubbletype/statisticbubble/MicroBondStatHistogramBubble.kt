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
import androidx.compose.material.icons.outlined.Check
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
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.StatHistogramData
import kotlin.math.absoluteValue

private val MutedText = Color(0xFF8696A0)
private val ChartGridLine = Color.White.copy(alpha = 0.05f)

@Composable
fun MicroBondStatHistogramBubble(
    meta: MicroBondMeta,
    data: StatHistogramData,
    modifier: Modifier = Modifier
) {
    val highlightBarColor = Color(data.highlightColorValue.toInt())
    _root_ide_package_.com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        authorType = _root_ide_package_.com.microbond.app.core.components.threadbubbles.BubbleAuthorType.USER,
        showTimestamp = false
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Sender-colored left accent line
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                    .background(meta.senderColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp)
            ) {
                // Header
                Text(
                    text = data.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = data.subtitle,
                    color = MutedText,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bar Chart Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111B21))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val textMeasurer = rememberTextMeasurer()

                    Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                        val width = size.width
                        val height = size.height

                        // Padding for axes
                        val paddingBottom = 24.dp.toPx()
                        val chartHeight = height - paddingBottom

                        // Draw grid lines
                        val yLevels = listOf(1f, 0.75f, 0.5f, 0.25f, 0f)
                        yLevels.forEach { level ->
                            val y = chartHeight * (1f - level)
                            drawLine(
                                color = ChartGridLine,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (data.bars.isNotEmpty()) {
                            val minValue = data.bars.minOfOrNull { it.value }?.coerceAtMost(0f) ?: 0f
                            val maxValue = data.bars.maxOfOrNull { it.value }?.coerceAtLeast(0f) ?: 1f
                            val valueRange = (maxValue - minValue).coerceAtLeast(1f)
                            val baselineY = chartHeight * (maxValue / valueRange)
                            val barsCount = data.bars.size
                            val barWidth = (width / barsCount) * 0.6f
                            val spacing = (width / barsCount)

                            data.bars.forEachIndexed { index, bar ->
                                val barHeightPx = (bar.value / valueRange).absoluteValue * chartHeight
                                val xOffset = (index * spacing) + (spacing - barWidth) / 2
                                val yOffset = if (bar.value >= 0f) {
                                    baselineY - barHeightPx
                                } else {
                                    baselineY
                                }

                                // Draw bar
                                drawRoundRect(
                                    color = bar.colorValue?.let { Color(it.toInt()) }
                                        ?: if (bar.highlight) highlightBarColor
                                        else highlightBarColor.copy(alpha = 0.45f),
                                    topLeft = Offset(xOffset, yOffset),
                                    size = Size(barWidth, barHeightPx),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                )

                                // Draw X-axis label
                                val style = TextStyle(color = MutedText, fontSize = 11.sp)
                                textMeasurer.measure(
                                    text = bar.label,
                                    style = style
                                ).also { layoutResult ->
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = bar.label,
                                        style = style,
                                        topLeft = Offset(
                                            xOffset + (barWidth / 2f) - (layoutResult.size.width / 2f),
                                            height - paddingBottom + 8.dp.toPx()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ChartGridLine)
                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Col 1
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.totalLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.totalValue,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(ChartGridLine))

                    // Col 2
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(
                            text = data.highestLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.highestValue,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(ChartGridLine))

                    // Col 3
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(
                            text = data.avgLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.avgValue,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.footerText,
                        color = MutedText,
                        fontSize = 11.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = meta.time,
                            color = MutedText,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Check, // Double check simulation
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
