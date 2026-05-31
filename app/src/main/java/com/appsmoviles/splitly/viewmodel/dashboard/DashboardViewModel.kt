package com.appsmoviles.splitly.viewmodel.dashboard

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.appsmoviles.splitly.model.client.RetrofitClient
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel : ViewModel() {
    var userName by mutableStateOf("Usuario")
    var userPlan by mutableStateOf("FREE")
    var userRole by mutableStateOf("Member")
    var householdId by mutableStateOf("")
    var totalMembers by mutableStateOf(0)

    var totalExpenses by mutableStateOf(0.0)
    var totalContributions by mutableStateOf(0.0)

    var myPendingDebt by mutableStateOf(0.0)
    var myPaidDebt by mutableStateOf(0.0)
    var overdueBillsCount by mutableStateOf(0)
    var upcomingBillsCount by mutableStateOf(0)

    var isLoading by mutableStateOf(true)

    suspend fun loadInternalData(context: Context) {
        isLoading = true
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        val rawToken = prefs.getString("token", "") ?: ""
        val token = "Bearer $rawToken"
        householdId = prefs.getString("householdId", "") ?: ""
        val userId = prefs.getInt("user_id", 0)

        try {
            val userJsonStr = prefs.getString("user", null)
            if (!userJsonStr.isNullOrEmpty()) {
                val json = JSONObject(userJsonStr)
                val rawName = json.optString("name", "")
                val email = json.optString("email", "")
                userName = rawName.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } }.ifBlank { "Usuario" }
                userPlan = json.optString("plan", "FREE").uppercase()
                userRole = json.optString("role", "Member")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (householdId.isNotEmpty() && rawToken.isNotEmpty() && userId != 0) {
            try {
                val membersRes = RetrofitClient.webService.getMembers(token, householdId)
                if (membersRes.isSuccessful && membersRes.body() != null) {
                    totalMembers = membersRes.body()!!.size
                }

                if (userRole.equals("Representative", ignoreCase = true) || userRole.equals("Admin", ignoreCase = true)) {
                    val billsRes = RetrofitClient.webService.getBills(token, householdId)
                    if (billsRes.isSuccessful && billsRes.body() != null) {
                        totalExpenses = billsRes.body()!!.sumOf { it.amount }
                    }
                    val contRes = RetrofitClient.webService.getContributions(token, householdId)
                    if (contRes.isSuccessful && contRes.body() != null) {
                        totalContributions = contRes.body()!!.sumOf { it.amount }
                    }
                } else {
                    val membershipRes = RetrofitClient.webService.getHouseholdMemberships(token, userId)
                    if (membershipRes.isSuccessful && membershipRes.body() != null) {
                        val myMembership = membershipRes.body()!!.find { it.householdId == householdId }
                        if (myMembership != null) {
                            val contributionsRes = RetrofitClient.webService.getMemberContributions(token, myMembership.id)
                            val allContRes = RetrofitClient.webService.getContributions(token, householdId)

                            if (contributionsRes.isSuccessful && contributionsRes.body() != null) {
                                val myContributions = contributionsRes.body()!!
                                val allContributions = allContRes.body() ?: emptyList()

                                var pending = 0.0
                                var paid = 0.0
                                var overdue = 0
                                var upcoming = 0

                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                                val today = Date()
                                val sevenDaysFromNow = Date(today.time + 7L * 24 * 60 * 60 * 1000)

                                myContributions.forEach { quota ->
                                    val parent = allContributions.find { it.id == quota.contributionId }
                                    val deadlineStr = parent?.deadlineForMembers ?: ""

                                    val isPending = quota.status.equals("Pending", ignoreCase = true) || quota.status.equals("Review", ignoreCase = true)

                                    if (isPending) {
                                        pending += quota.amount
                                        try {
                                            if (deadlineStr.isNotEmpty()) {
                                                val cleanDeadline = deadlineStr.substringBefore(".").removeSuffix("Z")
                                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                                                val deadlineDate = sdf.parse(cleanDeadline)

                                                if (deadlineDate != null) {
                                                    if (deadlineDate.before(today)) {
                                                        overdue++
                                                    } else if (deadlineDate.after(today) && deadlineDate.before(sevenDaysFromNow)) {
                                                        upcoming++
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        paid += quota.amount
                                    }
                                }

                                myPendingDebt = pending
                                myPaidDebt = paid
                                overdueBillsCount = overdue
                                upcomingBillsCount = upcoming
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoading = false
    }
}