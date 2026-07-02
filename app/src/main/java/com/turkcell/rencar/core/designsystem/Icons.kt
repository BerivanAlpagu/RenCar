package com.turkcell.rencar.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object RenCarIcons {
    val CarLogo: ImageVector by lazy {
        ImageVector.Builder(
            name = "CarLogo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 13f)
                lineTo(4.5f, 8.8f)
                curveTo(4.7f, 8.0f, 5.5f, 7.5f, 6.4f, 7.5f)
                lineTo(17.6f, 7.5f)
                curveTo(18.5f, 7.5f, 19.3f, 8.0f, 19.5f, 8.8f)
                lineTo(21f, 13f)
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 13f)
                horizontalLineTo(19f)
                curveTo(19.6f, 13f, 20f, 13.4f, 20f, 14f)
                verticalLineTo(16.5f)
                curveTo(20f, 17.1f, 19.6f, 17.5f, 19f, 17.5f)
                horizontalLineTo(18f)
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 13f)
                curveTo(4.4f, 13f, 4f, 13.4f, 4f, 14f)
                verticalLineTo(16.5f)
                curveTo(4f, 17.1f, 4.4f, 17.5f, 5f, 17.5f)
                horizontalLineTo(5.5f)
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7.5f, 17.5f)
                horizontalLineTo(16.5f)
            }
            // Left Wheel
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 17.5f)
                curveTo(8f, 18.38f, 7.28f, 19.1f, 6.4f, 19.1f)
                curveTo(5.52f, 19.1f, 4.8f, 18.38f, 4.8f, 17.5f)
                curveTo(4.8f, 16.62f, 5.52f, 15.9f, 6.4f, 15.9f)
                curveTo(7.28f, 15.9f, 8f, 16.62f, 8f, 17.5f)
                close()
            }
            // Right Wheel
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19.2f, 17.5f)
                curveTo(19.2f, 18.38f, 18.48f, 19.1f, 17.6f, 19.1f)
                curveTo(16.72f, 19.1f, 16f, 18.38f, 16f, 17.5f)
                curveTo(16f, 16.62f, 16.72f, 15.9f, 17.6f, 15.9f)
                curveTo(18.48f, 15.9f, 19.2f, 16.62f, 19.2f, 17.5f)
                close()
            }
        }.build()
    }
}
