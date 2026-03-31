package com.mankeluvsit.kaicodexui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceLifecycle
import com.mankeluvsit.kaicodexui.data.ServiceStatus
import com.mankeluvsit.kaicodexui.data.ServiceType
import com.mankeluvsit.kaicodexui.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDashboardScreen(
    viewModel: DashboardViewModel,
    onOpenBrowser: (ServiceType) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Status Dashboard") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.configs, key = { it.type.name }) { config ->
                ServiceCard(
                    config = config,
                    status = state.statuses[config.type],
                    onSave = viewModel::updateConfig,
                    onStart = { viewModel.startService(config) },
                    onStop = { viewModel.stopService(config) },
                    onOpenBrowser = { onOpenBrowser(config.type) }
                )
            }
        }
    }
}

@Composable
private fun ServiceCard(
    config: ServiceConfig,
    status: ServiceStatus?,
    onSave: (ServiceConfig) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onOpenBrowser: () -> Unit
) {
    var baseUrl by remember(config.type) { mutableStateOf(config.baseUrl) }
    var port by remember(config.type) { mutableStateOf(config.port.toString()) }
    var token by remember(config.type) { mutableStateOf(config.token) }

    LaunchedEffect(config) {
        baseUrl = config.baseUrl
        port = config.port.toString()
        token = config.token
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(config.displayName, style = MaterialTheme.typography.titleLarge)
            StatusIndicator(status)

            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = port,
                onValueChange = { port = it.filter(Char::isDigit) },
                label = { Text("Port") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Token") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    onSave(
                        config.copy(
                            baseUrl = baseUrl,
                            port = port.toIntOrNull() ?: config.port,
                            token = token
                        )
                    )
                }) { Text("Save") }
                Button(onClick = onStart) { Text("Start") }
                Button(onClick = onStop) { Text("Stop") }
                Button(onClick = onOpenBrowser) { Text("Open in Browser") }
            }

            if (status?.metadata?.isNotEmpty() == true) {
                Text("Metadata", style = MaterialTheme.typography.titleMedium)
                status.metadata.forEach { (key, value) ->
                    Text("$key: $value", style = MaterialTheme.typography.bodySmall)
                }
            }

            status?.errorMessage?.let { message ->
                Text("Error: $message", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: ServiceStatus?) {
    val lifecycle = status?.lifecycle ?: ServiceLifecycle.UNKNOWN
    val color = when (lifecycle) {
        ServiceLifecycle.RUNNING -> Color(0xFF1B5E20)
        ServiceLifecycle.STARTING -> Color(0xFFEF6C00)
        ServiceLifecycle.STOPPED -> Color(0xFF616161)
        ServiceLifecycle.ERROR -> Color(0xFFB71C1C)
        ServiceLifecycle.UNKNOWN -> Color(0xFF546E7A)
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .height(12.dp)
                .background(color, shape = MaterialTheme.shapes.small)
                .fillMaxWidth(0.1f)
        )
        Text(lifecycle.name)
    }
}
