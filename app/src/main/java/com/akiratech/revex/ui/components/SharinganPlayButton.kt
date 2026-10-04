package com.akiratech.revex.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SharinganPlayButton(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    isBoosting: Boolean = false,
    onClick: () -> Unit
) {
    // Rotation animation speed switch: Continuous spin
    val infiniteTransition = rememberInfiniteTransition(label = "SharinganSpin")

    val duration = if (isBoosting) 350 else 6000
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    // Pulse animation for outer glow
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerRadius = this.size.width / 2f * 0.92f
            val irisRadius = outerRadius * 0.75f
            val innerPupilRadius = outerRadius * 0.32f

            // 1. Outer Crimson Glow Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF2A2A).copy(alpha = 0.6f * pulseGlow),
                        Color(0xFFB3001B).copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius * 1.15f
                ),
                radius = outerRadius * 1.15f,
                center = center
            )

            // 2. Eye Frame Gold Accent Circle
            drawCircle(
                color = Color(0xFFFFD700),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // 3. Iris Background Gradient (Rich Crimson Iris)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF3333),
                        Color(0xFFB3001B),
                        Color(0xFF4A000A)
                    ),
                    center = center,
                    radius = irisRadius
                ),
                radius = irisRadius,
                center = center
            )

            // 4. Iris Outer Border
            drawCircle(
                color = Color(0xFF101010),
                radius = irisRadius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // 5. Spinning Tomoe Layer
            rotate(rotationAngle, pivot = center) {
                // Tomoe ring track
                val tomoeTrackRadius = (irisRadius + innerPupilRadius) / 2f
                drawCircle(
                    color = Color(0xFF1A1A1A),
                    radius = tomoeTrackRadius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // 3 Tomoe
                val tomoeRadius = (irisRadius - innerPupilRadius) * 0.35f
                for (i in 0 until 3) {
                    val angleDeg = i * 120f - 90f
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val tx = center.x + tomoeTrackRadius * cos(angleRad).toFloat()
                    val ty = center.y + tomoeTrackRadius * sin(angleRad).toFloat()

                    // Tomoe Circle
                    drawCircle(
                        color = Color.Black,
                        radius = tomoeRadius,
                        center = Offset(tx, ty)
                    )

                    // Tomoe Tail Curve
                    val tailPath = Path().apply {
                        val endAngle = angleRad + Math.toRadians(50.0)
                        moveTo(tx, ty)
                        quadraticTo(
                            (center.x + (tomoeTrackRadius + tomoeRadius * 1.4f) * cos(angleRad + 0.35).toFloat()),
                            (center.y + (tomoeTrackRadius + tomoeRadius * 1.4f) * sin(angleRad + 0.35).toFloat()),
                            (center.x + tomoeTrackRadius * cos(endAngle).toFloat()),
                            (center.y + tomoeTrackRadius * sin(endAngle).toFloat())
                        )
                    }
                    drawPath(
                        path = tailPath,
                        color = Color.Black,
                        style = Stroke(width = tomoeRadius * 0.9f)
                    )
                }
            }

            // 6. Black Center Pupil
            drawCircle(
                color = Color.Black,
                radius = innerPupilRadius,
                center = center
            )

            // Gold Ring around pupil
            drawCircle(
                color = Color(0xFFFFD700),
                radius = innerPupilRadius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Center Pupil Label Text: Explicitly PLAY
        Text(
            text = if (isBoosting) "BOOST" else "PLAY",
            color = Color(0xFFFFD700),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
    }
}
