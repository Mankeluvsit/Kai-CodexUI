package com.mankeluvsit.kaicodexui.data

import com.mankeluvsit.codexui.CodexUiApi
import com.mankeluvsit.kaicodexui.network.ServiceClients
import com.mankeluvsit.openclaw.OpenClawApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceRepository {
    suspend fun fetchCodexStatus(cfg: ServiceConfig): ServiceRuntimeStatus = withContext(Dispatchers.IO) {
        callStatus(ServiceClients.codexApi(cfg))
    }

    suspend fun fetchOpenClawStatus(cfg: ServiceConfig): ServiceRuntimeStatus = withContext(Dispatchers.IO) {
        callStatus(ServiceClients.openClawApi(cfg))
    }

    suspend fun startCodex(cfg: ServiceConfig): Result<String> = withContext(Dispatchers.IO) {
        action { ServiceClients.codexApi(cfg).start() }
    }

    suspend fun stopCodex(cfg: ServiceConfig): Result<String> = withContext(Dispatchers.IO) {
        action { ServiceClients.codexApi(cfg).stop() }
    }

    suspend fun startOpenClaw(cfg: ServiceConfig): Result<String> = withContext(Dispatchers.IO) {
        action { ServiceClients.openClawApi(cfg).start() }
    }

    suspend fun stopOpenClaw(cfg: ServiceConfig): Result<String> = withContext(Dispatchers.IO) {
        action { ServiceClients.openClawApi(cfg).stop() }
    }

    private suspend fun callStatus(api: CodexUiApi): ServiceRuntimeStatus {
        return runCatching { api.status() }
            .map { response ->
                ServiceRuntimeStatus(
                    state = mapState(response.state),
                    version = response.version,
                    uptimeSeconds = response.uptimeSeconds,
                    gatewayStatus = response.gatewayStatus,
                    activeConnections = response.activeConnections,
                    message = response.message
                )
            }
            .getOrElse { throwable ->
                ServiceRuntimeStatus(state = ServiceState.ERROR, lastError = throwable.message)
            }
    }

    private suspend fun callStatus(api: OpenClawApi): ServiceRuntimeStatus {
        return runCatching { api.status() }
            .map { response ->
                ServiceRuntimeStatus(
                    state = mapState(response.state),
                    version = response.version,
                    uptimeSeconds = response.uptimeSeconds,
                    gatewayStatus = response.gatewayStatus,
                    activeConnections = response.activeConnections,
                    message = response.message
                )
            }
            .getOrElse { throwable ->
                ServiceRuntimeStatus(state = ServiceState.ERROR, lastError = throwable.message)
            }
    }

    private inline fun action(block: () -> Any): Result<String> {
        return runCatching {
            block()
            "ok"
        }
    }

    private fun mapState(raw: String): ServiceState = when (raw.lowercase()) {
        "starting" -> ServiceState.STARTING
        "running" -> ServiceState.RUNNING
        "stopped" -> ServiceState.STOPPED
        "error" -> ServiceState.ERROR
        else -> ServiceState.UNKNOWN
    }
}
