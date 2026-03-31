package com.mankeluvsit.codexui.model

data class CodexServerConfig(
    val gatewayHost: String = "127.0.0.1",
    val gatewayPort: Int = 18923,
    val proxyEnabled: Boolean = false,
    val proxyHost: String = "",
    val proxyPort: Int = 0,
    val publicUrl: String = "http://127.0.0.1:18923"
)
