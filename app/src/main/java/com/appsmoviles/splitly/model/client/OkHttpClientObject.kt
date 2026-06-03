package com.appsmoviles.splitly.model.client

import android.content.Context
import okhttp3.OkHttpClient

object OkHttpClientObject {

    private lateinit var okHttpClient: OkHttpClient

    fun init(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor {
                val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                prefs.getString("token", "") ?: ""
            })
            .build()
    }

    fun getClient(): OkHttpClient {
        if (!::okHttpClient.isInitialized) {
            throw IllegalStateException("OkHttpClientObject not initialized. Call init(context) first.")
        }
        return okHttpClient
    }
}