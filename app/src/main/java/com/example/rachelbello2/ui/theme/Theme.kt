package com.example.rachelbello2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightBlueColorScheme = lightColorScheme(
    primary = LightBluePrimary,
    onPrimary = LightBlueOnPrimary,
    primaryContainer = LightBluePrimaryContainer,
    onPrimaryContainer = LightBlueOnPrimaryContainer,
    secondary = LightBlueSecondary,
    onSecondary = LightBlueOnSecondary,
    secondaryContainer = LightBlueSecondaryContainer,
    onSecondaryContainer = LightBlueOnSecondaryContainer,
    tertiary = LightBlueTertiary,
    onTertiary = LightBlueOnTertiary,
    tertiaryContainer = LightBlueTertiaryContainer,
    onTertiaryContainer = LightBlueOnTertiaryContainer,
    error = LightBlueError,
    onError = LightBlueOnError,
    errorContainer = LightBlueErrorContainer,
    onErrorContainer = LightBlueOnErrorContainer,
    background = LightBlueBackground,
    onBackground = LightBlueOnBackground,
    surface = LightBlueSurface,
    onSurface = LightBlueOnSurface,
    surfaceVariant = LightBlueSurfaceVariant,
    onSurfaceVariant = LightBlueOnSurfaceVariant,
    outline = LightBlueOutline
)

@Composable
fun RachelBelloTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightBlueColorScheme,
        content = content
    )
}
