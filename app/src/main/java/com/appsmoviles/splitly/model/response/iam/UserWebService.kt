// Ubicación: com.appsmoviles.splitly.model.response.iam.UserWebService.kt
package com.appsmoviles.splitly.model.response.iam

import com.appsmoviles.splitly.model.beans.iam.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserWebService {
    @GET("user/user/{id}")
    suspend fun getUserProfile(
        @Path("id") id: Int
    ): Response<User>

    @GET("user")
    suspend fun getAllUsers(): Response<List<User>>
}