package com.appsmoviles.splitly.model.response

import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.beans.distribution.MemberContribution
import com.appsmoviles.splitly.model.beans.distribution.PayContributionRequest
import com.appsmoviles.splitly.model.beans.distribution.UpdateIncomeRequest
import com.appsmoviles.splitly.model.beans.householdmanagement.HouseholdMember
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.SignUpRequest
import com.appsmoviles.splitly.model.beans.iam.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    @GET("user")
    suspend fun getMembers(
        @Header("Authorization") token: String,
        @Query("householdId") householdId: String,
        @Query("role") role: String = "member"
    ): Response<List<User>>

    @POST("authentication/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<User>

    @GET("user/user/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<User>

    @GET("household_member/user/{userId}")
    suspend fun getHouseholdMemberships(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<HouseholdMember>>

    @PUT("household_member/{id}")
    suspend fun updateMemberIncome(
        @Header("Authorization") token: String,
        @Path("id") memberId: String,
        @Body request: UpdateIncomeRequest
    ): Response<Any>

    @GET("bills")
    suspend fun getBills(
        @Header("Authorization") token: String,
        @Query("householdId") householdId: String
    ): Response<List<Bills>>

    @GET("contribution/byhouseholdid/{householdId}")
    suspend fun getContributions(
        @Header("Authorization") token: String,
        @Path("householdId") householdId: String
    ): Response<List<Contribution>>

    @GET("member_contribution/bymemberid/{memberId}")
    suspend fun getMemberContributions(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: String
    ): Response<List<MemberContribution>>

    @PUT("member_contribution/{id}")
    suspend fun payContribution(
        @Header("Authorization") token: String,
        @Path("id") memberContributionId: String,
        @Body request: PayContributionRequest
    ): Response<Any>

    @PUT("member_contribution/{id}/request")
    suspend fun requestPayment(
        @Header("Authorization") token: String,
        @Path("id") memberContributionId: String,
        @Body request: PayContributionRequest
    ): Response<Any>
}