// Ubicación: com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel.kt
package com.appsmoviles.splitly.viewmodel.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

data class DashboardUiState(
    val userName: String = "",
    val userPlan: String = "FREE",
    val householdId: String = "",
    val totalMembers: Int = 0,
    val totalExpenses: Double = 0.0,
    val totalContributions: Double = 0.0,
    val isLoading: Boolean = true
)

class DashboardViewModel(context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    private val prefs = context.applicationContext.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)

    init {
        loadInternalData()
    }

    fun loadInternalData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            var name = "Usuario"
            var plan = "FREE"
            var hId = prefs.getString("householdId", "") ?: ""

            try {
                val userJsonStr = prefs.getString("user", null)
                if (!userJsonStr.isNullOrEmpty()) {
                    val json = JSONObject(userJsonStr)
                    name = json.optString("name", "Usuario")
                    plan = json.optString("plan", "FREE").uppercase()
                    if (hId.isEmpty()) hId = json.optString("householdId", "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _uiState.update { it.copy(userName = name, userPlan = plan, householdId = hId) }

            if (hId.isNotEmpty()) {
                try {
                    // Peticiones asíncronas concurrentes homólogas a Promise.allSettled
                    val membersDeferred = async { RetrofitClient.userWebService.getMembers(hId) }
                    val billsDeferred = async { RetrofitClient.billWebService.getBills(hId) }
                    val contributionsDeferred = async { RetrofitClient.contributionWebService.getContributions(hId) }

                    val membersRes = membersDeferred.await()
                    val billsRes = billsDeferred.await()
                    val contRes = contributionsDeferred.await()

                    val membersList = if (membersRes.isSuccessful) membersRes.body() ?: emptyList() else emptyList()
                    val billsList = if (billsRes.isSuccessful) billsRes.body() ?: emptyList() else emptyList()
                    val contList = if (contRes.isSuccessful) contRes.body() ?: emptyList() else emptyList()

                    // Sumarización manual segura replicando la capa de vista web
                    val validMembersCount = membersList.filter { it.householdId == hId }.size
                    val expensesSum = billsList.sumOf { it.amount?.toDoubleOrNull() ?: 0.0 }
                    val contributionsSum = contList.sumOf { it.amount?.toDoubleOrNull() ?: 0.0 }

                    _uiState.update {
                        it.copy(
                            totalMembers = validMembersCount,
                            totalExpenses = expensesSum,
                            totalContributions = contributionsSum,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}