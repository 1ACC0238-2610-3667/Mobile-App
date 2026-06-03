package com.appsmoviles.splitly.model.client

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        //Build new req with auth header
        val requestWithAuth = originalRequest.newBuilder()
            .header("Authorization","Bearer ${tokenProvider()}")
            .build()

        return chain.proceed(requestWithAuth)
    }
}