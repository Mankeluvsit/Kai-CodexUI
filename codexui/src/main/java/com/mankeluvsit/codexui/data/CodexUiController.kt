package com.mankeluvsit.codexui.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CodexUiController {
    private val _settings = MutableStateFlow(CodexUiSettings())
    val settings: StateFlow<CodexUiSettings> = _settings.asStateFlow()

    private val _status = MutableStateFlow(CodexUiStatus())
    val status: StateFlow<CodexUiStatus> = _status.asStateFlow()

    fun updateGateway(gateway: String) {
        _settings.update { it.copy(gateway = gateway) }
        appendLog("Gateway updated to '$gateway'.")
    }

    fun updateProxy(proxy: String) {
        _settings.update { it.copy(proxy = proxy) }
        appendLog("Proxy updated to '$proxy'.")
    }

    fun updatePort(port: Int) {
        val resolvedUrl = _settings.value.url.substringBeforeLast(':') + ":$port"
        _settings.update { it.copy(port = port, url = resolvedUrl) }
        appendLog("Port updated to '$port'.")
    }

    fun updateUrl(url: String) {
        _settings.update { it.copy(url = url) }
        appendLog("URL updated to '$url'.")
    }

    fun startServer() {
        _status.update {
            it.copy(
                running = true,
                lastUpdatedEpochMillis = System.currentTimeMillis(),
                logs = it.logs + "Server started for ${_settings.value.url}."
            )
        }
    }

    fun stopServer() {
        _status.update {
            it.copy(
                running = false,
                lastUpdatedEpochMillis = System.currentTimeMillis(),
                logs = it.logs + "Server stopped."
            )
        }
    }

    fun triggerHealthCheck() {
        appendLog("Health-check requested against ${_settings.value.url}/health.")
    }

    private fun appendLog(message: String) {
        _status.update {
            it.copy(
                lastUpdatedEpochMillis = System.currentTimeMillis(),
                logs = (it.logs + message).takeLast(100)
            )
        }
    }
}
