package com.mankeluvsit.kaicodexui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mankeluvsit.kaicodexui.data.ServiceConfigStore
import com.mankeluvsit.kaicodexui.model.DashboardServiceUiModel
import com.mankeluvsit.kaicodexui.model.ServiceConfig
import com.mankeluvsit.kaicodexui.model.ServiceRunState
import com.mankeluvsit.kaicodexui.network.ServiceApiClient
import com.mankeluvsit.kaicodexui.repository.DashboardRepository
import com.mankeluvsit.kaicodexui.ui.theme.KaiTheme
import com.mankeluvsit.kaicodexui.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = DashboardRepository(ServiceConfigStore(applicationContext), ServiceApiClient())

        setContent {
            KaiTheme {
                val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(repository))
                DashboardRoute(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardRoute(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Dashboard") },
                actions = {
                    TextButton(onClick = viewModel::refreshNow) {
                        Text("Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.services) { service ->
                    ServiceCard(
                        service = service,
                        onSave = { viewModel.onSaveConfig(service.type, it) },
                        onStart = { viewModel.onStartService(service.type) },
                        onStop = { viewModel.onStopService(service.type) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: DashboardServiceUiModel,
    onSave: (ServiceConfig) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val context = LocalContext.current
    var baseUrl by remember(service.type, service.config.baseUrl) { mutableStateOf(service.config.baseUrl) }
    var portText by remember(service.type, service.config.port) { mutableStateOf(service.config.port.toString()) }
    var token by remember(service.type, service.config.token) { mutableStateOf(service.config.token) }

    LaunchedEffect(service.config) {
        baseUrl = service.config.baseUrl
        portText = service.config.port.toString()
        token = service.config.token
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(service.type.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor = when (service.status.state) {
                    ServiceRunState.RUNNING -> Color(0xFF2E7D32)
                    ServiceRunState.STARTING -> Color(0xFFEF6C00)
                    ServiceRunState.STOPPED -> Color(0xFF757575)
                    ServiceRunState.ERROR -> Color(0xFFC62828)
                    ServiceRunState.UNKNOWN -> Color(0xFF546E7A)
                }
                AssistChip(
                    onClick = { },
                    label = { Text(service.status.state.name) }
                )
                Spacer(Modifier.width(8.dp))
                Text(text = service.status.message, color = statusColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = portText, onValueChange = { portText = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("Token") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onStart, modifier = Modifier.weight(1f)) { Text("Start") }
                Button(onClick = onStop, modifier = Modifier.weight(1f)) { Text("Stop") }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val address = "${baseUrl.removeSuffix("/")}:${portText}"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(address)))
                    }
                ) { Text("Open in Browser") }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onSave(
                        ServiceConfig(
                            baseUrl = baseUrl,
                            port = portText.toIntOrNull() ?: service.config.port,
                            token = token
                        )
                    )
                }
            ) { Text("Save Connection") }

            service.status.version?.let { Text("Version: $it") }
            service.status.uptime?.let { Text("Uptime: $it") }
            service.status.activeConnections?.let { Text("Connections: $it") }
            if (service.status.extraMetadata.isNotEmpty()) {
                Text("Metadata", fontWeight = FontWeight.Bold)
                service.status.extraMetadata.forEach { (k, v) ->
                    Text("$k: $v")
                }
            }
        }
    }
}
