package com.mankeluvsit.codexui

data class CodexUiConfig(
    val gateway: String,
    val proxy: String,
    val port: Int,
    val url: String
)

enum class ServerState {
    STOPPED,
    STARTING,
    RUNNING,
    STOPPING,
    ERROR
}
