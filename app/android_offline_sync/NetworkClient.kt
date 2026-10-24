package com.microbond.app.network

import com.microbond.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cloud-Agnostic Retrofit & WebSocket Client for Microbond Android.
 *
 * Sourced dynamically from Android Gradle BuildConfig:
 * - Debug build: Points to Render Staging (e.g. https://microbond-api.onrender.com/)
 * - Release build: Points to Cloud Run Production (e.g. https://api.getmicrobond.com/)
 *
 * Zero lines of network client, ViewModel, or UI Compose code need to change
 * when migrating or testing between environments.
 */
object NetworkClient {

    val BASE_URL: String = BuildConfig.BASE_URL
    val WS_BASE_URL: String = BuildConfig.WS_BASE_URL

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        // Only log HTTP request/response payloads in Debug builds to prevent leaking sensitive KYC/PINs in logcat
        if (BuildConfig.ENABLE_HTTP_LOGS) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        builder.build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Strongly-typed Retrofit service creator.
     */
    inline fun <reified T> createService(): T = retrofit.create(T::class.java)

    /**
     * Singleton API service instance.
     */
    val apiService: MicrobondApiService by lazy {
        createService<MicrobondApiService>()
    }
}
