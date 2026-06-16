package com.appsmoviles.splitly.model.response

import com.appsmoviles.splitly.model.beans.iam.AuthResponse
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.SignUpRequest
import com.appsmoviles.splitly.model.beans.iam.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface WebService {

    @GET("user")
    suspend fun getMembers(
        @Header("Authorization") token: String,
        @Query("householdId") householdId: String,
        @Query("role") role: String = "member"
    ): Response<List<User>>

    @POST("authentication/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<User>
}