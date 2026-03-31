package com.mankeluvsit.kaicodexui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceKind
import com.mankeluvsit.kaicodexui.data.ServiceRuntimeStatus
import com.mankeluvsit.kaicodexui.data.ServiceState
import com.mankeluvsit.kaicodexui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenCodex: () -> Unit,
    onOpenClaw: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refreshStatuses() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Kai Status Dashboard", style = MaterialTheme.typography.headlineMedium)
        state.globalError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        ServiceCard(
            title = "CodexUI",
            kind = ServiceKind.CODEX_UI,
            config = state.codexConfig,
            status = state.codexStatus,
            onSave = viewModel::saveConfig,
            onStart = viewModel::startService,
            onStop = viewModel::stopService,
            onOpenBrowser = onOpenCodex
        )
        ServiceCard(
            title = "OpenClaw",
            kind = ServiceKind.OPENCLAW,
            config = state.openClawConfig,
            status = state.openClawStatus,
            onSave = viewModel::saveConfig,
            onStart = viewModel::startService,
            onStop = viewModel::stopService,
            onOpenBrowser = onOpenClaw
        )
    }
}

@Composable
private fun ServiceCard(
    title: String,
    kind: ServiceKind,
    config: ServiceConfig,
    status: ServiceRuntimeStatus,
    onSave: (ServiceKind, ServiceConfig) -> Unit,
    onStart: (ServiceKind) -> Unit,
    onStop: (ServiceKind) -> Unit,
    onOpenBrowser: () -> Unit
) {
    var baseUrl by remember(config.baseUrl) { mutableStateOf(config.baseUrl) }
    var port by remember(config.port) { mutableStateOf(config.port.toString()) }
    var token by remember(config.token) { mutableStateOf(config.token) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            StatusBadge(status.state)
            Text("Gateway: ${status.gatewayStatus ?: "unknown"}")
            Text("Version: ${status.version ?: "-"}")
            Text("Uptime: ${status.uptimeSeconds ?: 0}s")
            Text("Active connections: ${status.activeConnections ?: 0}")
            status.lastError?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = port, onValueChange = { port = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("API token") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    onSave(kind, ServiceConfig(baseUrl = baseUrl, port = port.toIntOrNull() ?: config.port, token = token))
                }) { Text("Save") }
                Button(onClick = { onStart(kind) }) { Text("Start") }
                Button(onClick = { onStop(kind) }) { Text("Stop") }
                Button(onClick = onOpenBrowser) { Text("Open in Browser") }
            }
        }
    }
}

@Composable
private fun StatusBadge(state: ServiceState) {
    val color = when (state) {
        ServiceState.RUNNING -> Color(0xFF2E7D32)
        ServiceState.STARTING -> Color(0xFFEF6C00)
        ServiceState.STOPPED -> Color(0xFF616161)
        ServiceState.ERROR -> Color(0xFFC62828)
        ServiceState.UNKNOWN -> Color(0xFF1565C0)
    }
    Text(
        text = "State: $state",
        color = Color.White,
        modifier = Modifier.background(color).padding(horizontal = 8.dp, vertical = 4.dp)
    )
    Spacer(Modifier.height(4.dp))
}
