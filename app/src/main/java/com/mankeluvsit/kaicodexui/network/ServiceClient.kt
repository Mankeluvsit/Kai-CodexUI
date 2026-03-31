package com.mankeluvsit.kaicodexui.network

import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceLifecycle
import com.mankeluvsit.kaicodexui.data.ServiceStatus
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ServiceClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .writeTimeout(4, TimeUnit.SECONDS)
        .build()

    fun fetchStatus(config: ServiceConfig): ServiceStatus {
        val request = Request.Builder()
            .url(config.endpoint(config.statusPath))
            .apply {
                if (config.token.isNotBlank()) {
                    addHeader("Authorization", "Bearer ${config.token}")
                }
            }
            .get()
            .build()

        return runCatching {
            http.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                val lifecycle = if (response.isSuccessful) ServiceLifecycle.RUNNING else ServiceLifecycle.ERROR
                ServiceStatus(
                    type = config.type,
                    lifecycle = lifecycle,
                    reachable = response.isSuccessful,
                    metadata = parseMetadata(body, response.code),
                    lastUpdatedEpochMs = System.currentTimeMillis(),
                    errorMessage = if (!response.isSuccessful) "HTTP ${response.code}" else null
                )
            }
        }.getOrElse { error ->
            ServiceStatus(
                type = config.type,
                lifecycle = ServiceLifecycle.STOPPED,
                reachable = false,
                metadata = emptyMap(),
                lastUpdatedEpochMs = System.currentTimeMillis(),
                errorMessage = error.message
            )
        }
    }

    fun trigger(config: ServiceConfig, start: Boolean): ServiceStatus {
        val request = Request.Builder()
            .url(config.endpoint(if (start) config.startPath else config.stopPath))
            .post("{}".toRequestBody("application/json".toMediaType()))
            .apply {
                if (config.token.isNotBlank()) {
                    addHeader("Authorization", "Bearer ${config.token}")
                }
            }
            .build()

        return runCatching {
            http.newCall(request).execute().use { response ->
                ServiceStatus(
                    type = config.type,
                    lifecycle = when {
                        response.isSuccessful && start -> ServiceLifecycle.STARTING
                        response.isSuccessful && !start -> ServiceLifecycle.STOPPED
                        else -> ServiceLifecycle.ERROR
                    },
                    reachable = response.isSuccessful,
                    metadata = mapOf("httpCode" to response.code.toString()),
                    lastUpdatedEpochMs = System.currentTimeMillis(),
                    errorMessage = if (!response.isSuccessful) response.message else null
                )
            }
        }.getOrElse { error ->
            ServiceStatus(
                type = config.type,
                lifecycle = ServiceLifecycle.ERROR,
                reachable = false,
                metadata = emptyMap(),
                lastUpdatedEpochMs = System.currentTimeMillis(),
                errorMessage = error.message
            )
        }
    }

    private fun parseMetadata(body: String, code: Int): Map<String, String> {
        if (body.isBlank()) {
            return mapOf("httpCode" to code.toString())
        }

        return runCatching {
            val json = JSONObject(body)
            json.keys().asSequence().take(8).associateWith { key ->
                json.optString(key)
            } + ("httpCode" to code.toString())
        }.getOrElse {
            mapOf(
                "httpCode" to code.toString(),
                "payload" to body.take(150)
            )
        }
    }
}
