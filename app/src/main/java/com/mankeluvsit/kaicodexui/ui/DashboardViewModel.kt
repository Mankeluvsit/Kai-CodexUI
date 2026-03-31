package com.mankeluvsit.kaicodexui.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mankeluvsit.kaicodexui.data.DashboardRepository
import com.mankeluvsit.kaicodexui.data.DashboardState
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    init {
        viewModelScope.launch {
            repository.configs.collectLatest { (codex, claw) ->
                _uiState.value = _uiState.value.copy(codexUiConfig = codex, openClawConfig = claw)
                observeStatus(codex, claw)
            }
        }
    }

    private fun observeStatus(codex: ServiceConfig, claw: ServiceConfig) {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            repository.statusStream(codex, claw).collectLatest { (codexStatus, clawStatus) ->
                _uiState.value = _uiState.value.copy(
                    codexUiStatus = codexStatus,
                    openClawStatus = clawStatus,
                    error = null
                )
            }
        }
    }

    fun saveCodex(config: ServiceConfig) {
        viewModelScope.launch { repository.saveCodex(config) }
    }

    fun saveOpenClaw(config: ServiceConfig) {
        viewModelScope.launch { repository.saveOpenClaw(config) }
    }

    fun start(config: ServiceConfig) = runAction { repository.startService(config) }
    fun stop(config: ServiceConfig) = runAction { repository.stopService(config) }

    private fun runAction(action: suspend () -> Result<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true)
            val result = action()
            _uiState.value = _uiState.value.copy(
                isBusy = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    override fun onCleared() {
        pollJob?.cancel()
        super.onCleared()
    }

    class Factory(private val repository: DashboardRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository) as T
        }
    }
}
