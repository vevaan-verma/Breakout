package com.vevaan.breakout.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BreakoutPrimary,
    onPrimary = BreakoutTextPrimary,
    secondary = BreakoutSecondary,
    onSecondary = BreakoutBackground,
    tertiary = BreakoutCoral,
    background = BreakoutBackground,
    onBackground = BreakoutTextPrimary,
    surface = BreakoutSurface,
    onSurface = BreakoutTextPrimary,
    surfaceVariant = BreakoutSurfaceVariant,
    onSurfaceVariant = BreakoutTextSecondary,
    outline = BreakoutOutline,
    error = BreakoutNegative
)

private val LightColorScheme = lightColorScheme(
    primary = BreakoutPrimaryDark,
    onPrimary = BreakoutTextPrimary,
    secondary = BreakoutSecondary,
    onSecondary = BreakoutBackground,
    tertiary = BreakoutCoral,
    background = BreakoutTextPrimary,
    onBackground = BreakoutBackground,
    surface = BreakoutTextPrimary,
    onSurface = BreakoutBackground,
    surfaceVariant = BreakoutSurfaceVariant,
    onSurfaceVariant = BreakoutTextSecondary,
    outline = BreakoutOutline,
    error = BreakoutNegative
)

@Composable
fun BreakoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
