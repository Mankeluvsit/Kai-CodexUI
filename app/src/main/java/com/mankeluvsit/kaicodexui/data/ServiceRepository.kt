package com.mankeluvsit.kaicodexui.data

import com.mankeluvsit.kaicodexui.network.ServiceApi
import com.mankeluvsit.kaicodexui.network.ServiceGateway
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.WebSocket
import retrofit2.Response

class ServiceRepository(
    private val configStore: ConfigStore,
    private val gateway: ServiceGateway = ServiceGateway(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val codexStatus = MutableStateFlow(ServiceStatus())
    private val openClawStatus = MutableStateFlow(ServiceStatus())
    private var codexSocket: WebSocket? = null
    private var openClawSocket: WebSocket? = null

    fun serviceState(type: ServiceType): Flow<ServiceUiState> {
        val configFlow: Flow<ServiceConfig> = when (type) {
            ServiceType.CODEX_UI -> configStore.codexConfig
            ServiceType.OPEN_CLAW -> configStore.openClawConfig
        }
        val statusFlow = when (type) {
            ServiceType.CODEX_UI -> codexStatus
            ServiceType.OPEN_CLAW -> openClawStatus
        }
        return combine(configFlow, statusFlow) { config, status -> ServiceUiState(config, status) }
    }

    suspend fun saveConfig(type: ServiceType, config: ServiceConfig) {
        configStore.updateConfig(type, config)
    }

    suspend fun pollStatus(type: ServiceType) = withContext(ioDispatcher) {
        val config = readConfig(type)
        val api = gateway.api(config)
        val auth = authHeader(config)

        val status = runCatching {
            val healthResp = api.health(auth)
            val statusResp = api.status(auth)
            statusFromResponses(healthResp, statusResp)
        }.getOrElse {
            ServiceStatus(
                state = ServiceState.ERROR,
                healthy = false,
                message = "Service unavailable",
                lastError = it.message
            )
        }

        emitStatus(type, status)
    }

    suspend fun start(type: ServiceType) = postAction(type) { api, auth -> api.start(auth) }

    suspend fun stop(type: ServiceType) = postAction(type) { api, auth -> api.stop(auth) }

    suspend fun observeLive(type: ServiceType) = withContext(ioDispatcher) {
        val config = readConfig(type)
        val socket = gateway.connectWebSocket(
            config = config,
            onEvent = { emitStatus(type, it) },
            onError = {
                emitStatus(type, ServiceStatus(ServiceState.ERROR, false, "WebSocket disconnected", lastError = it))
            }
        )

        when (type) {
            ServiceType.CODEX_UI -> codexSocket = socket
            ServiceType.OPEN_CLAW -> openClawSocket = socket
        }
    }

    fun closeSockets() {
        codexSocket?.close(1000, "ViewModel cleared")
        openClawSocket?.close(1000, "ViewModel cleared")
    }

    suspend fun startPollingLoop(type: ServiceType) = withContext(ioDispatcher) {
        while (isActive) {
            pollStatus(type)
            delay(3_000)
        }
    }

    private suspend fun postAction(
        type: ServiceType,
        call: suspend (ServiceApi, String?) -> Response<Unit>
    ) = withContext(ioDispatcher) {
        emitStatus(type, currentStatus(type).copy(state = ServiceState.STARTING, message = "Updating service state..."))
        val config = readConfig(type)
        val result = runCatching { call(gateway.api(config), authHeader(config)) }

        val status = result.fold(
            onSuccess = {
                if (it.isSuccessful) {
                    currentStatus(type).copy(state = ServiceState.RUNNING, message = "Request accepted")
                } else {
                    currentStatus(type).copy(state = ServiceState.ERROR, message = "Request failed: ${it.code()}")
                }
            },
            onFailure = {
                currentStatus(type).copy(state = ServiceState.ERROR, message = "Request failed", lastError = it.message)
            }
        )
        emitStatus(type, status)
    }

    private suspend fun readConfig(type: ServiceType): ServiceConfig {
        val flow = when (type) {
            ServiceType.CODEX_UI -> configStore.codexConfig
            ServiceType.OPEN_CLAW -> configStore.openClawConfig
        }
        return flow.first()
    }

    private fun statusFromResponses(
        healthResp: Response<Unit>,
        statusResp: Response<GenericServiceStatusResponse>
    ): ServiceStatus {
        val raw = statusResp.body()
        val state = when {
            statusResp.isSuccessful && healthResp.isSuccessful -> ServiceState.RUNNING
            statusResp.isSuccessful -> ServiceState.STARTING
            else -> ServiceState.ERROR
        }
        return ServiceStatus(
            state = state,
            healthy = healthResp.isSuccessful,
            message = raw?.message ?: "HTTP ${statusResp.code()}",
            version = raw?.version,
            uptimeSeconds = raw?.uptime,
            activeConnections = raw?.activeConnections,
            lastError = null
        )
    }

    private fun emitStatus(type: ServiceType, status: ServiceStatus) {
        when (type) {
            ServiceType.CODEX_UI -> codexStatus.value = status
            ServiceType.OPEN_CLAW -> openClawStatus.value = status
        }
    }

    private fun currentStatus(type: ServiceType): ServiceStatus = when (type) {
        ServiceType.CODEX_UI -> codexStatus.value
        ServiceType.OPEN_CLAW -> openClawStatus.value
    }

    private fun authHeader(config: ServiceConfig): String? = config.token.takeIf(String::isNotBlank)?.let { "Bearer $it" }
}
