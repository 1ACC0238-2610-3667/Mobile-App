package com.appsmoviles.splitly.viewmodel.dashboard

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class ApprovalItem(
    val contributionId: String,
    val householdName: String,
    val memberName: String,
    val amount: Double
)

class DashboardViewModel : ViewModel() {

    var userName by mutableStateOf("User")
    var email by mutableStateOf("")

    var totalHouseholdsCount by mutableStateOf(0)
    var totalMembersCount by mutableStateOf(0)
    var totalCollected by mutableStateOf(0.0)
    var totalPending by mutableStateOf(0.0)

    var approvalsNeeded by mutableStateOf<List<ApprovalItem>>(emptyList())

    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadSummary(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading = true
                errorMessage = null
            }

            val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
            val userJsonStr = prefs.getString("user", null)
            var userId = -1

            if (!userJsonStr.isNullOrEmpty()) {
                try {
                    val json = JSONObject(userJsonStr)
                    withContext(Dispatchers.Main) {
                        userName = json.optString("name", "User")
                        email = json.optString("email", "Email")
                    }
                    userId = json.optInt("id", -1)
                } catch (e: Exception) { e.printStackTrace() }
            }

            if (userId != -1) {
                try {
                    val householdsRes = RetrofitClient.householdWebService.getHouseHoldByRepresentativeId(userId)
                    if (householdsRes.isSuccessful && householdsRes.body() != null) {
                        val householdsList = householdsRes.body()!!

                        val usersRes = RetrofitClient.userWebService.getAllUsers()
                        val userMap: Map<Int?, String> = if (usersRes.isSuccessful && usersRes.body() != null) {
                            usersRes.body()!!.associate { it.id to (it.personName?.takeIf { name -> name.isNotEmpty() } ?: it.email ?: "Miembro") }
                        } else emptyMap()

                        var membersCount = 0
                        var collected = 0.0
                        var pending = 0.0
                        val pendingApprovals = mutableListOf<ApprovalItem>()

                        for (household in householdsList) {
                            val hId = household.id ?: continue
                            val hName = household.name ?: "Hogar"

                            val membersRes = RetrofitClient.householdMemberWebService.getHouseholdMembersByHouseholdId(hId)
                            if (membersRes.isSuccessful && membersRes.body() != null) {
                                val members = membersRes.body()!!
                                membersCount += members.size

                                for (member in members) {
                                    val mcRes = RetrofitClient.memberContributionWebService.getMemberContributionsByMemberId(member.id!!)
                                    if (mcRes.isSuccessful && mcRes.body() != null) {
                                        val mcs = mcRes.body()!!
                                        val memberName = userMap[member.userId] ?: "Miembro"

                                        for (mc in mcs) {
                                            val status = mc.status?.lowercase() ?: "pending"
                                            val amount = mc.amount ?: 0.0

                                            if (status == "done" || status == "paid" || status == "approved") {
                                                collected += amount
                                            } else if (status.contains("review") || status.contains("request")) {
                                                pendingApprovals.add(ApprovalItem(mc.id!!, hName, memberName, amount))
                                            } else {
                                                pending += amount
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            totalHouseholdsCount = householdsList.size
                            totalMembersCount = membersCount
                            totalCollected = collected
                            totalPending = pending
                            approvalsNeeded = pendingApprovals
                        }
                    } else {
                        withContext(Dispatchers.Main) { errorMessage = "Error al cargar los hogares." }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { errorMessage = "Error de conexión: ${e.message}" }
                }
            } else {
                withContext(Dispatchers.Main) { errorMessage = "Usuario no autenticado." }
            }
            withContext(Dispatchers.Main) { isLoading = false }
        }
    }

    fun approvePayment(context: Context, contributionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.memberContributionWebService.approvePayment(contributionId)
                if (response.isSuccessful) {
                    loadSummary(context)
                } else {
                    withContext(Dispatchers.Main) { errorMessage = "Error: ${response.code()}" }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { errorMessage = "Error al aprobar: ${e.message}" }
            }
        }
    }
}