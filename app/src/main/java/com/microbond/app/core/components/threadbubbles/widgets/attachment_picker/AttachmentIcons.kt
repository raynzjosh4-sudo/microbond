package com.microbond.app.core.components.threadbubbles.widgets.attachment_picker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// ─── Modern WhatsApp Wireframe Icons ──────────────────────────────────────────

val AttachGallery: ImageVector
    get() = ImageVector.Builder(
        name = "AttachGallery",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Front rectangle
            moveTo(9f, 5f)
            lineTo(19f, 5f)
            curveTo(20.1f, 5f, 21f, 5.9f, 21f, 7f)
            lineTo(21f, 17f)
            curveTo(21f, 18.1f, 20.1f, 19f, 19f, 19f)
            lineTo(9f, 19f)
            curveTo(7.9f, 19f, 7f, 18.1f, 7f, 17f)
            lineTo(7f, 7f)
            curveTo(7f, 5.9f, 7.9f, 5f, 9f, 5f)
            close()
            // Back rectangle
            moveTo(5f, 15f)
            lineTo(3f, 15f)
            curveTo(3f, 13f, 3f, 7f, 3f, 5f)
            curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
            lineTo(15f, 3f)
            lineTo(15f, 5f)
        }
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Mountains
            moveTo(7f, 17f)
            lineTo(11f, 12f)
            lineTo(14f, 16f)
            lineTo(16.5f, 13f)
            lineTo(21f, 19f)
        }
    }.build()

val AttachDepositStatus: ImageVector
    get() = ImageVector.Builder(
        name = "AttachDepositStatus",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6f, 3f)
            lineTo(15f, 3f)
            lineTo(19f, 7f)
            lineTo(19f, 21f)
            lineTo(6f, 21f)
            close()
            moveTo(15f, 3f)
            lineTo(15f, 7f)
            lineTo(19f, 7f)
            moveTo(9f, 12f)
            lineTo(11f, 14f)
            lineTo(14f, 11f)
            moveTo(9f, 17f)
            horizontalLineTo(16f)
        }
    }.build()

val AttachSaveReport: ImageVector
    get() = ImageVector.Builder(
        name = "AttachSaveReport",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(13f, 3f)
            horizontalLineTo(6f)
            curveTo(4.9f, 3f, 4f, 3.9f, 4f, 5f)
            verticalLineTo(19f)
            curveTo(4f, 20.1f, 4.9f, 21f, 6f, 21f)
            horizontalLineTo(13f)
            moveTo(13f, 3f)
            lineTo(17f, 7f)
            horizontalLineTo(13f)
            verticalLineTo(3f)
            moveTo(7f, 11f)
            horizontalLineTo(11f)
            moveTo(7f, 14f)
            horizontalLineTo(11f)
            moveTo(18f, 11f)
            verticalLineTo(17f)
            moveTo(15f, 14f)
            lineTo(18f, 17f)
            lineTo(21f, 14f)
            moveTo(15f, 20f)
            horizontalLineTo(21f)
        }
    }.build()

val AttachLocation: ImageVector
    get() = ImageVector.Builder(
        name = "AttachLocation",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 22f)
            curveTo(12f, 22f, 19f, 14.25f, 19f, 9f)
            curveTo(19f, 5.13f, 15.87f, 2f, 12f, 2f)
            curveTo(8.13f, 2f, 5f, 5.13f, 5f, 9f)
            curveTo(5f, 14.25f, 12f, 22f, 12f, 22f)
            close()
            // Inner circle
            moveTo(12f, 12f)
            curveTo(10.34f, 12f, 9f, 10.66f, 9f, 9f)
            curveTo(9f, 7.34f, 10.34f, 6f, 12f, 6f)
            curveTo(13.66f, 6f, 15f, 7.34f, 15f, 9f)
            curveTo(15f, 10.66f, 13.66f, 12f, 12f, 12f)
            close()
        }
    }.build()

val AttachContact: ImageVector
    get() = ImageVector.Builder(
        name = "AttachContact",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Head
            moveTo(12f, 11f)
            curveTo(14.21f, 11f, 16f, 9.21f, 16f, 7f)
            curveTo(16f, 4.79f, 14.21f, 3f, 12f, 3f)
            curveTo(9.79f, 3f, 8f, 4.79f, 8f, 7f)
            curveTo(8f, 9.21f, 9.79f, 11f, 12f, 11f)
            close()
            // Shoulders
            moveTo(12f, 14f)
            curveTo(9.33f, 14f, 4f, 15.34f, 4f, 18f)
            verticalLineTo(20f)
            horizontalLineTo(20f)
            verticalLineTo(18f)
            curveTo(20f, 15.34f, 14.67f, 14f, 12f, 14f)
            close()
        }
    }.build()

