package com.appsmoviles.splitly.model.response.appmanagement

import com.appsmoviles.splitly.model.beans.appmanagement.Settings
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SettingsWebService: WebService {

    @GET("settings")
    suspend fun getSettingByUserId(
        @Query("userId") userId: Int
    ): Response<Settings>

    @POST("settings")
    suspend fun createSettings(
        @Body settings: Settings
    ): Response<Settings>

    @PUT("settings/{id}")
    suspend fun updateSetting(
        @Path("id") id: Int,
        @Body settings: Settings
    ) : Response<Settings>

}