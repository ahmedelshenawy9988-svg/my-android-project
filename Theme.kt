package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = BackgroundDark,
    primaryContainer = GoldDark,
    onPrimaryContainer = AmberGlow,
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    secondaryContainer = SurfaceDarkElevated,
    onSecondaryContainer = OrangeLight,
    tertiary = GoldLight,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceDarkBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // WAKE UP is intentionally a luxury dark themed app for optimal night/morning eye comfort
    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
