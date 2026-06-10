package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BillWebService : WebService {
    @GET("bills/byhousehold/{householdId}")
    suspend fun getBillByHouseHoldId(
        @Path("householdId") householdId: String
    ) : Response<List<Bills>>

    @POST("bills")
    suspend fun createBill(
        @Body bill: Bills
    ): Response<Bills>

    @DELETE("bills/{id}")
    suspend fun deleteBill(
        @Path("id") id: String
    ): Response<Unit>
}