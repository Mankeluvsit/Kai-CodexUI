package com.mankeluvsit.codexui.data

data class CodexUiSettings(
    val gateway: String = "local",
    val proxy: String = "",
    val port: Int = 18923,
    val url: String = "http://127.0.0.1:18923"
)

data class CodexUiStatus(
    val running: Boolean = false,
    val lastUpdatedEpochMillis: Long = System.currentTimeMillis(),
    val logs: List<String> = emptyList()
)
