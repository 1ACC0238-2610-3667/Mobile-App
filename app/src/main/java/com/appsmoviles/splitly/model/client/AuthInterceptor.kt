package com.appsmoviles.splitly.model.client

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        // Only add header if token is present
        if (token.isBlank()) {
            return chain.proceed(originalRequest)
        }

        //Build new req with auth header
        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization","Bearer $token")
            .build()

        return chain.proceed(requestWithAuth)
    }
}