package com.vgtech.mobile.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VGTechColorScheme = lightColorScheme(
    primary            = Navy,
    onPrimary          = SurfaceWhite,
    primaryContainer   = NavyLight,
    onPrimaryContainer = SurfaceWhite,

    secondary          = Teal,
    onSecondary        = Navy,
    secondaryContainer = TealLight,
    onSecondaryContainer = Navy,

    tertiary           = Mustard,
    onTertiary         = Navy,
    tertiaryContainer  = Mustard.copy(alpha = 0.2f),
    onTertiaryContainer = Navy,

    background         = BackgroundLight,
    onBackground       = TextPrimary,
    surface            = SurfaceWhite,
    onSurface          = TextPrimary,
    surfaceVariant     = BackgroundLight,
    onSurfaceVariant   = TextMuted,

    error              = ErrorRed,
    onError            = SurfaceWhite,
    errorContainer     = ErrorBg,
    onErrorContainer   = ErrorRed,

    outline            = BorderColor,
    outlineVariant     = BorderColor.copy(alpha = 0.5f)
)

@Composable
fun VGTechTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = VGTechColorScheme

    // Status bar color — immersive Navy
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = Navy.toArgb()
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = VGTechTypography,
        shapes      = VGTechShapes,
        content     = content
    )
}
