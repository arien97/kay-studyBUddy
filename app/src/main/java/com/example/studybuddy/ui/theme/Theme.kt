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