package com.litera.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LiteraLightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = PurpleContainer,
    onPrimaryContainer = PurplePrimaryDark,
    secondary = PurplePrimaryDark,
    onSecondary = SurfaceWhite,
    secondaryContainer = PurpleContainerAlt,
    onSecondaryContainer = PurplePrimaryDark,
    background = SurfaceWhite,
    onBackground = OnSurfaceDark,
    surface = SurfaceWhite,
    onSurface = OnSurfaceDark,
    surfaceVariant = PurpleContainer,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineLight,
    error = ErrorRed,
    onError = SurfaceWhite
)

// The design mock is light-first; dark theme reuses the same palette with
// inverted surfaces so the app is still usable with system dark mode on.
private val LiteraDarkColorScheme = darkColorScheme(
    primary = PurpleMuted,
    onPrimary = Color(0xFF381E68),
    primaryContainer = PurplePrimaryDark,
    onPrimaryContainer = PurpleContainer,
    secondary = PurpleMuted,
    onSecondary = Color(0xFF381E68),
    secondaryContainer = PurplePrimaryDark,
    onSecondaryContainer = PurpleContainer,
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1E9),
    surface = Color(0xFF1D1B20),
    onSurface = Color(0xFFE6E1E9),
    surfaceVariant = Color(0xFF2B2831),
    onSurfaceVariant = Color(0xFFCAC4CF),
    outline = Color(0xFF948F99),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun LiteraAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LiteraDarkColorScheme else LiteraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LiteraTypography,
        content = content
    )
}
