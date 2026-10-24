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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.data_classes_and_enums.StatGroupSnapshotData
import kotlin.math.cos
import kotlin.math.sin

private val MutedText = Color(0xFF8696A0)
private val ChartGridLine = Color.White.copy(alpha = 0.05f)

@Composable
fun MicroBondStatGroupSnapshotBubble(
    meta: MicroBondMeta,
    data: StatGroupSnapshotData,
    modifier: Modifier = Modifier
) {
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subtitle,
                    color = MutedText,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chart and Legend Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Donut Chart
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val textMeasurer = rememberTextMeasurer()

                        Canvas(modifier = Modifier.size(160.dp)) {
                            val strokeWidth = 50.dp.toPx()
                            val innerRadius = (size.width - strokeWidth) / 2
                            val centerOffset = Offset(size.width / 2, size.height / 2)

                            var currentAngle = -90f // Start from top

                            data.slices.forEach { slice ->
                                val sweepAngle = (slice.percentage / 100f) * 360f

                                drawArc(
                                    color = Color(slice.colorValue.toInt()),
                                    startAngle = currentAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(
                                        size.width - strokeWidth,
                                        size.height - strokeWidth
                                    ),
                                    style = Stroke(width = strokeWidth)
                                )

                                // Draw percentage text inside the slice
                                val textAngle = currentAngle + (sweepAngle / 2)
                                val radians = Math.toRadians(textAngle.toDouble())
                                val textX = centerOffset.x + innerRadius * cos(radians).toFloat()
                                val textY = centerOffset.y + innerRadius * sin(radians).toFloat()

                                val labelText = "${slice.percentage}%"
                                val style = TextStyle(
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

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

                                currentAngle += sweepAngle
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Legend
                    Column(
                        modifier = Modifier.weight(0.8f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        data.slices.forEach { slice ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(slice.colorValue.toInt()))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${slice.percentage}% ${slice.label}",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ChartGridLine)
                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Col 1
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = data.membersLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.membersValue,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(ChartGridLine))

                    // Col 2
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = data.collectedLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.collectedValue,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(ChartGridLine))

                    // Col 3
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = data.nextPayoutLabel,
                            color = MutedText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.nextPayoutValue,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
