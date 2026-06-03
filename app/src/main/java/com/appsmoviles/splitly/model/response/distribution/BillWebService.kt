package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BillWebService : WebService {

    @GET("bills/byhouseholdId/{householdId}")
    suspend fun getBillByHouseHoldId(
        @Path("householdId") householdId: String
    ) : Response<Bills>
}