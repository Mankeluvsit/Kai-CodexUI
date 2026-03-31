package com.mankeluvsit.kaicodexui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kai_service_settings")

class SettingsStore(private val context: Context) {
    private object Keys {
        val codexBaseUrl = stringPreferencesKey("codex_base_url")
        val codexPort = intPreferencesKey("codex_port")
        val codexToken = stringPreferencesKey("codex_token")

        val clawBaseUrl = stringPreferencesKey("claw_base_url")
        val clawPort = intPreferencesKey("claw_port")
        val clawToken = stringPreferencesKey("claw_token")
    }

    val codexConfig: Flow<ServiceConfig> = context.dataStore.data.map { pref ->
        ServiceConfig(
            baseUrl = pref[Keys.codexBaseUrl] ?: "http://10.0.2.2",
            port = pref[Keys.codexPort] ?: 3001,
            token = pref[Keys.codexToken] ?: ""
        )
    }

    val openClawConfig: Flow<ServiceConfig> = context.dataStore.data.map { pref ->
        ServiceConfig(
            baseUrl = pref[Keys.clawBaseUrl] ?: "http://10.0.2.2",
            port = pref[Keys.clawPort] ?: 3002,
            token = pref[Keys.clawToken] ?: ""
        )
    }

    suspend fun saveConfig(kind: ServiceKind, cfg: ServiceConfig) {
        context.dataStore.edit { pref ->
            when (kind) {
                ServiceKind.CODEX_UI -> {
                    pref[Keys.codexBaseUrl] = cfg.baseUrl
                    pref[Keys.codexPort] = cfg.port
                    pref[Keys.codexToken] = cfg.token
                }
                ServiceKind.OPENCLAW -> {
                    pref[Keys.clawBaseUrl] = cfg.baseUrl
                    pref[Keys.clawPort] = cfg.port
                    pref[Keys.clawToken] = cfg.token
                }
            }
        }
    }
}
