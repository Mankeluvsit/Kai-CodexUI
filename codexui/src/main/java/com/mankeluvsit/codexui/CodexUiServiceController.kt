package com.mankeluvsit.codexui

import kotlinx.coroutines.delay

class CodexUiServiceController {
    suspend fun start(current: CodexUiState): CodexUiState {
        delay(250)
        val url = "${current.config.baseUrl}:${current.config.port}"
        return current.copy(
            serverStatus = ServerStatus.RUNNING,
            activeUrl = url,
            latestLogLines = current.latestLogLines + listOf(
                "[server] start requested",
                "[server] gateway=${current.config.gateway}, proxy=${current.config.proxy.ifBlank { "none" }}",
                "[server] running on $url"
            )
        )
    }

    suspend fun stop(current: CodexUiState): CodexUiState {
        delay(150)
        return current.copy(
            serverStatus = ServerStatus.STOPPED,
            latestLogLines = current.latestLogLines + "[server] stopped"
        )
    }

    suspend fun triggerOperation(current: CodexUiState, operation: String): CodexUiState {
        delay(100)
        return current.copy(
            latestLogLines = current.latestLogLines + "[operation] $operation executed"
        )
    }
}
