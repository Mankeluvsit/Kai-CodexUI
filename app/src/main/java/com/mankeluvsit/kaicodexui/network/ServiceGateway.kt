package com.mankeluvsit.kaicodexui.network

import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceState
import com.mankeluvsit.kaicodexui.data.ServiceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody.Companion.toRequestBody

class ServiceGateway {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun fetchStatus(config: ServiceConfig, healthPaths: List<String>): ServiceStatus = withContext(Dispatchers.IO) {
        val headers = mapOfNotNull(
            "Authorization" to config.token.takeIf { it.isNotBlank() }?.let { "Bearer $it" },
            "X-API-Key" to config.token.takeIf { it.isNotBlank() }
        )

        healthPaths.forEach { path ->
            val url = "${config.endpoint()}$path"
            val request = Request.Builder().url(url).apply {
                headers.forEach { (k, v) -> header(k, v) }
            }.build()
            runCatching {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        return@withContext ServiceStatus(
                            state = ServiceState.RUNNING,
                            message = "${config.name} reachable",
                            metadata = mapOf(
                                "endpoint" to url,
                                "http" to response.code.toString(),
                                "server" to (response.header("Server") ?: "unknown")
                            )
                        )
                    }
                }
            }
        }
        ServiceStatus(state = ServiceState.ERROR, message = "Unable to reach ${config.name}")
    }

    suspend fun sendAction(config: ServiceConfig, action: String): Result<String> = withContext(Dispatchers.IO) {
        val candidates = listOf("/api/$action", "/$action")
        for (path in candidates) {
            val request = Request.Builder()
                .url("${config.endpoint()}$path")
                .post("".toRequestBody())
                .apply {
                    if (config.token.isNotBlank()) header("Authorization", "Bearer ${config.token}")
                }
                .build()
            val result = runCatching {
                client.newCall(request).execute().use {
                    if (it.isSuccessful) "${config.name} $action success" else error("HTTP ${it.code}")
                }
            }
            if (result.isSuccess) return@withContext result
        }
        Result.failure(IllegalStateException("${config.name} does not expose start/stop endpoint"))
    }
}
