package com.appsmoviles.splitly.model.response.householdmanagement

import com.appsmoviles.splitly.model.beans.householdmanagement.HouseholdMember
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HouseholdMemberWebService: WebService {

    @GET("household_member/household/{householdId}")
    suspend fun getHouseholdMembersByHouseholdId(
        @Path("householdId") householdId: String
    ): Response<List<HouseholdMember>>



}