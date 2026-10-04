package com.akiratech.revex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CrimsonDark = Color(0xFF0A0A0A)
val CrimsonPrimary = Color(0xFFB3001B)
val CrimsonAccent = Color(0xFFFF2A2A)
val GoldAccent = Color(0xFFFFD700)
val SurfaceDark = Color(0xFF141414)
val SurfaceVariantDark = Color(0xFF1E1E1E)

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonAccent,
    secondary = GoldAccent,
    background = CrimsonDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun REVEXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
