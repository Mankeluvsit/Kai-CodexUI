package com.mankeluvsit.kaicodexui.network

import com.mankeluvsit.kaicodexui.data.GenericServiceStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ServiceApi {
    @GET("health")
    suspend fun health(@Header("Authorization") auth: String? = null): Response<Unit>

    @GET("status")
    suspend fun status(@Header("Authorization") auth: String? = null): Response<GenericServiceStatusResponse>

    @POST("start")
    suspend fun start(@Header("Authorization") auth: String? = null): Response<Unit>

    @POST("stop")
    suspend fun stop(@Header("Authorization") auth: String? = null): Response<Unit>
}
