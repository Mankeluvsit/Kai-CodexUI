package com.mankeluvsit.kaicodexui.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mankeluvsit.kaicodexui.data.GenericServiceStatusResponse
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.kaicodexui.data.ServiceState
import com.mankeluvsit.kaicodexui.data.ServiceStatus
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ServiceGateway {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun api(config: ServiceConfig): ServiceApi {
        return Retrofit.Builder()
            .baseUrl(config.rootUrl.ensureTrailingSlash())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(ServiceApi::class.java)
    }

    fun connectWebSocket(
        config: ServiceConfig,
        onEvent: (ServiceStatus) -> Unit,
        onError: (String) -> Unit
    ): WebSocket {
        val request = Request.Builder()
            .url(config.rootUrl.toWsUrl() + "/ws")
            .apply {
                if (config.token.isNotBlank()) {
                    header("Authorization", "Bearer ${config.token}")
                }
            }
            .build()

        return client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val response = runCatching {
                    json.decodeFromString(GenericServiceStatusResponse.serializer(), text)
                }.getOrNull()

                if (response != null) {
                    onEvent(response.toServiceStatus())
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                onError(t.message ?: "WebSocket failure")
            }
        })
    }
}

private fun GenericServiceStatusResponse.toServiceStatus(): ServiceStatus {
    val normalized = (state ?: status).orEmpty().lowercase()
    val mappedState = when {
        normalized.contains("run") -> ServiceState.RUNNING
        normalized.contains("start") -> ServiceState.STARTING
        normalized.contains("error") -> ServiceState.ERROR
        else -> ServiceState.STOPPED
    }

    return ServiceStatus(
        state = mappedState,
        healthy = mappedState == ServiceState.RUNNING,
        message = message.orEmpty(),
        version = version,
        uptimeSeconds = uptime,
        activeConnections = activeConnections
    )
}

private fun String.ensureTrailingSlash(): String = if (endsWith('/')) this else "$this/"

private fun String.toWsUrl(): String = when {
    startsWith("https://") -> replaceFirst("https://", "wss://")
    startsWith("http://") -> replaceFirst("http://", "ws://")
    else -> "ws://$this"
}
