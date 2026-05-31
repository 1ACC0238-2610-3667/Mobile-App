package com.appsmoviles.splitly.model.client

import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl("https://harmonix-mobile-backend.onrender.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}