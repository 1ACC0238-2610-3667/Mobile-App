package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.ContributionDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ContributionWebService {
    @GET("contribution/byhouseholdid/{id}")
    suspend fun getContributions(@Path("id") id: String): Response<List<ContributionDto>>
}