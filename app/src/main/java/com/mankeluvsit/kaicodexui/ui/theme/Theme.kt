package com.mankeluvsit.kaicodexui.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Light = lightColorScheme()
private val Dark = darkColorScheme()

@Composable
fun KaiCodexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Light,
        content = content
    )
}
