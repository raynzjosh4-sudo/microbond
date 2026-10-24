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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.TrackChanges
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.GroupHealthData
import kotlin.math.cos
import kotlin.math.sin

private val MutedText = Color(0xFF8696A0)
private val ChartGridLine = Color.White.copy(alpha = 0.15f)
private val DataColor = Color(0xFF5CE1E6)
private val DataFillColor = DataColor.copy(alpha = 0.2f)

@Composable
fun MicroBondGroupHealthBubble(
    meta: MicroBondMeta,
    data: GroupHealthData,
    modifier: Modifier = Modifier
) {
    val dataColor = data.chartColorValue?.let { Color(it.toInt()) } ?: DataColor
    val dataFillColor = dataColor.copy(alpha = 0.2f)
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

                // Radar Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val textMeasurer = rememberTextMeasurer()

                    Canvas(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF111B21))
                    ) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val maxRadius = (minOf(size.width, size.height) / 2) - 30.dp.toPx()
                        val numPoints = data.radarPoints.size
                        val angleStep = (2 * Math.PI) / numPoints

                        // Draw background web (concentric polygons)
                        val levels = 3
                        for (level in 1..levels) {
                            val currentRadius = maxRadius * (level.toFloat() / levels)
                            val path = Path()

                            for (i in 0 until numPoints) {
                                val angle =
                                    i * angleStep - (Math.PI / 2) // Start from top (-90 degrees)
                                val x = center.x + currentRadius * cos(angle).toFloat()
                                val y = center.y + currentRadius * sin(angle).toFloat()

                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            path.close()

                            drawPath(
                                path = path,
                                color = ChartGridLine,
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = if (level < levels) PathEffect.dashPathEffect(
                                        floatArrayOf(10f, 10f),
                                        0f
                                    ) else null
                                )
                            )
                        }

                        // Draw radial lines from center to outer points
                        for (i in 0 until numPoints) {
                            val angle = i * angleStep - (Math.PI / 2)
                            val x = center.x + maxRadius * cos(angle).toFloat()
                            val y = center.y + maxRadius * sin(angle).toFloat()

                            drawLine(
                                color = ChartGridLine,
                                start = center,
                                end = Offset(x, y),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Draw labels
                            val textRadius = maxRadius + 14.dp.toPx()
                            val textX = center.x + textRadius * cos(angle).toFloat()
                            val textY = center.y + textRadius * sin(angle).toFloat()

                            val labelText = data.radarPoints[i].label
                            val style = TextStyle(color = Color(0xFFD1D7DB), fontSize = 11.sp)

                            textMeasurer.measure(
                                text = labelText,
                                style = style
                            ).also { layoutResult ->
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = labelText,
                                    style = style,
                                    topLeft = Offset(
                                        textX - layoutResult.size.width / 2f,
                                        textY - layoutResult.size.height / 2f
                                    )
                                )
                            }
                        }

                        // Draw data polygon
                        val dataPath = Path()
                        val dataPoints = mutableListOf<Offset>()

                        for (i in 0 until numPoints) {
                            val angle = i * angleStep - (Math.PI / 2)
                            val value = data.radarPoints[i].value.coerceIn(0f, 1f)
                            val radius = maxRadius * value

                            val x = center.x + radius * cos(angle).toFloat()
                            val y = center.y + radius * sin(angle).toFloat()
                            val point = Offset(x, y)
                            dataPoints.add(point)

                            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
                        }
                        dataPath.close()

                        // Fill data polygon
                        drawPath(
                            path = dataPath,
                            color = dataFillColor
                        )

                        // Stroke data polygon
                        drawPath(
                            path = dataPath,
                            color = dataColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                join = StrokeJoin.Round
                            )
                        )

                        // Draw dots at vertices
                        dataPoints.forEach { point ->
                            drawCircle(
                                color = dataColor,
                                radius = 4.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ChartGridLine)
                Spacer(modifier = Modifier.height(16.dp))

                // Actionable Insights
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(dataColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = dataColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Strongest: ",
                        color = dataColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = data.strongestText,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Color(0xFFF59E0B).copy(alpha = 0.15f),
                                CircleShape
                            ), // Orange for focus
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Focus next: ",
                        color = Color(0xFFF59E0B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = data.focusNextText,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ChartGridLine)
                Spacer(modifier = Modifier.height(8.dp))

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
