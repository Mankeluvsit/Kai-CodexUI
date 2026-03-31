package com.mankeluvsit.kaicodexui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mankeluvsit.codexui.CodexUiViewModel

@Composable
fun KaiHomeScreen(
    onManageCodexUi: () -> Unit,
    codexViewModel: CodexUiViewModel,
    modifier: Modifier = Modifier
) {
    val state by codexViewModel.state.collectAsStateWithLifecycle()
    val config by codexViewModel.config.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Kai Integration Hub", style = MaterialTheme.typography.headlineMedium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("CodexUI status: $state")
                Text("Gateway: ${config.gateway}")
                Text("Proxy: ${config.proxy}")
                Text("Endpoint: ${config.url}:${config.port}")
            }
        }
        Button(onClick = onManageCodexUi) {
            Text("Open CodexUI Controls")
        }
        Button(onClick = codexViewModel::triggerOperation) {
            Text("Trigger CodexUI Operation")
        }
    }
}
