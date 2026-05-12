package com.example.pos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(

    // Primary
    primary = GreenPrimary,
    onPrimary = White,

    // Primary Container
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,

    // Secondary
    secondary = GreenPrimary,
    onSecondary = White,

    secondaryContainer = GreenContainer,
    onSecondaryContainer = GreenDark,

    // Tertiary
    tertiary = GreenPrimary,
    onTertiary = White,

    tertiaryContainer = GreenContainer,
    onTertiaryContainer = GreenDark,

    // Background
    background = BackgroundLight,
    onBackground = Color.Black,

    // Surface
    surface = White,
    onSurface = Color.Black,

    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = GreenDark,

    // Error
    error = Color(0xFFB3261E),
    onError = White,

    // Outline
    outline = Color(0xFFB7CBB2),

    // NAVBAR/FAB
    inversePrimary = GreenPrimary
)

private val DarkColorScheme = darkColorScheme(

    primary = GreenPrimary,
    onPrimary = White,

    primaryContainer = GreenDark,
    onPrimaryContainer = White,

    secondary = GreenPrimary,
    onSecondary = White,

    tertiary = GreenPrimary,
    onTertiary = White,

    background = Color(0xFF121212),
    onBackground = White,

    surface = Color(0xFF131313),
    onSurface = White,

    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = White,

    inversePrimary = GreenPrimary,
)

@Composable
fun SupabaseAuthComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}