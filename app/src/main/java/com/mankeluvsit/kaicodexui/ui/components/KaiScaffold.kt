package com.mankeluvsit.kaicodexui.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun KaiScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val items = listOf(
        "dashboard" to "Status",
        "codexui" to "CodexUI",
        "openclaw" to "OpenClaw"
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { (route, label) ->
                    NavigationBarItem(
                        selected = route == currentRoute,
                        onClick = { onNavigate(route) },
                        icon = {
                            Icon(
                                imageVector = if (route == "dashboard") Icons.Default.Dashboard else Icons.Default.Language,
                                contentDescription = label
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }
        },
        content = content
    )
}
