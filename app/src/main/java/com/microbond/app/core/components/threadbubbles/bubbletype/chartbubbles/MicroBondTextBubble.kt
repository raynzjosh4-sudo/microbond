package com.microbond.app.core.components.threadbubbles.bubbletype.chartbubbles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microbond.app.core.components.threadbubbles.MicroBondBubbleSurface
import com.microbond.app.core.components.threadbubbles.MicroBondInk
import com.microbond.app.core.components.threadbubbles.MicroBondMuted
import com.microbond.app.core.components.threadbubbles.model.MicroBondMeta
import com.microbond.app.core.components.threadbubbles.widgets.statusicon.MicroBondStatusIcon
import kotlin.math.ceil

@Composable
fun MicroBondTextBubble(text: String, meta: MicroBondMeta, modifier: Modifier = Modifier) {
    var isExpanded by remember(text) { mutableStateOf(false) }
    var canExpand by remember(text) { mutableStateOf(false) }
    val collapsedLineLimit = 8

    MicroBondBubbleSurface(
        meta = meta,
        modifier = modifier,
        maxWidthFraction = 0.82f,
        showTimestamp = false,
        contentPadding = PaddingValues(
            start = if (meta.outgoing) 9.dp else 13.dp,
            end = if (meta.outgoing) 13.dp else 9.dp,
            top = 5.dp,
            bottom = 5.dp
        )
    ) {
        WhatsAppTextWithTimestampLayout(
            text = text,
            meta = meta,
            isExpanded = isExpanded,
            canExpand = canExpand,
            collapsedLineLimit = collapsedLineLimit,
            onCanExpand = { canExpand = it },
            onToggleExpand = { isExpanded = !isExpanded }
        )
    }
}

