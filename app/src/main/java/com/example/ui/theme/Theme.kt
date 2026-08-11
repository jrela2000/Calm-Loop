package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = SoftBlue,
    secondary = Sage,
    tertiary = Lavender,
    background = DarkSlateBackground,
    surface = DarkSlateSurface,
    onPrimary = SlateTextPrimary,
    onSecondary = SlateTextPrimary,
    onTertiary = SlateTextPrimary,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outlineVariant = TextSecondaryDark.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBlueDark,
    secondary = SageDark,
    tertiary = LavenderDark,
    background = WarmWhite,
    surface = WarmCardLight,
    onPrimary = WarmWhite,
    onSecondary = WarmWhite,
    onTertiary = WarmWhite,
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    onSurfaceVariant = SlateTextSecondary,
    outlineVariant = SlateTextMuted.copy(alpha = 0.4f)
)

val CalmShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun CalmLoopTheme(
    darkTheme: Boolean = false,
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
        shapes = CalmShapes,
        typography = Typography,
        content = content
    )
}
