package com.akiratech.revex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CrosshairCanvas(
    config: CrosshairConfig,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(config.sizeDp.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val mainColor = Color(config.colorArgb).copy(alpha = config.opacity)
        val centerColor = Color(config.centerColorArgb).copy(alpha = config.opacity)

        rotate(config.rotationDeg, pivot = center) {
            if (config.isSharingan) {
                drawSharinganCrosshair(
                    center = center,
                    radius = size.width / 2f,
                    config = config,
                    mainColor = mainColor,
                    centerColor = centerColor
                )
            } else {
                drawStandardCrosshair(
                    center = center,
                    config = config,
                    mainColor = mainColor,
                    centerColor = centerColor
                )
            }
        }
    }
}

private fun DrawScope.drawStandardCrosshair(
    center: Offset,
    config: CrosshairConfig,
    mainColor: Color,
    centerColor: Color
) {
    val thicknessPx = config.thicknessDp.dp.toPx()
    val gapPx = config.gapDp.dp.toPx()
    val armLengthPx = (config.sizeDp.dp.toPx() / 2f) - gapPx

    if (armLengthPx > 0) {
        // Top arm
        drawLine(
            color = mainColor,
            start = Offset(center.x, center.y - gapPx),
            end = Offset(center.x, center.y - gapPx - armLengthPx),
            strokeWidth = thicknessPx
        )
        // Bottom arm
        drawLine(
            color = mainColor,
            start = Offset(center.x, center.y + gapPx),
            end = Offset(center.x, center.y + gapPx + armLengthPx),
            strokeWidth = thicknessPx
        )
        // Left arm
        drawLine(
            color = mainColor,
            start = Offset(center.x - gapPx, center.y),
            end = Offset(center.x - gapPx - armLengthPx, center.y),
            strokeWidth = thicknessPx
        )
        // Right arm
        drawLine(
            color = mainColor,
            start = Offset(center.x + gapPx, center.y),
            end = Offset(center.x + gapPx + armLengthPx, center.y),
            strokeWidth = thicknessPx
        )
    }

    // Draw center mark
    drawCenterMark(center, config.centerStyle, centerColor, config.centerSizeDp.dp.toPx())
}

private fun DrawScope.drawSharinganCrosshair(
    center: Offset,
    radius: Float,
    config: CrosshairConfig,
    mainColor: Color,
    centerColor: Color
) {
    val sharinganAlpha = (config.sharinganOpacity * config.opacity).coerceIn(0.1f, 1.0f)
    val sharinganColor = Color(config.colorArgb).copy(alpha = sharinganAlpha)
    val strokePx = config.thicknessDp.dp.toPx().coerceAtLeast(2f)

    val outerRadius = radius * 0.95f
    val innerRadius = radius * 0.55f

    // 1. Outer Ring
    drawCircle(
        color = sharinganColor,
        radius = outerRadius,
        center = center,
        style = Stroke(width = strokePx)
    )

    // 2. Inner Ring (delineating empty center)
    drawCircle(
        color = sharinganColor,
        radius = innerRadius,
        center = center,
        style = Stroke(width = strokePx * 0.75f)
    )

    // Note: The area inside innerRadius is kept EMPTY / TRANSPARENT per Sharingan design rule.

    // 3. Draw Tomoe / Mangekyo Blades on the iris ring area
    when (config.sharinganType) {
        1 -> drawTomoePattern(center, innerRadius, outerRadius, 1, sharinganColor)
        2 -> drawTomoePattern(center, innerRadius, outerRadius, 2, sharinganColor)
        3 -> drawTomoePattern(center, innerRadius, outerRadius, 3, sharinganColor)
        4 -> drawMangekyoBlades(center, innerRadius, outerRadius, sharinganColor)
        else -> drawTomoePattern(center, innerRadius, outerRadius, 3, sharinganColor)
    }

    // 4. Draw Center Mark if requested (independent of empty center iris)
    drawCenterMark(center, config.centerStyle, centerColor, config.centerSizeDp.dp.toPx())
}

private fun DrawScope.drawTomoePattern(
    center: Offset,
    innerR: Float,
    outerR: Float,
    count: Int,
    color: Color
) {
    val tomoeRadius = (outerR - innerR) * 0.45f
    val trackRadius = (innerR + outerR) / 2f

    for (i in 0 until count) {
        val angleDeg = i * (360f / count) - 90f
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val tx = center.x + trackRadius * cos(angleRad).toFloat()
        val ty = center.y + trackRadius * sin(angleRad).toFloat()

        // Draw Tomoe head
        drawCircle(
            color = color,
            radius = tomoeRadius,
            center = Offset(tx, ty)
        )

        // Draw Tomoe curved tail
        val tailPath = Path().apply {
            val startAngle = angleRad
            val endAngle = angleRad + Math.toRadians(45.0)
            moveTo(tx, ty)
            quadraticTo(
                (center.x + (trackRadius + tomoeRadius * 1.2f) * cos(startAngle + 0.3).toFloat()),
                (center.y + (trackRadius + tomoeRadius * 1.2f) * sin(startAngle + 0.3).toFloat()),
                (center.x + trackRadius * cos(endAngle).toFloat()),
                (center.y + trackRadius * sin(endAngle).toFloat())
            )
        }
        drawPath(
            path = tailPath,
            color = color,
            style = Stroke(width = tomoeRadius * 0.8f)
        )
    }
}

private fun DrawScope.drawMangekyoBlades(
    center: Offset,
    innerR: Float,
    outerR: Float,
    color: Color
) {
    val bladeCount = 3
    for (i in 0 until bladeCount) {
        val angleDeg = i * 120f
        rotate(angleDeg, pivot = center) {
            val bladePath = Path().apply {
                moveTo(center.x, center.y - innerR)
                cubicTo(
                    center.x - outerR * 0.6f, center.y - outerR * 0.7f,
                    center.x - outerR * 0.2f, center.y - outerR * 1.1f,
                    center.x, center.y - outerR
                )
                cubicTo(
                    center.x + outerR * 0.2f, center.y - outerR * 1.1f,
                    center.x + outerR * 0.6f, center.y - outerR * 0.7f,
                    center.x, center.y - innerR
                )
                close()
            }
            drawPath(path = bladePath, color = color)
        }
    }
}

private fun DrawScope.drawCenterMark(
    center: Offset,
    style: CenterStyle,
    color: Color,
    sizePx: Float
) {
    when (style) {
        CenterStyle.NONE -> { /* No center mark */ }
        CenterStyle.DOT -> {
            drawCircle(
                color = color,
                radius = sizePx / 2f,
                center = center
            )
        }
        CenterStyle.CROSS -> {
            val half = sizePx / 2f
            drawLine(color, Offset(center.x - half, center.y), Offset(center.x + half, center.y), strokeWidth = 2f)
            drawLine(color, Offset(center.x, center.y - half), Offset(center.x, center.y + half), strokeWidth = 2f)
        }
    }
}
