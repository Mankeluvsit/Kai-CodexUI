package com.mankeluvsit.kaicodexui.data

enum class ServiceType {
    CODEX_UI,
    OPEN_CLAW
}

enum class ServiceLifecycle {
    STARTING,
    RUNNING,
    STOPPED,
    ERROR,
    UNKNOWN
}

data class ServiceConfig(
    val type: ServiceType,
    val displayName: String,
    val baseUrl: String,
    val port: Int,
    val token: String,
    val statusPath: String,
    val startPath: String,
    val stopPath: String,
    val browserPath: String
) {
    val normalizedBaseUrl: String
        get() = baseUrl.trim().trimEnd('/')

    val serviceRoot: String
        get() = "$normalizedBaseUrl:$port"

    fun endpoint(path: String): String {
        val safePath = if (path.startsWith('/')) path else "/$path"
        return "$serviceRoot$safePath"
    }

    val browserUrl: String
        get() = endpoint(browserPath)
}

data class ServiceStatus(
    val type: ServiceType,
    val lifecycle: ServiceLifecycle,
    val reachable: Boolean,
    val metadata: Map<String, String>,
    val lastUpdatedEpochMs: Long,
    val errorMessage: String? = null
)
