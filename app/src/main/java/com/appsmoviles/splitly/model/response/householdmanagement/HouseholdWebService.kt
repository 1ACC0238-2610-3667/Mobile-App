package com.appsmoviles.splitly.model.response.householdmanagement

import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HouseholdWebService: WebService {

    @GET("house_hold/{id}")
    suspend fun getHouseHoldById(
        @Path("id") id: String
    ) : Response<Household>

    @GET("household/{representativeId}")
    suspend fun getHouseHoldByRepresentativeId(
        @Path("representativeId") representativeId: Int
    ) : Response<List<Household>>

    @PUT("house_hold/{id}")
    suspend fun updateHouseHoldIdById(
        @Path("id") id: String,
        @Body houseHold: Household
    ): Response<Household>

    @POST("house_hold")
    suspend fun createHouseHold(
        @Body household: Household
    ): Response<Household>
}