val AttachDocument: ImageVector
    get() = ImageVector.Builder(
        name = "AttachDocument",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14f, 2f)
            horizontalLineTo(6f)
            curveTo(4.9f, 2f, 4f, 2.9f, 4f, 4f)
            verticalLineTo(20f)
            curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f)
            horizontalLineTo(18f)
            curveTo(19.1f, 22f, 20f, 21.1f, 20f, 20f)
            verticalLineTo(8f)
            lineTo(14f, 2f)
            close()
            moveTo(13f, 3f)
            lineTo(13f, 9f)
            lineTo(19f, 9f)
            // Lines
            moveTo(8f, 13f)
            horizontalLineTo(16f)
            moveTo(8f, 17f)
            horizontalLineTo(16f)
        }
    }.build()

val AttachPoll: ImageVector
    get() = ImageVector.Builder(
        name = "AttachPoll",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2.2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6f, 7f)
            horizontalLineTo(18f)
            moveTo(6f, 12f)
            horizontalLineTo(14f)
            moveTo(6f, 17f)
            horizontalLineTo(10f)
        }
    }.build()

val AttachEvent: ImageVector
    get() = ImageVector.Builder(
        name = "AttachEvent",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 4f)
            horizontalLineTo(18f)
            verticalLineTo(2f)
            moveTo(16f, 4f)
            horizontalLineTo(8f)
            verticalLineTo(2f)
            moveTo(6f, 4f)
            horizontalLineTo(5f)
            curveTo(3.9f, 4f, 3f, 4.9f, 3f, 6f)
            verticalLineTo(20f)
            curveTo(3f, 21.1f, 3.9f, 22f, 5f, 22f)
            horizontalLineTo(19f)
            curveTo(20.1f, 22f, 21f, 21.1f, 21f, 20f)
            verticalLineTo(6f)
            curveTo(21f, 4.9f, 20.1f, 4f, 19f, 4f)
            close()
            moveTo(3f, 10f)
            horizontalLineTo(21f)
            moveTo(9f, 16f)
            lineTo(9f, 16.01f) // Dot
        }
    }.build()

val AttachAiImages: ImageVector
    get() = ImageVector.Builder(
        name = "AttachAiImages",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Frame
            moveTo(17f, 9f)
            lineTo(17f, 6f)
            curveTo(17f, 4.9f, 16.1f, 4f, 15f, 4f)
            lineTo(6f, 4f)
            curveTo(4.9f, 4f, 4f, 4.9f, 4f, 6f)
            lineTo(4f, 18f)
            curveTo(4f, 19.1f, 4.9f, 20f, 6f, 20f)
            lineTo(15f, 20f)
            curveTo(16.1f, 20f, 17f, 19.1f, 17f, 18f)
            lineTo(17f, 15f)
            // Sparkle
            moveTo(19f, 13f)
            lineTo(19.8f, 10.8f)
            lineTo(22f, 10f)
            lineTo(19.8f, 9.2f)
            lineTo(19f, 7f)
            lineTo(18.2f, 9.2f)
            lineTo(16f, 10f)
            lineTo(18.2f, 10.8f)
            close()
        }
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Mountain
            moveTo(4f, 18f)
            lineTo(8.5f, 12f)
            lineTo(11.5f, 16f)
            lineTo(14f, 13f)
            lineTo(17f, 18f)
        }
    }.build()

val AttachRules: ImageVector
    get() = ImageVector.Builder(
        name = "AttachRules",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9f, 4f)
            horizontalLineTo(6f)
            curveTo(4.9f, 4f, 4f, 4.9f, 4f, 6f)
            verticalLineTo(19f)
            curveTo(4f, 20.1f, 4.9f, 21f, 6f, 21f)
            horizontalLineTo(18f)
            curveTo(19.1f, 21f, 20f, 20.1f, 20f, 19f)
            verticalLineTo(6f)
            curveTo(20f, 4.9f, 19.1f, 4f, 18f, 4f)
            horizontalLineTo(15f)
            moveTo(9f, 4f)
            curveTo(9f, 2.9f, 9.9f, 2f, 11f, 2f)
            horizontalLineTo(13f)
            curveTo(14.1f, 2f, 15f, 2.9f, 15f, 4f)
            verticalLineTo(6f)
            horizontalLineTo(9f)
            verticalLineTo(4f)
            moveTo(7f, 11f)
            lineTo(8.5f, 12.5f)
            lineTo(11f, 10f)
            moveTo(13f, 11f)
            horizontalLineTo(17f)
            moveTo(7f, 16f)
            lineTo(8.5f, 17.5f)
            lineTo(11f, 15f)
            moveTo(13f, 16f)
            horizontalLineTo(17f)
        }
    }.build()

val AttachStatistics: ImageVector
    get() = ImageVector.Builder(
        name = "AttachStatistics",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4f, 20f)
            horizontalLineTo(21f)
            moveTo(7f, 17f)
            verticalLineTo(12f)
            moveTo(12f, 17f)
            verticalLineTo(8f)
            moveTo(17f, 17f)
            verticalLineTo(4f)
        }
    }.build()
