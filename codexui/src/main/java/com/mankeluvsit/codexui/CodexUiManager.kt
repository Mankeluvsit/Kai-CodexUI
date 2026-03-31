package com.mankeluvsit.codexui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CodexUiManager(initialConfig: CodexUiConfig) {
    private val _config = MutableStateFlow(initialConfig)
    val config: StateFlow<CodexUiConfig> = _config.asStateFlow()

    private val _state = MutableStateFlow(ServerState.STOPPED)
    val state: StateFlow<ServerState> = _state.asStateFlow()

    private val _logs = MutableStateFlow(listOf("CodexUI manager initialized."))
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    fun updateConfig(config: CodexUiConfig) {
        _config.value = config
        appendLog("Configuration updated: $config")
    }

    fun start() {
        _state.value = ServerState.STARTING
        appendLog("Starting codexUI server at ${_config.value.url}:${_config.value.port}...")
        _state.value = ServerState.RUNNING
        appendLog("codexUI server is running.")
    }

    fun stop() {
        _state.value = ServerState.STOPPING
        appendLog("Stopping codexUI server...")
        _state.value = ServerState.STOPPED
        appendLog("codexUI server stopped.")
    }

    fun triggerSyncOperation() {
        appendLog("Triggered codexUI sync operation through Kai.")
    }

    private fun appendLog(message: String) {
        _logs.value = _logs.value + "[${System.currentTimeMillis()}] $message"
    }
}
