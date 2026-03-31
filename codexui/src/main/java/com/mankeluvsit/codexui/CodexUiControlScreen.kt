package com.mankeluvsit.codexui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodexUiControlScreen(viewModel: CodexUiControlViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var gateway by rememberSaveable { mutableStateOf(state.config.gateway) }
    var proxy by rememberSaveable { mutableStateOf(state.config.proxy) }
    var port by rememberSaveable { mutableStateOf(state.config.port.toString()) }
    var baseUrl by rememberSaveable { mutableStateOf(state.config.baseUrl) }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("CodexUI Server Management", style = MaterialTheme.typography.titleLarge)
            Text("Manage gateway, proxy, ports, URL and runtime operations without leaving Kai.")
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = gateway,
                        onValueChange = {
                            gateway = it
                            viewModel.updateGateway(it)
                        },
                        label = { Text("Gateway") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = proxy,
                        onValueChange = {
                            proxy = it
                            viewModel.updateProxy(it)
                        },
                        label = { Text("Proxy") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = {
                            port = it
                            viewModel.updatePort(it)
                        },
                        label = { Text("Port") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = {
                            baseUrl = it
                            viewModel.updateBaseUrl(it)
                        },
                        label = { Text("Base URL") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.config.tunnelEnabled,
                            onClick = { viewModel.setTunnelEnabled(!state.config.tunnelEnabled) },
                            label = { Text("Cloudflare Tunnel") }
                        )
                        FilterChip(
                            selected = !state.config.tunnelEnabled,
                            onClick = { viewModel.setTunnelEnabled(false) },
                            label = { Text("No Tunnel") }
                        )
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Server status: ${state.serverStatus}")
                    Text("Active URL: ${state.activeUrl}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = viewModel::startServer) { Text("Start") }
                        Button(onClick = viewModel::stopServer) { Text("Stop") }
                        Button(onClick = viewModel::refreshStatus) { Text("Refresh") }
                        Button(onClick = viewModel::triggerSync) { Text("Trigger Sync") }
                    }
                }
            }
        }

        item {
            Text("Runtime Logs", style = MaterialTheme.typography.titleMedium)
        }

        items(state.latestLogLines.takeLast(40)) { line ->
            Text(text = line, style = MaterialTheme.typography.bodySmall)
        }
    }
}
