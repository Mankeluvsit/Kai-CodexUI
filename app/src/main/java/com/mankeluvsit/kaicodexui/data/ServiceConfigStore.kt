package com.mankeluvsit.kaicodexui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mankeluvsit.kaicodexui.model.ServiceConfig
import com.mankeluvsit.kaicodexui.model.ServiceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.configDataStore by preferencesDataStore(name = "service_config")

class ServiceConfigStore(private val context: Context) {

    fun observeConfig(serviceType: ServiceType): Flow<ServiceConfig> {
        return context.configDataStore.data.map { prefs ->
            ServiceConfig(
                baseUrl = prefs[stringPreferencesKey("${serviceType.name}_base_url")] ?: "http://10.0.2.2",
                port = prefs[intPreferencesKey("${serviceType.name}_port")] ?: defaultPort(serviceType),
                token = prefs[stringPreferencesKey("${serviceType.name}_token")] ?: ""
            )
        }
    }

    suspend fun saveConfig(serviceType: ServiceType, config: ServiceConfig) {
        context.configDataStore.edit { prefs ->
            prefs[stringPreferencesKey("${serviceType.name}_base_url")] = config.baseUrl
            prefs[intPreferencesKey("${serviceType.name}_port")] = config.port
            prefs[stringPreferencesKey("${serviceType.name}_token")] = config.token
        }
    }

    private fun defaultPort(serviceType: ServiceType): Int = when (serviceType) {
        ServiceType.CODEX_UI -> 3000
        ServiceType.OPEN_CLAW -> 3333
    }
}
