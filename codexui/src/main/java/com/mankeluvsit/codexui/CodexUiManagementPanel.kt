package com.mankeluvsit.codexui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CodexUiManagementPanel(
    viewModel: CodexUiViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.editingConfig.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("CodexUI Server Management", style = MaterialTheme.typography.headlineSmall)
        Text("Current state: $state", style = MaterialTheme.typography.bodyLarge)

        OutlinedTextField(
            value = config.gateway,
            onValueChange = viewModel::onGatewayChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Gateway") }
        )
        OutlinedTextField(
            value = config.proxy,
            onValueChange = viewModel::onProxyChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Proxy") }
        )
        OutlinedTextField(
            value = config.port.toString(),
            onValueChange = viewModel::onPortChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Port") }
        )
        OutlinedTextField(
            value = config.url,
            onValueChange = viewModel::onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Server URL") }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::applyConfiguration) { Text("Apply") }
            Button(onClick = viewModel::startServer) { Text("Start") }
            Button(onClick = viewModel::stopServer) { Text("Stop") }
            Button(onClick = viewModel::triggerOperation) { Text("Sync") }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier.padding(12.dp)) {
                items(logs) { log ->
                    Text(log, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
