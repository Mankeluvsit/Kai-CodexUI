package com.mankeluvsit.kaicodexui.data

import kotlinx.serialization.Serializable

enum class ServiceType { CODEX_UI, OPEN_CLAW }

enum class ServiceState { STOPPED, STARTING, RUNNING, ERROR }

@Serializable
data class ServiceConfig(
    val baseUrl: String = "http://10.0.2.2",
    val port: Int = 0,
    val token: String = ""
) {
    val rootUrl: String get() = "$baseUrl:$port"
}

data class ServiceStatus(
    val state: ServiceState = ServiceState.STOPPED,
    val healthy: Boolean = false,
    val message: String = "",
    val version: String? = null,
    val uptimeSeconds: Long? = null,
    val activeConnections: Int? = null,
    val lastError: String? = null
)

data class ServiceUiState(
    val config: ServiceConfig,
    val status: ServiceStatus,
    val isLoading: Boolean = false
)

@Serializable
data class GenericServiceStatusResponse(
    val status: String? = null,
    val state: String? = null,
    val message: String? = null,
    val version: String? = null,
    val uptime: Long? = null,
    val activeConnections: Int? = null
)
