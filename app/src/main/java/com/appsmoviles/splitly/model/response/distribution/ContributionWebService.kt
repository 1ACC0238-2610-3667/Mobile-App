package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.Contribution
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContributionWebService {
    @POST("contribution")
    suspend fun createContribution(
        @Body contribution: Contribution
    ): Response<Contribution>


    @GET("contribution/byhouseholdid/{householdId}")
    suspend fun getContributionsByHouseholdId(
        @Path("houeholdId") householdId: String
    ): Response<Contribution>

    @DELETE("contribution/{id}")
    suspend fun deleteContribution(
        @Path("id") id: String
    ): Response<Contribution>
}