package com.mankeluvsit.codexui

import retrofit2.http.GET
import retrofit2.http.POST

interface CodexUiApi {
    @GET("api/status")
    suspend fun status(): ServiceStatusResponse

    @POST("api/start")
    suspend fun start(): ActionResponse

    @POST("api/stop")
    suspend fun stop(): ActionResponse
}

data class ServiceStatusResponse(
    val state: String = "unknown",
    val version: String? = null,
    val uptimeSeconds: Long? = null,
    val gatewayStatus: String? = null,
    val activeConnections: Int? = null,
    val message: String? = null
)

data class ActionResponse(
    val success: Boolean,
    val message: String? = null
)
