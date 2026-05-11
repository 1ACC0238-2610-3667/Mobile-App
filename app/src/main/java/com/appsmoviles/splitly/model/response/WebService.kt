package com.appsmoviles.splitly.model.response

import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WebService{

    @POST("authentication/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body user: User): Response<User>

}