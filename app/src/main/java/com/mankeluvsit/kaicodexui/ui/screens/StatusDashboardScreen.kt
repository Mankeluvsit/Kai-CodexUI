package com.mankeluvsit.kaicodexui.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mankeluvsit.kaicodexui.data.DashboardState
import com.mankeluvsit.kaicodexui.data.ServiceConfig

@Composable
fun StatusDashboardScreen(
    state: DashboardState,
    onSaveCodex: (ServiceConfig) -> Unit,
    onSaveClaw: (ServiceConfig) -> Unit,
    onStart: (ServiceConfig) -> Unit,
    onStop: (ServiceConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Status Dashboard", style = MaterialTheme.typography.headlineSmall)
        ServiceCard(state.codexUiConfig, state.codexUiStatus.message, onSaveCodex, onStart, onStop)
        ServiceCard(state.openClawConfig, state.openClawStatus.message, onSaveClaw, onStart, onStop)
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun ServiceCard(
    config: ServiceConfig,
    statusMessage: String,
    onSave: (ServiceConfig) -> Unit,
    onStart: (ServiceConfig) -> Unit,
    onStop: (ServiceConfig) -> Unit
) {
    val context = LocalContext.current
    var baseUrl by remember(config.name) { mutableStateOf(config.baseUrl) }
    var port by remember(config.name) { mutableStateOf(config.port.toString()) }
    var token by remember(config.name) { mutableStateOf(config.token) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(config.name, style = MaterialTheme.typography.titleLarge)
            Text("Status: $statusMessage")
            OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("Base URL") })
            OutlinedTextField(value = port, onValueChange = { port = it }, label = { Text("Port") })
            OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("API Token") })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val updated = config.copy(baseUrl = baseUrl.trim(), port = port.toIntOrNull() ?: config.port, token = token.trim())
                    onSave(updated)
                }) { Text("Save") }
                Button(onClick = { onStart(config) }) { Text("Start") }
                Button(onClick = { onStop(config) }) { Text("Stop") }
                Button(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(config.endpoint())))
                }) { Text("Open in Browser") }
            }
        }
    }
}
