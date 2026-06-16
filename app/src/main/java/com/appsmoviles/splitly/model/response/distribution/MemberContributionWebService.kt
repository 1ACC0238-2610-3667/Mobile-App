package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.CreateMemberContributionResource
import com.appsmoviles.splitly.model.beans.distribution.MemberContribution
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberContributionWebService {
    @GET("member_contribution/bymemberid/{memberId}")
    suspend fun getMemberContributionsByMemberId(@Path("memberId") memberId: String): Response<List<MemberContribution>>

    @GET("member_contribution/bycontributionid/{contributionId}")
    suspend fun getMemberContributionsByContributionId(@Path("contributionId") contributionId: String): Response<List<MemberContribution>>

    @POST("member_contribution")
    suspend fun createMemberContribution(@Body memberContribution: CreateMemberContributionResource): Response<MemberContribution>

    @PUT("member_contribution/{id}/approve")
    suspend fun approvePayment(@Path("id") id: String): Response<Unit>
}