package com.example.composeeffectlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LabColors = lightColorScheme(
    primary = Color(0xFF3657B3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE1FF),
    onPrimaryContainer = Color(0xFF08164B),
    secondary = Color(0xFF56618A),
    secondaryContainer = Color(0xFFDDE1FF),
    background = Color(0xFFF9F9FF),
    surface = Color(0xFFF9F9FF),
    surfaceVariant = Color(0xFFE2E2EC),
)

@Composable
fun ComposeEffectLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LabColors,
        content = content,
    )
}

