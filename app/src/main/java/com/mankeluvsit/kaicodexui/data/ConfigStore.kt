package com.mankeluvsit.kaicodexui.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kai_dashboard")

class ConfigStore(private val context: Context) {

    private object Keys {
        val codexUrl = stringPreferencesKey("codex_url")
        val codexPort = intPreferencesKey("codex_port")
        val codexToken = stringPreferencesKey("codex_token")
        val clawUrl = stringPreferencesKey("claw_url")
        val clawPort = intPreferencesKey("claw_port")
        val clawToken = stringPreferencesKey("claw_token")
    }

    val state: Flow<Pair<ServiceConfig, ServiceConfig>> = context.dataStore.data.map { prefs ->
        val codex = ServiceConfig(
            name = "CodexUI",
            baseUrl = prefs[Keys.codexUrl] ?: "http://127.0.0.1",
            port = prefs[Keys.codexPort] ?: 3000,
            token = prefs[Keys.codexToken] ?: ""
        )
        val claw = ServiceConfig(
            name = "OpenClaw",
            baseUrl = prefs[Keys.clawUrl] ?: "http://127.0.0.1",
            port = prefs[Keys.clawPort] ?: 18789,
            token = prefs[Keys.clawToken] ?: ""
        )
        codex to claw
    }

    suspend fun saveCodex(config: ServiceConfig) = saveService(
        urlKey = Keys.codexUrl,
        portKey = Keys.codexPort,
        tokenKey = Keys.codexToken,
        config = config
    )

    suspend fun saveOpenClaw(config: ServiceConfig) = saveService(
        urlKey = Keys.clawUrl,
        portKey = Keys.clawPort,
        tokenKey = Keys.clawToken,
        config = config
    )

    private suspend fun saveService(
        urlKey: Preferences.Key<String>,
        portKey: Preferences.Key<Int>,
        tokenKey: Preferences.Key<String>,
        config: ServiceConfig
    ) {
        context.dataStore.edit { prefs ->
            prefs[urlKey] = config.baseUrl
            prefs[portKey] = config.port
            prefs[tokenKey] = config.token
        }
    }
}
