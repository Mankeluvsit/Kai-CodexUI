package com.mankeluvsit.kaicodexui.data

import com.mankeluvsit.kaicodexui.network.ServiceGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DashboardRepository(
    private val configStore: ConfigStore,
    private val serviceGateway: ServiceGateway
) {
    val configs: Flow<Pair<ServiceConfig, ServiceConfig>> = configStore.state

    fun statusStream(codex: ServiceConfig, claw: ServiceConfig): Flow<Pair<ServiceStatus, ServiceStatus>> = flow {
        while (true) {
            val codexStatus = serviceGateway.fetchStatus(codex, listOf("/api/health", "/health", "/"))
            val clawStatus = serviceGateway.fetchStatus(claw, listOf("/health", "/api/status", "/"))
            emit(codexStatus to clawStatus)
            kotlinx.coroutines.delay(4_000)
        }
    }

    suspend fun saveCodex(config: ServiceConfig) = configStore.saveCodex(config)
    suspend fun saveOpenClaw(config: ServiceConfig) = configStore.saveOpenClaw(config)

    suspend fun startService(config: ServiceConfig): Result<String> = serviceGateway.sendAction(config, "start")
    suspend fun stopService(config: ServiceConfig): Result<String> = serviceGateway.sendAction(config, "stop")
}
