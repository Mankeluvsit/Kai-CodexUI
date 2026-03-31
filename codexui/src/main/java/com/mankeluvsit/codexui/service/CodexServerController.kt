package com.mankeluvsit.codexui.service

import com.mankeluvsit.codexui.model.CodexServerConfig
import com.mankeluvsit.codexui.model.CodexServerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

class CodexServerController {
    private val _state = MutableStateFlow(CodexServerState())
    val state: StateFlow<CodexServerState> = _state.asStateFlow()

    fun startServer() {
        updateState {
            it.copy(running = true, logs = appendLog(it.logs, "Server started on ${it.config.publicUrl}"))
        }
    }

    fun stopServer() {
        updateState {
            it.copy(running = false, logs = appendLog(it.logs, "Server stopped"))
        }
    }

    fun updateConfig(config: CodexServerConfig) {
        updateState {
            it.copy(config = config, logs = appendLog(it.logs, "Configuration updated: ${config.publicUrl}"))
        }
    }

    fun triggerOperation(name: String) {
        updateState {
            it.copy(logs = appendLog(it.logs, "Operation '$name' triggered"))
        }
    }

    private fun updateState(reducer: (CodexServerState) -> CodexServerState) {
        _state.value = reducer(_state.value)
    }

    private fun appendLog(existing: List<String>, message: String): List<String> {
        val entry = "${Instant.now()} | $message"
        return (existing + entry).takeLast(200)
    }
}
