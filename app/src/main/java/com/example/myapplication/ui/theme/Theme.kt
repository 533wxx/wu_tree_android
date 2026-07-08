package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WuDarkColorScheme = darkColorScheme(
    primary = WuAccent,
    onPrimary = WuBg,
    primaryContainer = WuAccentDim,
    onPrimaryContainer = WuText,
    secondary = WuAccentDim,
    onSecondary = WuBg,
    background = WuBg,
    onBackground = WuText,
    surface = WuSurface,
    onSurface = WuText,
    surfaceVariant = WuSurface2,
    onSurfaceVariant = WuTextMuted,
    outline = WuBorder,
    outlineVariant = WuBorderLight,
    error = WuRed,
    onError = WuText,
    errorContainer = WuRedBg,
    onErrorContainer = WuRed,
    scrim = WuBg.copy(alpha = 0.6f)
)

@Composable
fun WuFamilyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WuDarkColorScheme,
        typography = Typography,
        content = content
    )
}
