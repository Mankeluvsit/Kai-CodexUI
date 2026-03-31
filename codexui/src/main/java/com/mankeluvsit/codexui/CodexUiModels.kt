package com.mankeluvsit.codexui

enum class ServerStatus {
    STOPPED,
    STARTING,
    RUNNING,
    STOPPING,
    ERROR
}

data class CodexUiConfig(
    val gateway: String = "local",
    val proxy: String = "",
    val port: Int = 18923,
    val baseUrl: String = "http://localhost",
    val tunnelEnabled: Boolean = true
)

data class CodexUiState(
    val config: CodexUiConfig = CodexUiConfig(),
    val serverStatus: ServerStatus = ServerStatus.STOPPED,
    val latestLogLines: List<String> = listOf("[init] CodexUI controls ready inside Kai"),
    val activeUrl: String = "http://localhost:18923"
)
