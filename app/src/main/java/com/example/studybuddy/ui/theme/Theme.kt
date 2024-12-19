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
private val RedColorScheme = lightColorScheme(
    primary = Color(0xFFCC1F03), // New primary color
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFB4A0), // Lighter tint of the primary color
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF9E1A02), // A slightly darker version of the primary
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD3), // Light tint of secondary
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFFD53C2E), // A medium-tone complement
    onTertiary = Color.White,
    background = Color(0xFFFFF3F2), // Light background tone
    onBackground = Color.Black,
    surface = Color(0xFFFFF3F2),
    onSurface = Color.Black,
    error = Color(0xFFD32F2F), // Keeping error color unchanged
    onError = Color.White,
    outline = Color(0xFFCC5A4D), // Slightly desaturated primary for outlines
    surfaceVariant = Color(0xFFFFE4E0), // Lighter variant for surfaces
    onSurfaceVariant = Color.Black,
    inverseSurface = Color(0xFF730000), // Darker inverse surface
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFFFF6B4A) // Bright complementary tone for inverse primary
)


@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = RedColorScheme,
        typography = Typography,
        content = content
    )
}