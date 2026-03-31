package com.mankeluvsit.codexui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CodexUiViewModel : ViewModel() {
    private val manager = CodexUiManager(
        initialConfig = CodexUiConfig(
            gateway = "local",
            proxy = "none",
            port = 18923,
            url = "http://localhost"
        )
    )

    val config = manager.config
    val state = manager.state
    val logs = manager.logs

    private val _editingConfig = MutableStateFlow(config.value)
    val editingConfig: StateFlow<CodexUiConfig> = _editingConfig.asStateFlow()

    fun onGatewayChange(value: String) {
        _editingConfig.value = _editingConfig.value.copy(gateway = value)
    }

    fun onProxyChange(value: String) {
        _editingConfig.value = _editingConfig.value.copy(proxy = value)
    }

    fun onPortChange(value: String) {
        val port = value.toIntOrNull() ?: return
        _editingConfig.value = _editingConfig.value.copy(port = port)
    }

    fun onUrlChange(value: String) {
        _editingConfig.value = _editingConfig.value.copy(url = value)
    }

    fun applyConfiguration() {
        manager.updateConfig(_editingConfig.value)
    }

    fun startServer() = manager.start()

    fun stopServer() = manager.stop()

    fun triggerOperation() = manager.triggerSyncOperation()
}
