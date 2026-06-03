package com.example.kihez.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KihezColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    surface = Surface,
    onSurface = OnSurface,
    onBackground = OnBackground,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainer = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceVariant = SurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error
)

@Composable
fun KihezTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KihezColorScheme,
        typography = Typography,
        content = content
    )
}
