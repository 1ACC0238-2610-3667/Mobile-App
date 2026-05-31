package com.appsmoviles.splitly.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.distribution.EnrichedContribution
import com.appsmoviles.splitly.model.beans.distribution.PayContributionRequest
import com.appsmoviles.splitly.model.beans.distribution.UpdateIncomeRequest
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.launch

class MyContributionsViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
    var currentIncome by mutableStateOf(0.0)
    var memberId by mutableStateOf("")

    var totalAssigned by mutableStateOf(0.0)
    var totalPaid by mutableStateOf(0.0)
    var totalPending by mutableStateOf(0.0)

    val pendingList = mutableStateListOf<EnrichedContribution>()
    val historyList = mutableStateListOf<EnrichedContribution>()

    suspend fun loadData(context: Context) {
        isLoading = true
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        val token = "Bearer ${prefs.getString("token", "")}"
        val householdId = prefs.getString("householdId", "") ?: ""
        val userId = prefs.getInt("user_id", 0)

        try {
            val membersRes = RetrofitClient.webService.getHouseholdMemberships(token, userId)
            if (membersRes.isSuccessful && membersRes.body() != null) {
                val myMembership = membersRes.body()!!.find { it.householdId == householdId }
                if (myMembership != null) {
                    memberId = myMembership.id
                    currentIncome = myMembership.income

                    val allContRes = RetrofitClient.webService.getContributions(token, householdId)
                    val allContributions = allContRes.body() ?: emptyList()

                    val myQuotasRes = RetrofitClient.webService.getMemberContributions(token, memberId)
                    val myQuotas = myQuotasRes.body() ?: emptyList()

                    pendingList.clear()
                    historyList.clear()
                    var assigned = 0.0
                    var paid = 0.0

                    myQuotas.forEach { quota ->
                        val parentContribution = allContributions.find { it.id == quota.contributionId }
                        val concept = parentContribution?.description ?: "Gasto de Hogar"
                        val deadline = parentContribution?.deadlineForMembers ?: "Sin fecha"

                        val enriched = EnrichedContribution(
                            memberContributionId = quota.id ?: "",
                            concept = concept,
                            amount = quota.amount,
                            status = quota.status ?: "Pending",
                            deadline = deadline,
                            payedAt = quota.payedAt ?: "---"
                        )

                        assigned += quota.amount

                        if (enriched.status.equals("Done", ignoreCase = true) || enriched.status.equals("Pagado", ignoreCase = true)) {
                            paid += quota.amount
                            historyList.add(enriched)
                        } else {
                            pendingList.add(enriched)
                        }
                    }

                    totalAssigned = assigned
                    totalPaid = paid
                    totalPending = assigned - paid
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    fun payQuota(context: Context, memberContributionId: String, amountToPay: Double) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        val token = "Bearer ${prefs.getString("token", "")}"

        viewModelScope.launch {
            isLoading = true
            try {
                val request = PayContributionRequest(amountToPay)
                val response = RetrofitClient.webService.requestPayment(token, memberContributionId, request)

                if (response.isSuccessful) {
                    loadData(context)
                } else {
                    println("Error al solicitar pago: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateIncome(context: Context, newIncome: Double) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        val token = "Bearer ${prefs.getString("token", "")}"

        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.updateMemberIncome(
                    token = token,
                    memberId = memberId,
                    request = UpdateIncomeRequest(newIncome)
                )
                if (response.isSuccessful) {
                    currentIncome = newIncome
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}