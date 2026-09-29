package com.microbond.app.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MicrobondDarkColorScheme = darkColorScheme(
    primary = MicrobondActiveGreen,
    secondary = MicrobondActivePillDark,
    background = MicrobondDarkBackground,
    surface = MicrobondDarkBackground,
    onBackground = MicrobondTextPrimary,
    onSurface = MicrobondInactiveGray,
)

@Composable
fun MicrobondTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MicrobondDarkBackground.toArgb()
            window.navigationBarColor = MicrobondDarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = MicrobondDarkColorScheme,
        typography = Typography,
        content = content
    )
}
