package com.turkcell.rencar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightScheme: ColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Neutral0,
    primaryContainer = Color(0xFFD8E8FF),
    onPrimaryContainer = Neutral900,
    secondary = Color(0xFF2E3A53),
    onSecondary = Neutral0,
    secondaryContainer = Neutral100,
    onSecondaryContainer = Neutral900,
    tertiary = BrandMint,
    onTertiary = Neutral0,
    tertiaryContainer = Color(0xFFD8F6EC),
    onTertiaryContainer = Neutral900,
    background = SurfaceLight,
    onBackground = Neutral900,
    surface = Neutral0,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral700,
    surfaceTint = BrandBlue,
    outline = Neutral200,
    outlineVariant = Neutral100,
    scrim = Color(0x66000000),
    inverseSurface = Neutral900,
    inverseOnSurface = Neutral0,
    error = BrandRed,
    onError = Neutral0,
    errorContainer = Color(0xFFFFE1E1),
    onErrorContainer = Neutral900
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    error = ErrorRed,
    onError = Color.White,
    tertiary = SuccessGreen // Using tertiary for success/green highlights
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    error = ErrorRed,
    onError = Color.White,
    tertiary = SuccessGreen
)

@Composable
fun RenCarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled dynamic colors to enforce branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = Typography,
        content = content
    )
}
