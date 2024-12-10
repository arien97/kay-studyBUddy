package com.example.studybuddy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = lightColorScheme(
    primary = Color(0xFF3477E6),
    secondary = Color(0xFF4091E6),
    tertiary = Color(0xFF8FD0F4),
)

/*
I want pink -Yuting
 */
private val PinkColorScheme = lightColorScheme(
    primary = Color(0xFFF06292),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFC1E3),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFFEC407A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFCDD2),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFFF48FB1),
    onTertiary = Color.Black,
    background = Color(0xFFFFEBEE),
    onBackground = Color.Black,
    surface = Color(0xFFFFEBEE),
    onSurface = Color.Black,
    error = Color(0xFFD32F2F),
    onError = Color.White,
    outline = Color(0xFFE91E63),
    surfaceVariant = Color(0xFFFFE4E6),
    onSurfaceVariant = Color.Black,
    inverseSurface = Color(0xFF880E4F),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFFD81B60)
)


@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}