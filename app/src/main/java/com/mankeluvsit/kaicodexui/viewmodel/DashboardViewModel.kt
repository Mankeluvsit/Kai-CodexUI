package com.mankeluvsit.kaicodexui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceKind
import com.mankeluvsit.kaicodexui.data.ServiceRepository
import com.mankeluvsit.kaicodexui.data.ServiceRuntimeStatus
import com.mankeluvsit.kaicodexui.data.ServiceState
import com.mankeluvsit.kaicodexui.data.SettingsStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardUiState(
    val codexConfig: ServiceConfig = ServiceConfig(port = 3001),
    val openClawConfig: ServiceConfig = ServiceConfig(port = 3002),
    val codexStatus: ServiceRuntimeStatus = ServiceRuntimeStatus(),
    val openClawStatus: ServiceRuntimeStatus = ServiceRuntimeStatus(),
    val globalError: String? = null
)

class DashboardViewModel(
    private val settingsStore: SettingsStore,
    private val repository: ServiceRepository = ServiceRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    init {
        viewModelScope.launch {
            combine(settingsStore.codexConfig, settingsStore.openClawConfig) { codex, claw ->
                codex to claw
            }.collect { (codex, claw) ->
                _uiState.value = _uiState.value.copy(codexConfig = codex, openClawConfig = claw)
            }
        }
        startPolling()
    }

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                refreshStatuses()
                delay(5000)
            }
        }
    }

    fun refreshStatuses() {
        viewModelScope.launch {
            val codex = repository.fetchCodexStatus(_uiState.value.codexConfig)
            val claw = repository.fetchOpenClawStatus(_uiState.value.openClawConfig)
            _uiState.value = _uiState.value.copy(codexStatus = codex, openClawStatus = claw)
        }
    }

    fun saveConfig(kind: ServiceKind, config: ServiceConfig) {
        viewModelScope.launch {
            settingsStore.saveConfig(kind, config)
        }
    }

    fun startService(kind: ServiceKind) {
        viewModelScope.launch {
            setTransitionState(kind, ServiceState.STARTING)
            val result = when (kind) {
                ServiceKind.CODEX_UI -> repository.startCodex(_uiState.value.codexConfig)
                ServiceKind.OPENCLAW -> repository.startOpenClaw(_uiState.value.openClawConfig)
            }
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(globalError = null)
            } else {
                _uiState.value.copy(globalError = result.exceptionOrNull()?.message)
            }
            refreshStatuses()
        }
    }

    fun stopService(kind: ServiceKind) {
        viewModelScope.launch {
            val result = when (kind) {
                ServiceKind.CODEX_UI -> repository.stopCodex(_uiState.value.codexConfig)
                ServiceKind.OPENCLAW -> repository.stopOpenClaw(_uiState.value.openClawConfig)
            }
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(globalError = null)
            } else {
                _uiState.value.copy(globalError = result.exceptionOrNull()?.message)
            }
            refreshStatuses()
        }
    }

    private fun setTransitionState(kind: ServiceKind, state: ServiceState) {
        _uiState.value = when (kind) {
            ServiceKind.CODEX_UI -> _uiState.value.copy(codexStatus = _uiState.value.codexStatus.copy(state = state))
            ServiceKind.OPENCLAW -> _uiState.value.copy(openClawStatus = _uiState.value.openClawStatus.copy(state = state))
        }
    }
}

class DashboardViewModelFactory(
    private val settingsStore: SettingsStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(settingsStore) as T
        }
        error("Unknown model class")
    }
}
