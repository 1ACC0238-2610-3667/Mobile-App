package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.MemberContribution
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MemberContributionWebService: WebService {

    @POST("member_contribution")
    suspend fun createMemberContribution(
        @Body memberContribution: MemberContribution
    ): Response<MemberContribution>


    @GET("member_contribution/bycontributionid/{contributionId}")
    suspend fun getMemberContributionsByContributionId(
        @Path("contributionId") contributionId: String
    ): Response<List<MemberContribution>>

}