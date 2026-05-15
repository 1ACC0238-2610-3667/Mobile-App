// Ubicación: com.appsmoviles.splitly.model.client.RetrofitClient.kt
package com.appsmoviles.splitly.model.client

import android.content.Context
import com.appsmoviles.splitly.model.response.WebService
import com.appsmoviles.splitly.model.response.distribution.BillWebService
import com.appsmoviles.splitly.model.response.distribution.ContributionWebService
import com.appsmoviles.splitly.model.response.iam.UserWebService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://harmonix-mobile-backend.onrender.com/api/v1/"
    private var retrofitInstance: Retrofit? = null

    fun initialize(context: Context) {
        if (retrofitInstance == null) {
            val authInterceptor = Interceptor { chain ->
                val prefs = context.applicationContext.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token", null)
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            retrofitInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: throw IllegalStateException("RetrofitClient no ha sido inicializado en la aplicación.")
    }

    val webService: WebService by lazy { getRetrofit().create(WebService::class.java) }
    val userWebService: UserWebService by lazy { getRetrofit().create(UserWebService::class.java) }
    val billWebService: BillWebService by lazy { getRetrofit().create(BillWebService::class.java) }
    val contributionWebService: ContributionWebService by lazy { getRetrofit().create(ContributionWebService::class.java) }
}