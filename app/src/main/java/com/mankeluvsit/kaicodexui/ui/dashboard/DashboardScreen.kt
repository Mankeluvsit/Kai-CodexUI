package com.mankeluvsit.kaicodexui.ui.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceState
import com.mankeluvsit.kaicodexui.data.ServiceType
import com.mankeluvsit.kaicodexui.data.ServiceUiState

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, contentPadding: PaddingValues) {
    val codexState by viewModel.codexState.collectAsStateWithLifecycle()
    val openClawState by viewModel.openClawState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Status Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Manage CodexUI and OpenClaw service endpoints, tokens, and runtime status.")

        ServiceCard(
            name = "CodexUI",
            type = ServiceType.CODEX_UI,
            state = codexState,
            onSave = viewModel::saveConfig,
            onStart = viewModel::start,
            onStop = viewModel::stop,
            onRefresh = viewModel::refresh
        )

        ServiceCard(
            name = "OpenClaw",
            type = ServiceType.OPEN_CLAW,
            state = openClawState,
            onSave = viewModel::saveConfig,
            onStart = viewModel::start,
            onStop = viewModel::stop,
            onRefresh = viewModel::refresh
        )
    }
}

@Composable
private fun ServiceCard(
    name: String,
    type: ServiceType,
    state: ServiceUiState,
    onSave: (ServiceType, ServiceConfig) -> Unit,
    onStart: (ServiceType) -> Unit,
    onStop: (ServiceType) -> Unit,
    onRefresh: (ServiceType) -> Unit
) {
    val context = LocalContext.current
    var baseUrl by remember(state.config.baseUrl) { mutableStateOf(state.config.baseUrl) }
    var port by remember(state.config.port) { mutableStateOf(state.config.port.toString()) }
    var token by remember(state.config.token) { mutableStateOf(state.config.token) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusIcon(state.status.state)
                Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(state.status.state.name)
            }
            state.status.lastError?.takeIf { it.isNotBlank() }?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
            OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = port, onValueChange = { port = it.filter(Char::isDigit) }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("API Token") }, modifier = Modifier.fillMaxWidth())

            Text("Version: ${state.status.version ?: "n/a"}")
            Text("Uptime: ${state.status.uptimeSeconds ?: 0}s")
            Text("Active connections: ${state.status.activeConnections ?: 0}")
            Text("Message: ${state.status.message.ifBlank { "No message" }}")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    onSave(type, ServiceConfig(baseUrl = baseUrl, port = port.toIntOrNull() ?: 0, token = token))
                }) { Text("Save") }
                Button(onClick = { onStart(type) }) { Text("Start") }
                Button(onClick = { onStop(type) }) { Text("Stop") }
                Button(onClick = { onRefresh(type) }) { Text("Refresh") }
            }

            Button(onClick = {
                val url = state.config.rootUrl
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }) { Text("Open in Browser") }
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
private fun StatusIcon(state: ServiceState) {
    when (state) {
        ServiceState.RUNNING -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        ServiceState.STARTING -> Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
        ServiceState.STOPPED -> Icon(Icons.Default.PauseCircle, contentDescription = null)
        ServiceState.ERROR -> Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
    }
}
