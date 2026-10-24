package com.microbond.app.core.network

import com.microbond.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    /** Optional token provider dynamically configured by Auth/Session storage */
    var tokenProvider: (() -> String?)? = null

    /** Optional callback invoked when any API call returns 401 Unauthorized */
    var onUnauthorizedCallback: (() -> Unit)? = null

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        // Attach Authorization header and monitor 401 session expirations
        builder.addInterceptor(
            AuthInterceptor(
                tokenProvider = { tokenProvider?.invoke() },
                onUnauthorized = { onUnauthorizedCallback?.invoke() }
            )
        )

        // Only log HTTP payloads in debug builds (prevents leaking PINs/KYC in production logcat)
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
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    inline fun <reified T> createService(): T = retrofit.create(T::class.java)

    val apiService: MicrobondApiService by lazy {
        createService<MicrobondApiService>()
    }
}
