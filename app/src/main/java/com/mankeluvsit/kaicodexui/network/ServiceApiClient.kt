package com.mankeluvsit.kaicodexui.network

import com.mankeluvsit.kaicodexui.model.ServiceConfig
import com.mankeluvsit.kaicodexui.model.ServiceRunState
import com.mankeluvsit.kaicodexui.model.ServiceStatus
import com.mankeluvsit.kaicodexui.model.ServiceType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.Instant
import java.util.concurrent.TimeUnit

class ServiceApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    fun fetchStatus(serviceType: ServiceType, config: ServiceConfig): Result<ServiceStatus> {
        val endpoints = listOf("/api/status", "/status", "/api/v1/status")
        return executeFirstSuccess(config, endpoints) { url ->
            val requestBuilder = Request.Builder().url(url).get()
            if (config.token.isNotBlank()) {
                requestBuilder.header("Authorization", "Bearer ${config.token}")
                requestBuilder.header("X-API-Key", config.token)
            }
            val response = client.newCall(requestBuilder.build()).execute()
            response.use {
                if (!it.isSuccessful) error("HTTP ${it.code}")
                val body = it.body?.string().orEmpty()
                parseStatus(serviceType, body)
            }
        }
    }

    fun startService(config: ServiceConfig): Result<Unit> {
        return executeAction(config, listOf("/api/start", "/start", "/api/v1/start"))
    }

    fun stopService(config: ServiceConfig): Result<Unit> {
        return executeAction(config, listOf("/api/stop", "/stop", "/api/v1/stop"))
    }

    private fun executeAction(config: ServiceConfig, endpoints: List<String>): Result<Unit> {
        return executeFirstSuccess(config, endpoints) { url ->
            val requestBuilder = Request.Builder()
                .url(url)
                .post("{}".toRequestBody("application/json".toMediaType()))
            if (config.token.isNotBlank()) {
                requestBuilder.header("Authorization", "Bearer ${config.token}")
                requestBuilder.header("X-API-Key", config.token)
            }
            client.newCall(requestBuilder.build()).execute().use {
                if (!it.isSuccessful) error("HTTP ${it.code}")
            }
        }
    }

    private fun <T> executeFirstSuccess(
        config: ServiceConfig,
        endpoints: List<String>,
        block: (url: String) -> T
    ): Result<T> {
        var lastError: Throwable = IllegalStateException("No endpoints available")
        for (endpoint in endpoints) {
            val normalizedBaseUrl = config.baseUrl.removeSuffix("/")
            val url = "$normalizedBaseUrl:${config.port}$endpoint"
            try {
                return Result.success(block(url))
            } catch (t: Throwable) {
                lastError = t
            }
        }
        return Result.failure(lastError)
    }

    private fun parseStatus(serviceType: ServiceType, payload: String): ServiceStatus {
        if (payload.isBlank()) return ServiceStatus(state = ServiceRunState.UNKNOWN, message = "Empty response")

        return runCatching {
            val json = JSONObject(payload)
            val statusRaw = json.optString("status", json.optString("state", "unknown"))
            val state = when (statusRaw.lowercase()) {
                "running", "ok", "healthy" -> ServiceRunState.RUNNING
                "starting", "booting" -> ServiceRunState.STARTING
                "stopped", "down", "offline" -> ServiceRunState.STOPPED
                "error", "failed" -> ServiceRunState.ERROR
                else -> ServiceRunState.UNKNOWN
            }

            val extra = mutableMapOf<String, String>()
            json.keys().forEach { key ->
                if (key !in setOf("status", "state", "version", "uptime", "activeConnections", "connections", "message")) {
                    extra[key] = json.opt(key)?.toString().orEmpty()
                }
            }

            ServiceStatus(
                state = state,
                message = json.optString("message", "$serviceType status: $statusRaw"),
                version = json.optString("version", null),
                uptime = json.optString("uptime", null),
                activeConnections = json.optInt("activeConnections", json.optInt("connections", -1)).takeIf { it >= 0 },
                extraMetadata = extra,
                updatedAt = Instant.now()
            )
        }.getOrElse {
            ServiceStatus(
                state = ServiceRunState.UNKNOWN,
                message = "Unable to parse status payload",
                extraMetadata = mapOf("raw" to payload.take(300)),
                updatedAt = Instant.now()
            )
        }
    }
}
