package com.mankeluvsit.kaicodexui.repository

import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceStatus
import com.mankeluvsit.kaicodexui.data.SettingsStore
import com.mankeluvsit.kaicodexui.network.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ServiceRepository(
    private val settingsStore: SettingsStore,
    private val serviceClient: ServiceClient
) {
    val configs: Flow<List<ServiceConfig>> = settingsStore.configs

    suspend fun saveConfig(config: ServiceConfig) = settingsStore.save(config)

    suspend fun loadStatus(config: ServiceConfig): ServiceStatus = withContext(Dispatchers.IO) {
        serviceClient.fetchStatus(config)
    }

    suspend fun start(config: ServiceConfig): ServiceStatus = withContext(Dispatchers.IO) {
        serviceClient.trigger(config, start = true)
    }

    suspend fun stop(config: ServiceConfig): ServiceStatus = withContext(Dispatchers.IO) {
        serviceClient.trigger(config, start = false)
    }
}