@Composable
private fun WhatsAppTextWithTimestampLayout(
    text: String,
    meta: MicroBondMeta,
    isExpanded: Boolean,
    canExpand: Boolean,
    collapsedLineLimit: Int = 8,
    onCanExpand: (Boolean) -> Unit,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        color = MicroBondInk,
        fontSize = 15.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    )

    Layout(
        modifier = modifier,
        content = {
            // Measurable 0: Text
            Text(
                text = text,
                style = textStyle,
                textAlign = TextAlign.Start,
                maxLines = if (isExpanded) Int.MAX_VALUE else collapsedLineLimit,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { layout ->
                    if (!isExpanded && layout.hasVisualOverflow != canExpand) {
                        onCanExpand(layout.hasVisualOverflow)
                    }
                }
            )
            // Measurable 1: Timestamp + Status ticks
            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meta.time,
                    color = MicroBondMuted,
                    fontSize = 11.sp,
                    letterSpacing = 0.sp,
                    lineHeight = 15.sp
                )
                if (meta.outgoing) {
                    MicroBondStatusIcon(meta.status)
                }
            }
            // Measurable 2: "View more" / "View less"
            if (canExpand) {
                Text(
                    text = if (isExpanded) "View less" else "View more",
                    modifier = Modifier
                        .clickable(onClick = onToggleExpand)
                        .padding(top = 2.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    letterSpacing = 0.sp,
                    lineHeight = 18.sp
                )
            }
        }
    ) { measurables, constraints ->
        val textMeasurable = measurables[0]
        val timeMeasurable = measurables[1]
        val expandMeasurable = if (canExpand && measurables.size > 2) measurables[2] else null

        val timePlaceable = timeMeasurable.measure(Constraints())
        val timeWidth = timePlaceable.width
        val timeHeight = timePlaceable.height
        val spacing = 8.dp.roundToPx()

        val textResult = textMeasurer.measure(
            text = AnnotatedString(text),
            style = textStyle,
            constraints = Constraints(maxWidth = constraints.maxWidth),
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedLineLimit,
            overflow = TextOverflow.Ellipsis
        )

        val textPlaceable = textMeasurable.measure(
            Constraints(maxWidth = constraints.maxWidth)
        )

        val lineCount = textResult.lineCount
        val lastLineIndex = lineCount - 1
        val lastLineWidth = if (lineCount > 0) {
            textResult.getLineRight(lastLineIndex) - textResult.getLineLeft(lastLineIndex)
        } else 0f

        val maxLineWidth = if (lineCount > 0) {
            (0 until lineCount).maxOf { textResult.getLineRight(it) - textResult.getLineLeft(it) }
        } else 0f

        val expandPlaceable = expandMeasurable?.measure(Constraints(maxWidth = constraints.maxWidth))

        if (expandPlaceable != null) {
            val expandWidth = expandPlaceable.width
            val totalW = maxOf(
                ceil(maxLineWidth).toInt(),
                expandWidth + spacing + timeWidth,
                constraints.minWidth
            )
            val totalH = textPlaceable.height + maxOf(expandPlaceable.height, timeHeight) + 4.dp.roundToPx()

            layout(totalW, totalH) {
                textPlaceable.placeRelative(0, 0)
                expandPlaceable.placeRelative(0, textPlaceable.height + 2.dp.roundToPx())
                timePlaceable.placeRelative(
                    totalW - timeWidth,
                    totalH - timeHeight - 1.dp.roundToPx()
                )
            }
        } else {
            val lastLineHasRoom = (ceil(lastLineWidth).toInt() + spacing + timeWidth) <= constraints.maxWidth

            if (lineCount == 1 && lastLineHasRoom) {
                // Single line: text and timestamp side-by-side on the same row!
                val naturalWidth = ceil(lastLineWidth).toInt() + spacing + timeWidth
                val totalW = maxOf(naturalWidth, constraints.minWidth)
                val totalH = maxOf(textPlaceable.height, timeHeight)

                layout(totalW, totalH) {
                    val textY = (totalH - textPlaceable.height) / 2
                    val timeY = totalH - timeHeight - 1.dp.roundToPx()
                    textPlaceable.placeRelative(0, textY)
                    timePlaceable.placeRelative(totalW - timeWidth, timeY)
                }
            } else if (lineCount > 1 && lastLineHasRoom && (ceil(lastLineWidth).toInt() + spacing + timeWidth) <= maxOf(ceil(maxLineWidth).toInt(), constraints.minWidth)) {
                // Multi-line: timestamp fits within the existing width of the text block!
                val naturalWidth = maxOf(ceil(maxLineWidth).toInt(), constraints.minWidth)
                val totalW = naturalWidth
                val totalH = textPlaceable.height

                layout(totalW, totalH) {
                    textPlaceable.placeRelative(0, 0)
                    timePlaceable.placeRelative(
                        totalW - timeWidth,
                        totalH - timeHeight - 1.dp.roundToPx()
                    )
                }
            } else if (lineCount > 1 && lastLineHasRoom && (ceil(lastLineWidth).toInt() + spacing + timeWidth) <= constraints.maxWidth) {
                // Multi-line: last line has room, expands bubble slightly to accommodate timestamp on the last line
                val naturalWidth = maxOf(ceil(maxLineWidth).toInt(), ceil(lastLineWidth).toInt() + spacing + timeWidth)
                val totalW = maxOf(naturalWidth, constraints.minWidth)
                val totalH = textPlaceable.height

                layout(totalW, totalH) {
                    textPlaceable.placeRelative(0, 0)
                    timePlaceable.placeRelative(
                        totalW - timeWidth,
                        totalH - timeHeight - 1.dp.roundToPx()
                    )
                }
            } else {
                // Timestamp wraps below text
                val naturalWidth = maxOf(ceil(maxLineWidth).toInt(), timeWidth)
                val totalW = maxOf(naturalWidth, constraints.minWidth)
                val totalH = textPlaceable.height + timeHeight + 2.dp.roundToPx()

                layout(totalW, totalH) {
                    textPlaceable.placeRelative(0, 0)
                    timePlaceable.placeRelative(
                        totalW - timeWidth,
                        textPlaceable.height + 2.dp.roundToPx()
                    )
                }
            }
        }
    }
}
