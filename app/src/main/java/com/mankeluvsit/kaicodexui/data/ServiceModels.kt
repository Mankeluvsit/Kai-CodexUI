package com.mankeluvsit.kaicodexui.data

import kotlinx.serialization.Serializable

@Serializable
data class ServiceConfig(
    val name: String,
    val baseUrl: String,
    val port: Int,
    val token: String
) {
    fun endpoint(): String = "${baseUrl.trimEnd(':', '/')}:$port"
}

enum class ServiceState {
    STARTING,
    RUNNING,
    STOPPED,
    ERROR,
    UNKNOWN
}

data class ServiceStatus(
    val state: ServiceState = ServiceState.UNKNOWN,
    val message: String = "",
    val lastUpdatedEpochMs: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)

data class DashboardState(
    val codexUiConfig: ServiceConfig = ServiceConfig("CodexUI", "http://127.0.0.1", 3000, ""),
    val openClawConfig: ServiceConfig = ServiceConfig("OpenClaw", "http://127.0.0.1", 18789, ""),
    val codexUiStatus: ServiceStatus = ServiceStatus(),
    val openClawStatus: ServiceStatus = ServiceStatus(),
    val isBusy: Boolean = false,
    val error: String? = null
)
