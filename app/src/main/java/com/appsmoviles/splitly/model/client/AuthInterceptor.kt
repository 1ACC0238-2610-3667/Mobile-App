package com.appsmoviles.splitly.model.client

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val urlPath = originalRequest.url().encodedPath()

        if (urlPath.contains("sign-in") || urlPath.contains("sign-up")) {
            return chain.proceed(originalRequest)
        }

        val token = tokenProvider()

        if (token.isBlank()) {
            return chain.proceed(originalRequest)
        }

        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(requestWithAuth)
    }
}