package com.mankeluvsit.codexui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CodexUiControlViewModel(
    private val controller: CodexUiServiceController = CodexUiServiceController()
) : ViewModel() {

    private val _state = MutableStateFlow(CodexUiState())
    val state: StateFlow<CodexUiState> = _state.asStateFlow()

    fun updateGateway(value: String) = updateConfig { copy(gateway = value) }
    fun updateProxy(value: String) = updateConfig { copy(proxy = value) }
    fun updatePort(value: String) {
        val parsed = value.toIntOrNull() ?: return
        updateConfig { copy(port = parsed) }
    }

    fun updateBaseUrl(value: String) = updateConfig { copy(baseUrl = value) }
    fun setTunnelEnabled(enabled: Boolean) = updateConfig { copy(tunnelEnabled = enabled) }

    fun startServer() {
        _state.update { it.copy(serverStatus = ServerStatus.STARTING) }
        viewModelScope.launch {
            _state.update { controller.start(it) }
        }
    }

    fun stopServer() {
        _state.update { it.copy(serverStatus = ServerStatus.STOPPING) }
        viewModelScope.launch {
            _state.update { controller.stop(it) }
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            _state.update { controller.triggerOperation(it, "sync-threads") }
        }
    }

    fun refreshStatus() {
        _state.update {
            it.copy(latestLogLines = it.latestLogLines + "[status] status refreshed from Kai panel")
        }
    }

    private fun updateConfig(update: CodexUiConfig.() -> CodexUiConfig) {
        _state.update { current ->
            val updatedConfig = current.config.update()
            current.copy(
                config = updatedConfig,
                activeUrl = "${updatedConfig.baseUrl}:${updatedConfig.port}",
                latestLogLines = current.latestLogLines + "[config] updated"
            )
        }
    }
}
