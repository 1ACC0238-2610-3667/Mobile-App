package com.appsmoviles.splitly.model.response

import com.appsmoviles.splitly.model.beans.appmanagement.Settings
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WebService{

    @POST("authentication/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body user: User): Response<User>

    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @PUT("user/settings")
    suspend fun saveSettings(@Body settings: Settings): Response<Settings>

}