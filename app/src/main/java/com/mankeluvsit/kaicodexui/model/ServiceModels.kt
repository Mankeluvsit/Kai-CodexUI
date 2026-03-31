package com.mankeluvsit.kaicodexui.model

import java.time.Instant

enum class ServiceType(val displayName: String) {
    CODEX_UI("CodexUI"),
    OPEN_CLAW("OpenClaw")
}

enum class ServiceRunState {
    STARTING,
    RUNNING,
    STOPPED,
    ERROR,
    UNKNOWN
}

data class ServiceConfig(
    val baseUrl: String = "http://10.0.2.2",
    val port: Int = 8080,
    val token: String = ""
)

data class ServiceStatus(
    val state: ServiceRunState = ServiceRunState.UNKNOWN,
    val message: String = "No data",
    val version: String? = null,
    val uptime: String? = null,
    val activeConnections: Int? = null,
    val extraMetadata: Map<String, String> = emptyMap(),
    val updatedAt: Instant = Instant.now()
)

data class DashboardServiceUiModel(
    val type: ServiceType,
    val config: ServiceConfig,
    val status: ServiceStatus = ServiceStatus(),
    val isBusy: Boolean = false,
    val lastError: String? = null
)

data class DashboardUiState(
    val services: List<DashboardServiceUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val globalError: String? = null
)
