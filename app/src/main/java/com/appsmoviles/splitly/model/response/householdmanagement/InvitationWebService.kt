package com.appsmoviles.splitly.model.response.householdmanagement

import com.appsmoviles.splitly.model.beans.householdmanagement.Invitation
import com.appsmoviles.splitly.model.response.WebService
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InvitationWebService: WebService {

    @POST("invitations")
    suspend fun createInvitation(
        @Body invitation: Invitation
    ) : Response<Invitation>


    @GET("invitations/pending")
    suspend fun getPendingInvitationsByEmailAndHousehold(
        @Query("email") email: String,
        @Query("householdId") householdId: String
    ) : Response<Invitation>
}