package com.microbond.app.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor: Injects JWT Bearer token into outgoing HTTP requests,
 * and intercepts HTTP 401 Unauthorized responses to notify the application
 * of session expiration or server-side token revocation (jti blacklist).
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?,
    private val onUnauthorized: (() -> Unit)? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath
        val isPublicEndpoint = path.contains("/auth/") || path.contains("/healthz")

        val requestBuilder = originalRequest.newBuilder()
        if (!isPublicEndpoint && originalRequest.header("Authorization") == null) {
            tokenProvider()?.let { token ->
                if (token.isNotBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }
        }
        val response = chain.proceed(requestBuilder.build())
        val isPinEndpoint = path.contains("pin")
        if (response.code == 401 && !isPublicEndpoint && !isPinEndpoint) {
            onUnauthorized?.invoke()
        }
        return response
    }
}
