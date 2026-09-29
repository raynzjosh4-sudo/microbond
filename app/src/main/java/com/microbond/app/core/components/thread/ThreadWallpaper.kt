package com.microbond.app.core.components.thread

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.microbond.app.R
import com.microbond.app.core.data_classes_and_enums.WallpaperManager
import com.microbond.app.core.ui.theme.MicrobondActiveGreen
import com.microbond.app.core.ui.theme.MicrobondDarkBackground
import kotlin.math.ceil

private val patternWallpapers = setOf(
    R.drawable.wp_curls,
    R.drawable.wp_diagonal_squares,
    R.drawable.wp_double_bubble_dark,
    R.drawable.wp_fancy_cushion,
    R.drawable.wp_wheat
)

/**
 * WhatsApp-style doodle wallpaper pattern drawn at low opacity
 * over the dark chat background (#0B141A), or a custom image/pattern.
 */
@Composable
fun ThreadWallpaper(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MicrobondDarkBackground,
    patternTintColor: Color = MicrobondActiveGreen
) {
    val selectedWallpaper by WallpaperManager.selectedWallpaper.collectAsState()

    if (selectedWallpaper != null) {
        val resId = selectedWallpaper!!
        if (patternWallpapers.contains(resId)) {
            val imageBitmap = ImageBitmap.imageResource(id = resId)
            val backgroundBrush = remember(imageBitmap) {
                ShaderBrush(
                    ImageShader(
                        image = imageBitmap,
                        tileModeX = TileMode.Repeated,
                        tileModeY = TileMode.Repeated
                    )
                )
            }
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .drawBehind {
                        drawRect(
                            brush = backgroundBrush,
                            alpha = 0.12f,
                            colorFilter = ColorFilter.tint(patternTintColor)
                        )
                    }
            )
        } else {
            Image(
                painter = painterResource(id = resId),
                contentDescription = "Chat Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
        }
    } else {
        Canvas(modifier = modifier.fillMaxSize()) {
            val tileSize = 80.dp.toPx()
            val lineWidth = 0.8.dp.toPx()
            val patternColor = Color(0xFF1B2B33).copy(alpha = 0.55f)
            val columns = ceil(size.width / tileSize).toInt() + 1
            val rows = ceil(size.height / tileSize).toInt() + 1

            repeat(rows) { row ->
                repeat(columns) { column ->
                    val x = column * tileSize + if (row % 2 == 0) 0f else tileSize / 2f
                    val y = row * tileSize
                    when ((row + column) % 3) {
                        0 -> {
                            drawCircle(
                                color = patternColor,
                                radius = tileSize * 0.14f,
                                center = Offset(x + tileSize * 0.38f, y + tileSize * 0.42f),
                                style = Stroke(width = lineWidth)
                            )
                            drawLine(
                                color = patternColor,
                                start = Offset(x + tileSize * 0.28f, y + tileSize * 0.42f),
                                end = Offset(x + tileSize * 0.48f, y + tileSize * 0.42f),
                                strokeWidth = lineWidth
                            )
                        }
                        1 -> drawRoundRect(
                            color = patternColor,
                            topLeft = Offset(x + tileSize * 0.2f, y + tileSize * 0.24f),
                            size = Size(tileSize * 0.42f, tileSize * 0.28f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(tileSize * 0.08f),
                            style = Stroke(width = lineWidth)
                        )
                        else -> {
                            drawCircle(
                                color = patternColor,
                                radius = tileSize * 0.10f,
                                center = Offset(x + tileSize * 0.62f, y + tileSize * 0.62f),
                                style = Stroke(width = lineWidth)
                            )
                            drawLine(
                                color = patternColor,
                                start = Offset(x + tileSize * 0.52f, y + tileSize * 0.62f),
                                end = Offset(x + tileSize * 0.72f, y + tileSize * 0.62f),
                                strokeWidth = lineWidth
                            )
                        }
                    }
                }
            }
        }
    }
}