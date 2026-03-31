package com.mankeluvsit.kaicodexui.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mankeluvsit.kaicodexui.data.ConfigStore
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceRepository
import com.mankeluvsit.kaicodexui.data.ServiceType
import com.mankeluvsit.kaicodexui.data.ServiceUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: ServiceRepository
) : ViewModel() {

    val codexState: StateFlow<ServiceUiState> = repository.serviceState(ServiceType.CODEX_UI)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ServiceUiState(ServiceConfig(), com.mankeluvsit.kaicodexui.data.ServiceStatus()))

    val openClawState: StateFlow<ServiceUiState> = repository.serviceState(ServiceType.OPEN_CLAW)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ServiceUiState(ServiceConfig(), com.mankeluvsit.kaicodexui.data.ServiceStatus()))

    init {
        listOf(ServiceType.CODEX_UI, ServiceType.OPEN_CLAW).forEach { type ->
            viewModelScope.launch { repository.observeLive(type) }
            viewModelScope.launch { repository.startPollingLoop(type) }
        }
    }

    fun saveConfig(type: ServiceType, config: ServiceConfig) {
        viewModelScope.launch {
            repository.saveConfig(type, config)
            repository.pollStatus(type)
        }
    }

    fun start(type: ServiceType) {
        viewModelScope.launch { repository.start(type) }
    }

    fun stop(type: ServiceType) {
        viewModelScope.launch { repository.stop(type) }
    }

    fun refresh(type: ServiceType) {
        viewModelScope.launch { repository.pollStatus(type) }
    }

    override fun onCleared() {
        repository.closeSockets()
        super.onCleared()
    }
}

class DashboardViewModelFactory(
    private val context: android.content.Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = ServiceRepository(ConfigStore(context.applicationContext))
        return DashboardViewModel(repository) as T
    }
}
