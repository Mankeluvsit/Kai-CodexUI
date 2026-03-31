package com.mankeluvsit.codexui.model

data class CodexServerState(
    val running: Boolean = false,
    val config: CodexServerConfig = CodexServerConfig(),
    val logs: List<String> = listOf("CodexUI service initialized in managed mode.")
)
