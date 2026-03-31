package com.mankeluvsit.kaicodexui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mankeluvsit.kaicodexui.model.DashboardUiState
import com.mankeluvsit.kaicodexui.model.ServiceConfig
import com.mankeluvsit.kaicodexui.model.ServiceType
import com.mankeluvsit.kaicodexui.repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        observeServices()
        startPolling()
    }

    private fun observeServices() {
        viewModelScope.launch {
            repository.observeServices()
                .catch { throwable ->
                    _uiState.update { it.copy(globalError = throwable.message, isLoading = false) }
                }
                .collect { services ->
                    _uiState.update { it.copy(services = services, isLoading = false, globalError = null) }
                }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            repository.observePolling().collect {}
        }
    }

    fun refreshNow() {
        viewModelScope.launch {
            repository.refreshAllStatuses()
        }
    }

    fun onSaveConfig(type: ServiceType, config: ServiceConfig) {
        viewModelScope.launch {
            repository.updateConfig(type, config)
            repository.refreshStatus(type)
        }
    }

    fun onStartService(type: ServiceType) {
        viewModelScope.launch {
            repository.start(type)
        }
    }

    fun onStopService(type: ServiceType) {
        viewModelScope.launch {
            repository.stop(type)
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }

    class Factory(private val repository: DashboardRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository) as T
        }
    }
}
