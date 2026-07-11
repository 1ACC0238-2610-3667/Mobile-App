package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.beans.distribution.CreateContributionResource
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContributionWebService {
    @POST("contribution")
    suspend fun createContribution(@Body contribution: CreateContributionResource): Response<Contribution>

    @GET("contribution/byhouseholdid/{householdId}")
    suspend fun getContributionsByHouseholdId(@Path("householdId") householdId: String): Response<Contribution>

    @DELETE("contribution/{id}")
    suspend fun deleteContribution(@Path("id") id: String): Response<Contribution>

    @GET("contribution/bybillid/{billId}")
    suspend fun getContributionByBillId(@Path("billId") billId: String): Response<Contribution>

    @GET("contribution/{id}")
    suspend fun getContributionById(@Path("id") id: String): Response<Contribution>
}