// Ubicación: com.appsmoviles.splitly.model.response.distribution.BillWebService.kt
package com.appsmoviles.splitly.model.response.distribution

import com.appsmoviles.splitly.model.beans.distribution.BillDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BillWebService {
    @GET("bills")
    suspend fun getBills(@Query("householdId") householdId: String): Response<List<BillDto>>
}