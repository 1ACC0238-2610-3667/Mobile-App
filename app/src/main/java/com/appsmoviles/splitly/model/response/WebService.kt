package com.appsmoviles.splitly.model.response

import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    //USER
    @GET("user")
    suspend fun getMembers(
        @Header("Authorization") token: String,
        @Query("householdId") householdId: String,
        @Query("role") role: String = "member"
    ): Response<List<User>>

    //AUTHENTICATION
    @POST("authentication/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body user: User): Response<User>


    @GET("contribution/byhouseholdid/{householdId}")
    suspend fun getContributions(
        @Header("Authorization") token: String,
        @Path("householdId") householdId: String
    ): Response<List<Contribution>>
}