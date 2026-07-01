package com.giovankov.tinytaps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = WhiteSurface,
    primaryContainer = PeachSurfaceVariant,
    onPrimaryContainer = DarkBrownText,
    secondary = CoralLight,
    onSecondary = WhiteSurface,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = DarkBrownText,
    background = CreamBackground,
    onBackground = DarkBrownText,
    surface = WhiteSurface,
    onSurface = DarkBrownText,
    surfaceVariant = PeachSurfaceVariant,
    onSurfaceVariant = DarkBrownText,
    outline = OutlineLight,
    surfaceTint = Coral
)

private val DarkColorScheme = darkColorScheme(
    primary = Coral,
    onPrimary = WhiteSurface,
    primaryContainer = CoralDark,
    onPrimaryContainer = LightText,
    secondary = CoralLight,
    onSecondary = DarkBrownBackground,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = LightText,
    background = DarkBrownBackground,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightText,
    outline = OutlineDark,
    surfaceTint = Coral
)

@Composable
fun TinyTapsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TinyTapsTypography,
        shapes = TinyTapsShapes,
        content = content
    )
}
