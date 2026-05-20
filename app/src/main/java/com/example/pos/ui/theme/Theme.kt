package com.example.pos.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,
    secondary = GreenPrimary,
    onSecondary = White,
    secondaryContainer = GreenContainer,
    onSecondaryContainer = GreenDark,
    tertiary = GreenPrimary,
    onTertiary = White,
    tertiaryContainer = GreenContainer,
    onTertiaryContainer = GreenDark,
    background = BackgroundLight,
    onBackground = Color.Black,
    surface = White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = GreenDark,
    error = Color(0xFFB3261E),
    onError = White,
    outline = Color(0xFFB7CBB2),
    inversePrimary = GreenPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenDark,
    onPrimaryContainer = White,
    secondary = GreenPrimary,
    onSecondary = White,
    secondaryContainer = GreenDark,
    onSecondaryContainer = White,
    tertiary = GreenPrimary,
    onTertiary = White,
    tertiaryContainer = GreenDark,
    onTertiaryContainer = White,
    background = Color(0xFF121212),
    onBackground = White,

    // ── PERUBAHAN KONTRAS DARK MODE ──
    // Diperterang dari 0xFF131313 agar Card lebih terlihat batasnya
    surface = Color(0xFF1E1E1E),
    onSurface = White,
    // Diperterang dari 0xFF2C2C2C
    surfaceVariant = Color(0xFF333333),
    onSurfaceVariant = White,

    inversePrimary = GreenPrimary,
)

@Composable
fun SupabaseAuthComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}