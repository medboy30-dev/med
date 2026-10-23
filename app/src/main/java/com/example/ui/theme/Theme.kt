package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = ElectricCyan,
  onPrimary = NavyDark,
  primaryContainer = DeepViolet,
  onPrimaryContainer = TextPrimary,
  secondary = NeonPurple,
  onSecondary = TextPrimary,
  secondaryContainer = NavyCardElevated,
  onSecondaryContainer = ElectricCyan,
  tertiary = GoldAmber,
  onTertiary = NavyDark,
  background = NavyBackground,
  onBackground = TextPrimary,
  surface = NavySurface,
  onSurface = TextPrimary,
  surfaceVariant = NavyCard,
  onSurfaceVariant = TextSecondary,
  outline = NavyCardBorder,
  error = WrongRose,
  onError = TextPrimary
)

@Composable
fun MindRushTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
