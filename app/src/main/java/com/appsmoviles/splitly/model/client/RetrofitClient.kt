package com.appsmoviles.splitly.model.client

import com.appsmoviles.splitly.model.response.WebService
import com.appsmoviles.splitly.model.response.appmanagement.SettingsWebService
import com.appsmoviles.splitly.model.response.distribution.BillWebService
import com.appsmoviles.splitly.model.response.householdmanagement.HouseholdMemberWebService
import com.appsmoviles.splitly.model.response.householdmanagement.HouseholdWebService
import com.appsmoviles.splitly.model.response.householdmanagement.InvitationWebService
import com.appsmoviles.splitly.model.response.iam.UserWebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://harmonix-mobile-backend.onrender.com/api/v1/")
        .client(OkHttpClientObject.getClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val webService: WebService by lazy {
        retrofit.create(WebService::class.java)
    }

    val userWebService: UserWebService by lazy {
        retrofit.create(UserWebService::class.java)
    }

    val settingsWebService: SettingsWebService by lazy {
        retrofit.create(SettingsWebService::class.java)
    }

    val householdWebService: HouseholdWebService by lazy {
        retrofit.create(HouseholdWebService::class.java)
    }

    val householdMember: HouseholdMemberWebService by lazy {
        retrofit.create(HouseholdMemberWebService::class.java)
    }

    val billWebService: BillWebService by lazy {
        retrofit.create(BillWebService::class.java)
    }

    val householdMemberWebService: HouseholdMemberWebService by lazy{
        retrofit.create(HouseholdMemberWebService::class.java)
    }

    val invitationWebService: InvitationWebService by lazy {
        retrofit.create(InvitationWebService::class.java)
    }

}