package com.arc.arc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArcAccent = Color(0xFFD7FF2F)

private val DarkColors = darkColorScheme(
    primary = ArcAccent,
    background = Color(0xFF141414),
    surface = Color(0xFF202020),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF4E6500),
    background = Color(0xFFF9FAFB),
    surface = Color.White,
)

@Composable
fun ArcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
