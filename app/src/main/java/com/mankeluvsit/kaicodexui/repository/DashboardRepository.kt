package com.mankeluvsit.kaicodexui.repository

import com.mankeluvsit.kaicodexui.data.ServiceConfigStore
import com.mankeluvsit.kaicodexui.model.DashboardServiceUiModel
import com.mankeluvsit.kaicodexui.model.ServiceConfig
import com.mankeluvsit.kaicodexui.model.ServiceRunState
import com.mankeluvsit.kaicodexui.model.ServiceStatus
import com.mankeluvsit.kaicodexui.model.ServiceType
import com.mankeluvsit.kaicodexui.network.ServiceApiClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class DashboardRepository(
    private val configStore: ServiceConfigStore,
    private val apiClient: ServiceApiClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val statusCache = MutableStateFlow(
        mapOf(
            ServiceType.CODEX_UI to ServiceStatus(),
            ServiceType.OPEN_CLAW to ServiceStatus()
        )
    )

    fun observeServices(): Flow<List<DashboardServiceUiModel>> {
        return combine(
            configStore.observeConfig(ServiceType.CODEX_UI),
            configStore.observeConfig(ServiceType.OPEN_CLAW),
            statusCache
        ) { codexConfig, openClawConfig, statusMap ->
            listOf(
                DashboardServiceUiModel(
                    type = ServiceType.CODEX_UI,
                    config = codexConfig,
                    status = statusMap[ServiceType.CODEX_UI] ?: ServiceStatus()
                ),
                DashboardServiceUiModel(
                    type = ServiceType.OPEN_CLAW,
                    config = openClawConfig,
                    status = statusMap[ServiceType.OPEN_CLAW] ?: ServiceStatus()
                )
            )
        }
    }

    fun observePolling(intervalMs: Long = 5_000): Flow<Unit> = flow {
        while (true) {
            refreshAllStatuses()
            emit(Unit)
            delay(intervalMs)
        }
    }.flowOn(ioDispatcher)

    suspend fun updateConfig(type: ServiceType, config: ServiceConfig) {
        configStore.saveConfig(type, config)
    }

    suspend fun refreshAllStatuses() = withContext(ioDispatcher) {
        refreshStatus(ServiceType.CODEX_UI)
        refreshStatus(ServiceType.OPEN_CLAW)
    }

    suspend fun refreshStatus(type: ServiceType) {
        val config = when (type) {
            ServiceType.CODEX_UI -> configStore.observeConfig(type)
            ServiceType.OPEN_CLAW -> configStore.observeConfig(type)
        }

        val currentConfig = config.first()

        val result = apiClient.fetchStatus(type, currentConfig)
        statusCache.update { map ->
            map + (type to result.getOrElse {
                ServiceStatus(
                    state = ServiceRunState.ERROR,
                    message = it.message ?: "Connection failed"
                )
            })
        }
    }

    suspend fun start(type: ServiceType): Result<Unit> = executeCommand(type) { apiClient.startService(it) }

    suspend fun stop(type: ServiceType): Result<Unit> = executeCommand(type) { apiClient.stopService(it) }

    private suspend fun executeCommand(
        type: ServiceType,
        block: (ServiceConfig) -> Result<Unit>
    ): Result<Unit> = withContext(ioDispatcher) {
        val config = configStore.observeConfig(type).first()
        block(config).also { refreshStatus(type) }
    }
}
