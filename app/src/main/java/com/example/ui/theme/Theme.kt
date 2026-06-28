package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkVibrantPrimary,
    primaryContainer = DarkVibrantPrimaryContainer,
    onPrimaryContainer = DarkVibrantOnPrimaryContainer,
    secondary = DarkVibrantSecondary,
    secondaryContainer = DarkVibrantSecondaryContainer,
    onSecondaryContainer = DarkVibrantOnSecondaryContainer,
    tertiary = DarkVibrantTertiary,
    tertiaryContainer = DarkVibrantTertiaryContainer,
    onTertiaryContainer = DarkVibrantOnTertiaryContainer,
    background = DarkVibrantBackground,
    surface = DarkVibrantSurface,
    surfaceVariant = DarkVibrantSurfaceVariant,
    onBackground = DarkVibrantOnSurface,
    onSurface = DarkVibrantOnSurface,
    onSurfaceVariant = DarkVibrantOnSurfaceVariant,
    outline = DarkVibrantOutline,
    error = DarkVibrantError,
    errorContainer = DarkVibrantErrorContainer,
    onErrorContainer = DarkVibrantOnErrorContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantPrimary,
    primaryContainer = VibrantPrimaryContainer,
    onPrimaryContainer = VibrantOnPrimaryContainer,
    secondary = VibrantSecondary,
    secondaryContainer = VibrantSecondaryContainer,
    onSecondaryContainer = VibrantOnSecondaryContainer,
    tertiary = VibrantTertiary,
    tertiaryContainer = VibrantTertiaryContainer,
    onTertiaryContainer = VibrantOnTertiaryContainer,
    background = VibrantBackground,
    surface = VibrantSurface,
    surfaceVariant = VibrantSurfaceVariant,
    onBackground = VibrantOnSurface,
    onSurface = VibrantOnSurface,
    onSurfaceVariant = VibrantOnSurfaceVariant,
    outline = VibrantOutline,
    error = VibrantError,
    errorContainer = VibrantErrorContainer,
    onErrorContainer = VibrantOnErrorContainer
  )

// Quick helper to bypass androidx.compose.ui.graphics.Color reference inside Theme.kt since we declared them in Color.kt

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For finance apps, brand consistency is paramount, so we disable dynamicColor by default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
