package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.CreateBillResource
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BillWebService {
    @GET("bills")
    suspend fun getAllBills(): Response<List<Bills>>

    @GET("bills/byhousehold/{householdId}")
    suspend fun getBillByHouseHoldId(@Path("householdId") householdId: String): Response<List<Bills>>

    @POST("bills")
    suspend fun createBill(@Body bill: CreateBillResource): Response<Bills>

    @PUT("bills/byid/{id}")
    suspend fun updateBill(@Path("id") id: String, @Body bill: Bills): Response<Bills>

    @DELETE("bills/{id}")
    suspend fun deleteBill(@Path("id") id: String): Response<Unit>
}