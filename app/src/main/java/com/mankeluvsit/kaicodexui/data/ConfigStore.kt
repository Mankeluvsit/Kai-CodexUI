package com.mankeluvsit.kaicodexui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "service_config")

class ConfigStore(private val context: Context) {

    private object Keys {
        val codexBaseUrl = stringPreferencesKey("codex_base_url")
        val codexPort = intPreferencesKey("codex_port")
        val codexToken = stringPreferencesKey("codex_token")

        val openClawBaseUrl = stringPreferencesKey("openclaw_base_url")
        val openClawPort = intPreferencesKey("openclaw_port")
        val openClawToken = stringPreferencesKey("openclaw_token")
    }

    val codexConfig: Flow<ServiceConfig> = context.dataStore.data.map { prefs ->
        ServiceConfig(
            baseUrl = prefs[Keys.codexBaseUrl] ?: "http://10.0.2.2",
            port = prefs[Keys.codexPort] ?: 8080,
            token = prefs[Keys.codexToken] ?: ""
        )
    }

    val openClawConfig: Flow<ServiceConfig> = context.dataStore.data.map { prefs ->
        ServiceConfig(
            baseUrl = prefs[Keys.openClawBaseUrl] ?: "http://10.0.2.2",
            port = prefs[Keys.openClawPort] ?: 3000,
            token = prefs[Keys.openClawToken] ?: ""
        )
    }

    suspend fun updateConfig(type: ServiceType, config: ServiceConfig) {
        context.dataStore.edit { prefs ->
            when (type) {
                ServiceType.CODEX_UI -> {
                    prefs[Keys.codexBaseUrl] = config.baseUrl
                    prefs[Keys.codexPort] = config.port
                    prefs[Keys.codexToken] = config.token
                }

                ServiceType.OPEN_CLAW -> {
                    prefs[Keys.openClawBaseUrl] = config.baseUrl
                    prefs[Keys.openClawPort] = config.port
                    prefs[Keys.openClawToken] = config.token
                }
            }
        }
    }
}
