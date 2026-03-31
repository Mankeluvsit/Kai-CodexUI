package com.mankeluvsit.codexui

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CodexUiEmbeddedPlaceholder(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text("CodexUI module linked. Render remote UI via app WebView/sessions.")
    }
}
