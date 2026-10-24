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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.data_classes_and_enums.StatGroupProgressData
import androidx.compose.ui.text.TextStyle
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta

private val MutedText = Color(0xFF8696A0)
private val ChartGridLine = Color.White.copy(alpha = 0.05f)
private val AccentGreen = Color(0xFF4ADE80) // Similar to screenshot
private val ChartDotBg = Color(0xFF1B2B33) // Match bubble background for dot center

@Composable
fun MicroBondStatGroupProgressBubble(
    meta: MicroBondMeta,
    data: StatGroupProgressData,
    modifier: Modifier = Modifier
) {
    val chartColor = data.chartColorValue?.let { Color(it.toInt()) } ?: AccentGreen
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
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                // Header
                Text(
                    text = data.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subtitle,
                    color = MutedText,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111B21))
                ) {
                    val textMeasurer = rememberTextMeasurer()

                    Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                        val width = size.width
                        val height = size.height

                        // Padding for axes
                        val paddingBottom = 24.dp.toPx()
                        val paddingStart = 32.dp.toPx()
                        val paddingTop = 16.dp.toPx()
                        val paddingEnd = 16.dp.toPx()

                        val chartWidth = width - paddingStart - paddingEnd
                        val chartHeight = height - paddingBottom - paddingTop

                        // Draw Grid Lines (Y-axis 0%, 25%, 50%, 75%, 100%)
                        val yLevels = listOf(100f, 75f, 50f, 25f, 0f)
                        yLevels.forEachIndexed { index, level ->
                            val y = paddingTop + (chartHeight * (index / 4f))
                            drawLine(
                                color = ChartGridLine,
                                start = Offset(paddingStart, y),
                                end = Offset(width - paddingEnd, y),
                                strokeWidth = 1.dp.toPx()
                            )
                            // Y-axis labels
                            textMeasurer.measure(
                                text = "${level.toInt()}%",
                                style = TextStyle(color = MutedText, fontSize = 10.sp)
                            ).also { layoutResult ->
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = "${level.toInt()}%",
                                    style = TextStyle(color = MutedText, fontSize = 10.sp),
                                    topLeft = Offset(0f, y - layoutResult.size.height / 2f)
                                )
                            }
                        }

                        if (data.chartPoints.isNotEmpty()) {
                            val pointsCount = data.chartPoints.size
                            val pointSpacing = chartWidth / (pointsCount - 1).coerceAtLeast(1)

                            // X-axis labels
                            data.chartPoints.forEachIndexed { index, point ->
                                val x = paddingStart + (index * pointSpacing)
                                val y = height - paddingBottom + 8.dp.toPx()
                                textMeasurer.measure(
                                    text = point.label,
                                    style = TextStyle(color = MutedText, fontSize = 11.sp)
                                ).also { layoutResult ->
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = point.label,
                                        style = TextStyle(color = MutedText, fontSize = 11.sp),
                                        topLeft = Offset(x - layoutResult.size.width / 2f, y)
                                    )
                                }
                            }

                            // Calculate actual pixel coordinates for the line
                            val points = data.chartPoints.mapIndexed { index, point ->
                                val x = paddingStart + (index * pointSpacing)
                                // value is 0-100, invert for Y (0 is top)
                                val y =
                                    paddingTop + chartHeight - (chartHeight * (point.value / 100f))
                                Offset(x, y)
                            }

                            // Draw the line path
                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }

                            drawPath(
                                path = path,
                                color = chartColor,
                                style = Stroke(
                                    width = 2.5.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // Draw points and values
                            points.forEachIndexed { index, point ->
                                val valueLabel = "${data.chartPoints[index].value.toInt()}%"

                                // Draw inner background
                                drawCircle(
                                    color = ChartDotBg,
                                    radius = 5.dp.toPx(),
                                    center = point
                                )
                                // Draw outer ring
                                drawCircle(
                                    color = chartColor,
                                    radius = 5.dp.toPx(),
                                    center = point,
                                    style = Stroke(width = 2.dp.toPx())
                                )

                                // Draw value label above point
                                textMeasurer.measure(
                                    text = valueLabel,
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ).also { layoutResult ->
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = valueLabel,
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        topLeft = Offset(
                                            point.x - layoutResult.size.width / 2f,
                                            point.y - 18.dp.toPx() - layoutResult.size.height / 2f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ChartGridLine)
                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Col 1
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.onTimePayoutsLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.onTimePayoutsValue,
                            color = chartColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(ChartGridLine))

                    // Col 2
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(
                            text = data.activeMembersLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.activeMembersValue,
                            color = chartColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(ChartGridLine))

                    // Col 3
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(
                            text = data.avgWaitLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            val parts = data.avgWaitValue.split(" ")
                            if (parts.size >= 2) {
                                Text(
                                    text = parts[0],
                                    color = chartColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = parts[1],
                                    color = chartColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            } else {
                                Text(
                                    text = data.avgWaitValue,
                                    color = chartColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ChartGridLine)
                Spacer(modifier = Modifier.height(12.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${data.updatedNote} \u00b7 ${meta.time}",
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
                            tint = chartColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
