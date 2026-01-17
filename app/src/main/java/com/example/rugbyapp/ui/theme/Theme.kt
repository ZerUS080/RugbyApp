package com.example.rugbyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF006400),  // Verde rugby
    secondary = androidx.compose.ui.graphics.Color(0xFFD4AF37), // Dorado
    background = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF228B22),  // Verde claro
    secondary = androidx.compose.ui.graphics.Color(0xFFFFD700), // Dorado claro
    background = androidx.compose.ui.graphics.Color(0xFF121212)
)

@Composable
fun RugbyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}