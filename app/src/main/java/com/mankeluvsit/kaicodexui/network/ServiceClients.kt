package com.mankeluvsit.kaicodexui.network

import com.mankeluvsit.codexui.CodexUiApi
import com.mankeluvsit.kaicodexui.data.ServiceConfig
import com.mankeluvsit.openclaw.OpenClawApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceClients {
    private fun retrofit(baseUrl: String, token: String): Retrofit {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun codexApi(cfg: ServiceConfig): CodexUiApi {
        return retrofit(url(cfg), cfg.token).create(CodexUiApi::class.java)
    }

    fun openClawApi(cfg: ServiceConfig): OpenClawApi {
        return retrofit(url(cfg), cfg.token).create(OpenClawApi::class.java)
    }

    fun url(cfg: ServiceConfig): String {
        val root = cfg.baseUrl.trimEnd('/')
        return "$root:${cfg.port}/"
    }
}
