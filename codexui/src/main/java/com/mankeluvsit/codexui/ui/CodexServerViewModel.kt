package com.mankeluvsit.codexui.ui

import androidx.lifecycle.ViewModel
import com.mankeluvsit.codexui.model.CodexServerConfig
import com.mankeluvsit.codexui.service.CodexServerController
import kotlinx.coroutines.flow.StateFlow

class CodexServerViewModel(
    private val controller: CodexServerController = CodexServerController()
) : ViewModel() {
    val state: StateFlow<com.mankeluvsit.codexui.model.CodexServerState> = controller.state

    fun startServer() = controller.startServer()
    fun stopServer() = controller.stopServer()
    fun triggerOperation(name: String) = controller.triggerOperation(name)

    fun saveConfig(
        gatewayHost: String,
        gatewayPort: String,
        proxyEnabled: Boolean,
        proxyHost: String,
        proxyPort: String,
        publicUrl: String
    ) {
        val normalizedGatewayPort = gatewayPort.toIntOrNull() ?: 18923
        val normalizedProxyPort = proxyPort.toIntOrNull() ?: 0
        controller.updateConfig(
            CodexServerConfig(
                gatewayHost = gatewayHost.ifBlank { "127.0.0.1" },
                gatewayPort = normalizedGatewayPort,
                proxyEnabled = proxyEnabled,
                proxyHost = proxyHost,
                proxyPort = normalizedProxyPort,
                publicUrl = publicUrl.ifBlank { "http://127.0.0.1:$normalizedGatewayPort" }
            )
        )
    }
}
