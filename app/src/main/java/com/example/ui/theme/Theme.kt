package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberNeonGreen,
    onPrimary = CyberDeepObsidian,
    secondary = CyberNeonCyan,
    onSecondary = CyberDeepObsidian,
    tertiary = CyberWarmAmber,
    background = CyberDeepObsidian,
    onBackground = CyberWhite,
    surface = CyberNavySlate,
    onSurface = CyberWhite,
    surfaceVariant = CyberTerminalGray,
    onSurfaceVariant = CyberLightSlate,
    error = CyberErrorRed,
    onError = CyberDeepObsidian,
    outline = CyberBorderNavy
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
