package com.microbond.app.core.components.padlockicon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground

/**
 * Custom Canvas drawing for the prominent green Padlock Icon with keyhole cutout.
 */
@Composable
fun MicrobondPadlockIcon(
    modifier: Modifier = Modifier,
    tint: Color = MicrobondActiveGreen,
    cutoutColor: Color = MicrobondDarkBackground
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Shackle (upper loop)
        val shackleWidth = w * 0.60f
        val shackleHeight = h * 0.44f
        val shackleStroke = w * 0.135f
        val shackleLeft = (w - shackleWidth) / 2f
        val shackleTop = 2f

        val shacklePath = Path().apply {
            moveTo(shackleLeft + shackleStroke / 2f, shackleHeight)
            lineTo(shackleLeft + shackleStroke / 2f, shackleTop + shackleWidth / 2f)
            arcTo(
                rect = Rect(
                    offset = Offset(shackleLeft, shackleTop),
                    size = Size(shackleWidth, shackleWidth)
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(shackleLeft + shackleWidth - shackleStroke / 2f, shackleHeight)
        }

        drawPath(
            path = shacklePath,
            color = tint,
            style = Stroke(width = shackleStroke, cap = StrokeCap.Round)
        )

        // Padlock body (rounded rectangle)
        val bodyWidth = w * 0.90f
        val bodyHeight = h * 0.58f
        val bodyLeft = (w - bodyWidth) / 2f
        val bodyTop = h - bodyHeight

        drawRoundRect(
            color = tint,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        )

        // Keyhole circular top
        val keyholeRadius = w * 0.082f
        val keyholeCenterX = w / 2f
        val keyholeCenterY = bodyTop + bodyHeight * 0.38f

        drawCircle(
            color = cutoutColor,
            radius = keyholeRadius,
            center = Offset(keyholeCenterX, keyholeCenterY)
        )

        // Keyhole bottom slot
        val slotTopWidth = keyholeRadius * 0.9f
        val slotBottomWidth = keyholeRadius * 1.35f
        val slotHeight = keyholeRadius * 1.6f

        val slotPath = Path().apply {
            moveTo(keyholeCenterX - slotTopWidth / 2f, keyholeCenterY)
            lineTo(keyholeCenterX + slotTopWidth / 2f, keyholeCenterY)
            lineTo(keyholeCenterX + slotBottomWidth / 2f, keyholeCenterY + slotHeight)
            lineTo(keyholeCenterX - slotBottomWidth / 2f, keyholeCenterY + slotHeight)
            close()
        }

        drawPath(path = slotPath, color = cutoutColor)
    }
}

@Preview
@Composable
private fun MicrobondPadlockIconPreview() {
    MicrobondPadlockIcon(modifier = Modifier.size(56.dp, 68.dp))
}
