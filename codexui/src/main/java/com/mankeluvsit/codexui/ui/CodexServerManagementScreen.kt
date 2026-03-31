package com.mankeluvsit.codexui.ui

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CodexServerManagementScreen(viewModel: CodexServerViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var gatewayHost by remember(state.config.gatewayHost) { mutableStateOf(state.config.gatewayHost) }
    var gatewayPort by remember(state.config.gatewayPort) { mutableStateOf(state.config.gatewayPort.toString()) }
    var proxyEnabled by remember(state.config.proxyEnabled) { mutableStateOf(state.config.proxyEnabled) }
    var proxyHost by remember(state.config.proxyHost) { mutableStateOf(state.config.proxyHost) }
    var proxyPort by remember(state.config.proxyPort) { mutableStateOf(state.config.proxyPort.toString()) }
    var publicUrl by remember(state.config.publicUrl) { mutableStateOf(state.config.publicUrl) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("CodexUI Server Management", style = MaterialTheme.typography.headlineSmall)
            Text("State: ${if (state.running) "Running" else "Stopped"}")
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = gatewayHost,
                        onValueChange = { gatewayHost = it },
                        label = { Text("Gateway Host") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = gatewayPort,
                        onValueChange = { gatewayPort = it },
                        label = { Text("Gateway Port") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = proxyEnabled, onCheckedChange = { proxyEnabled = it })
                        Text("Enable Proxy")
                    }
                    OutlinedTextField(
                        value = proxyHost,
                        onValueChange = { proxyHost = it },
                        label = { Text("Proxy Host") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = proxyPort,
                        onValueChange = { proxyPort = it },
                        label = { Text("Proxy Port") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = publicUrl,
                        onValueChange = { publicUrl = it },
                        label = { Text("Public URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.saveConfig(
                                gatewayHost = gatewayHost,
                                gatewayPort = gatewayPort,
                                proxyEnabled = proxyEnabled,
                                proxyHost = proxyHost,
                                proxyPort = proxyPort,
                                publicUrl = publicUrl
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Configuration")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.startServer() }) { Text("Start") }
                Button(onClick = { viewModel.stopServer() }) { Text("Stop") }
                Button(onClick = { viewModel.triggerOperation("Health Check") }) { Text("Health Check") }
            }
        }

        item {
            Text("Server Logs", style = MaterialTheme.typography.titleMedium)
        }

        items(state.logs.reversed()) { log ->
            Text(log, fontFamily = FontFamily.Monospace)
        }
    }
}
