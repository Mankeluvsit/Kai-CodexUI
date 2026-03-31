package com.mankeluvsit.kaicodexui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.serviceDataStore by preferencesDataStore(name = "service_settings")

class SettingsStore(private val context: Context) {

    private object Keys {
        val codexUrl = stringPreferencesKey("codex_url")
        val codexPort = intPreferencesKey("codex_port")
        val codexToken = stringPreferencesKey("codex_token")
        val codexStatus = stringPreferencesKey("codex_status")
        val codexStart = stringPreferencesKey("codex_start")
        val codexStop = stringPreferencesKey("codex_stop")

        val clawUrl = stringPreferencesKey("claw_url")
        val clawPort = intPreferencesKey("claw_port")
        val clawToken = stringPreferencesKey("claw_token")
        val clawStatus = stringPreferencesKey("claw_status")
        val clawStart = stringPreferencesKey("claw_start")
        val clawStop = stringPreferencesKey("claw_stop")
    }

    val configs: Flow<List<ServiceConfig>> = context.serviceDataStore.data.map { prefs ->
        listOf(
            ServiceConfig(
                type = ServiceType.CODEX_UI,
                displayName = "CodexUI",
                baseUrl = prefs[Keys.codexUrl] ?: "http://127.0.0.1",
                port = prefs[Keys.codexPort] ?: 5173,
                token = prefs[Keys.codexToken] ?: "",
                statusPath = prefs[Keys.codexStatus] ?: "/api/status",
                startPath = prefs[Keys.codexStart] ?: "/api/start",
                stopPath = prefs[Keys.codexStop] ?: "/api/stop",
                browserPath = "/"
            ),
            ServiceConfig(
                type = ServiceType.OPEN_CLAW,
                displayName = "OpenClaw",
                baseUrl = prefs[Keys.clawUrl] ?: "http://127.0.0.1",
                port = prefs[Keys.clawPort] ?: 11434,
                token = prefs[Keys.clawToken] ?: "",
                statusPath = prefs[Keys.clawStatus] ?: "/v1/models",
                startPath = prefs[Keys.clawStart] ?: "/api/start",
                stopPath = prefs[Keys.clawStop] ?: "/api/stop",
                browserPath = "/"
            )
        )
    }

    suspend fun save(config: ServiceConfig) {
        context.serviceDataStore.edit { prefs ->
            when (config.type) {
                ServiceType.CODEX_UI -> {
                    prefs[Keys.codexUrl] = config.baseUrl
                    prefs[Keys.codexPort] = config.port
                    prefs[Keys.codexToken] = config.token
                    prefs[Keys.codexStatus] = config.statusPath
                    prefs[Keys.codexStart] = config.startPath
                    prefs[Keys.codexStop] = config.stopPath
                }

                ServiceType.OPEN_CLAW -> {
                    prefs[Keys.clawUrl] = config.baseUrl
                    prefs[Keys.clawPort] = config.port
                    prefs[Keys.clawToken] = config.token
                    prefs[Keys.clawStatus] = config.statusPath
                    prefs[Keys.clawStart] = config.startPath
                    prefs[Keys.clawStop] = config.stopPath
                }
            }
        }
    }
}
