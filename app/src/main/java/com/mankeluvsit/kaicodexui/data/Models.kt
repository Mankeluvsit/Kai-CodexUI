package com.mankeluvsit.kaicodexui.data

enum class ServiceKind { CODEX_UI, OPENCLAW }

enum class ServiceState { STARTING, RUNNING, STOPPED, ERROR, UNKNOWN }

data class ServiceConfig(
    val baseUrl: String = "http://10.0.2.2",
    val port: Int = 3000,
    val token: String = ""
)

data class ServiceRuntimeStatus(
    val state: ServiceState = ServiceState.UNKNOWN,
    val version: String? = null,
    val uptimeSeconds: Long? = null,
    val gatewayStatus: String? = null,
    val activeConnections: Int? = null,
    val message: String? = null,
    val lastError: String? = null
)
