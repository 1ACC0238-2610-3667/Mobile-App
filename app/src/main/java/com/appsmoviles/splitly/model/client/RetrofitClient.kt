package com.appsmoviles.splitly.model.client

import com.appsmoviles.splitly.model.response.WebService
import com.appsmoviles.splitly.model.response.appmanagement.SettingsWebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://harmonix-mobile-backend.onrender.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val webService: WebService by lazy {
        retrofit.create(WebService::class.java)
    }

    val settingsWebService: SettingsWebService by lazy {
        retrofit.create(SettingsWebService::class.java)
    }
}