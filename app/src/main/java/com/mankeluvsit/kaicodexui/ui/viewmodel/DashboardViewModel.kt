package com.mankeluvsit.kaicodexui.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceLifecycle
import com.mankeluvsit.kaicodexui.data.ServiceStatus
import com.mankeluvsit.kaicodexui.data.ServiceType
import com.mankeluvsit.kaicodexui.data.SettingsStore
import com.mankeluvsit.kaicodexui.network.ServiceClient
import com.mankeluvsit.kaicodexui.repository.ServiceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val configs: List<ServiceConfig> = emptyList(),
    val statuses: Map<ServiceType, ServiceStatus> = emptyMap()
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ServiceRepository(SettingsStore(application), ServiceClient())

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        observeConfigs()
    }

    private fun observeConfigs() {
        viewModelScope.launch {
            repository.configs.collectLatest { configs ->
                _uiState.update { it.copy(configs = configs) }
                pollingJob?.cancel()
                pollingJob = launch { pollStatuses() }
            }
        }
    }

    private suspend fun pollStatuses() {
        while (true) {
            val statuses = _uiState.value.configs.associate { cfg ->
                cfg.type to repository.loadStatus(cfg)
            }
            _uiState.update { it.copy(statuses = statuses) }
            delay(5_000)
        }
    }

    fun updateConfig(config: ServiceConfig) {
        viewModelScope.launch {
            repository.saveConfig(config)
        }
    }

    fun startService(config: ServiceConfig) {
        applyAction(config) { repository.start(config) }
    }

    fun stopService(config: ServiceConfig) {
        applyAction(config) { repository.stop(config) }
    }

    private fun applyAction(
        config: ServiceConfig,
        action: suspend () -> ServiceStatus
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    statuses = it.statuses + (
                        config.type to ServiceStatus(
                            type = config.type,
                            lifecycle = ServiceLifecycle.STARTING,
                            reachable = false,
                            metadata = emptyMap(),
                            lastUpdatedEpochMs = System.currentTimeMillis()
                        )
                        )
                )
            }
            val result = action()
            _uiState.update { current -> current.copy(statuses = current.statuses + (config.type to result)) }
        }
    }
}
