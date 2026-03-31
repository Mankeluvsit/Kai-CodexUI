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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mankeluvsit.codexui.data.CodexUiController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CodexUiManagementPanel(
    controller: CodexUiController,
    modifier: Modifier = Modifier
) {
    val settings by controller.settings.collectAsStateWithLifecycle()
    val status by controller.status.collectAsStateWithLifecycle()

    var gatewayInput by remember(settings.gateway) { mutableStateOf(settings.gateway) }
    var proxyInput by remember(settings.proxy) { mutableStateOf(settings.proxy) }
    var portInput by remember(settings.port) { mutableStateOf(settings.port.toString()) }
    var urlInput by remember(settings.url) { mutableStateOf(settings.url) }

    val timestamp = DateTimeFormatter.ISO_LOCAL_TIME
        .format(Instant.ofEpochMilli(status.lastUpdatedEpochMillis).atZone(ZoneId.systemDefault()))

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("CodexUI Service Management", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Server state: ${if (status.running) "RUNNING" else "STOPPED"}", fontWeight = FontWeight.Bold)
                    Text("Last update: $timestamp")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = controller::startServer, enabled = !status.running) { Text("Start") }
                        Button(onClick = controller::stopServer, enabled = status.running) { Text("Stop") }
                        Button(onClick = controller::triggerHealthCheck) { Text("Health Check") }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gatewayInput,
                        onValueChange = {
                            gatewayInput = it
                            controller.updateGateway(it)
                        },
                        label = { Text("Gateway") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = proxyInput,
                        onValueChange = {
                            proxyInput = it
                            controller.updateProxy(it)
                        },
                        label = { Text("Proxy") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = portInput,
                        onValueChange = {
                            portInput = it
                            it.toIntOrNull()?.let(controller::updatePort)
                        },
                        label = { Text("Port") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = {
                            urlInput = it
                            controller.updateUrl(it)
                        },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Text("Logs", style = MaterialTheme.typography.titleMedium)
        }

        items(status.logs.reversed()) { logLine ->
            Text(text = logLine, style = MaterialTheme.typography.bodySmall)
        }
    }
}
