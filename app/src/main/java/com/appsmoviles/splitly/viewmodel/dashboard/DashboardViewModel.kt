package com.appsmoviles.splitly.viewmodel.dashboard

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.appsmoviles.splitly.model.client.RetrofitClient
import org.json.JSONObject

class DashboardViewModel : ViewModel() {

    var userName by mutableStateOf("Usuario")
    var userPlan by mutableStateOf("FREE")
    var householdId by mutableStateOf("")
    var totalMembers by mutableStateOf(0)
    var totalExpenses by mutableStateOf(0.0)
    var totalContributions by mutableStateOf(0.0)
    var isLoading by mutableStateOf(true)

    suspend fun loadInternalData(context: Context) {
        isLoading = true
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)

        val rawToken = prefs.getString("token", "") ?: ""
        val token = "Bearer $rawToken"
        householdId = prefs.getString("householdId", "") ?: ""

        try {
            val userJsonStr = prefs.getString("user", null)
            if (!userJsonStr.isNullOrEmpty()) {
                val json = JSONObject(userJsonStr)
                userName = json.optString("name", "Usuario")
                userPlan = json.optString("plan", "FREE").uppercase()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (householdId.isNotEmpty() && rawToken.isNotEmpty()) {
            try {
                // TOTAL MIEMBROS
                val membersRes = RetrofitClient.webService.getMembers(token, householdId)
                if (membersRes.isSuccessful && membersRes.body() != null) {
                    val membersList = membersRes.body()!!
                    totalMembers = membersList.filter { it.houseHoldId == householdId }.size
                }

                // GASTOS TOTALES
                val billsRes = RetrofitClient.webService.getBills(token, householdId)
                if (billsRes.isSuccessful && billsRes.body() != null) {
                    val billsList = billsRes.body()!!
                    totalExpenses = billsList.sumOf { it.amount }
                }

                // APORTES TOTALES
                val contRes = RetrofitClient.webService.getContributions(token, householdId)
                if (contRes.isSuccessful && contRes.body() != null) {
                    val contList = contRes.body()!!
                    totalContributions = contList.sumOf { it.amount }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        isLoading = false
    }
}