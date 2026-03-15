package com.furkanyildirim.learningcompose.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBlack,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = CyberLight,
    secondary = HotMagenta,
    onSecondary = CyberBlack,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = CyberLight,
    tertiary = NeonLime,
    onTertiary = CyberBlack,
    background = CyberBlack,
    onBackground = CyberLight,
    surface = CyberNight,
    onSurface = CyberLight,
    surfaceVariant = CyberSurface,
    onSurfaceVariant = Color(0xFFBCC9E8),
    error = AlertRed,
    onError = CyberBlack
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = CyberLight,
    primaryContainer = NeonCyan,
    onPrimaryContainer = CyberBlack,
    secondary = HotMagenta,
    onSecondary = CyberLight,
    secondaryContainer = Color(0xFFFFD2F7),
    onSecondaryContainer = Color(0xFF3A0030),
    tertiary = Color(0xFF4E6B00),
    onTertiary = CyberLight,
    background = Color(0xFFF2F6FF),
    onBackground = CyberBlack,
    surface = Color(0xFFEAF0FF),
    onSurface = CyberBlack,
    surfaceVariant = Color(0xFFDCE5FF),
    onSurfaceVariant = Color(0xFF334062),
    error = AlertRed,
    onError = CyberLight
)

@Composable
fun LearningComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
