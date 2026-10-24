package com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.itemscreens.screens.statisticscreens.analytics.ParsedChartSeries
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondAttachmentSheetDark
import com.microbond.app.core.ui.theme.MicrobondChipNonSelectedColor
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import com.microbond.app.core.ui.theme.MicrobondDividerDark
import com.microbond.app.core.ui.theme.MicrobondInactiveGray
import com.microbond.app.core.ui.theme.MicrobondTextPrimary
import java.util.Locale

@Composable
fun ChartPreviewPane(
    xId: String,
    yId: String,
    xLabel: String,
    yLabel: String,
    chartId: String,
    chartLabel: String,
    colors: List<Color>,
    granLabel: String,
    liveSeries: ParsedChartSeries? = null,
    onBack: () -> Unit,
    onSend: () -> Unit
) {
    val xData = liveSeries?.xValues ?: emptyList()
    val yData = liveSeries?.yValues ?: emptyList()
    val hasData = xData.isNotEmpty() && yData.isNotEmpty()
    val primary = colors.firstOrNull() ?: MicrobondActiveGreen
    val secondary = if (colors.size > 1) colors[1] else primary.copy(alpha = 0.45f)

    Surface(modifier = Modifier.fillMaxSize(), color = MicrobondDarkBackground) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MicrobondDividerDark)
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MicrobondTextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$chartLabel preview",
                            color = MicrobondTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$yLabel  ·  $xLabel$granLabel",
                            color = MicrobondInactiveGray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                HorizontalDivider(color = MicrobondDividerDark)

                // Summary strip from live backend analytics
                liveSeries?.summary?.let { summary ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryPill(label = "TOTAL", value = formatMetricValue(summary.total, liveSeries.unit), modifier = Modifier.weight(1f))
                        SummaryPill(label = "AVG", value = formatMetricValue(summary.avg), modifier = Modifier.weight(1f))
                        SummaryPill(label = "MIN", value = formatMetricValue(summary.min), modifier = Modifier.weight(1f))
                        SummaryPill(label = "MAX", value = formatMetricValue(summary.max), modifier = Modifier.weight(1f))
                    }
                }

                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    LegendItem(yLabel, primary)
                    LegendItem(xLabel, secondary)
                }

                // Chart canvas Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MicrobondAttachmentSheetDark)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasData) {
                        ChartPreviewPlot(
                            chartId = chartId,
                            xData = xData,
                            yData = yData,
                            primaryColor = primary,
                            secondaryColor = secondary,
                            xLabel = xLabel,
                            yLabel = yLabel
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "No Activity Recorded",
                                color = MicrobondTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "There are no settled deposits or records for $yLabel in this time range yet.",
                                color = MicrobondInactiveGray,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(80.dp))
            }

            // FAB send button (only active when data exists)
            FloatingActionButton(
                onClick = onSend,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = if (hasData) MicrobondActiveGreen else MicrobondChipNonSelectedColor,
                contentColor = if (hasData) MicrobondDarkBackground else MicrobondInactiveGray
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send", modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun SummaryPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MicrobondChipNonSelectedColor)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(label, color = MicrobondInactiveGray, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = MicrobondTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = MicrobondTextPrimary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatMetricValue(value: Double, unit: String? = null): String {
    return when {
        value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
        value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000)
        value % 1.0 == 0.0 -> String.format(Locale.US, "%.0f", value)
        else -> String.format(Locale.US, "%.1f", value)
    }
}
