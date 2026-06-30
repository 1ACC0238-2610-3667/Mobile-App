package com.appsmoviles.splitly.viewmodel.household

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.distribution.CreateHouseHoldResource
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HouseholdViewModel : ViewModel() {

    var household: Household? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)
    var households: List<Household> by mutableStateOf(emptyList())
    var householdHasDebtsMap by mutableStateOf<Map<String, Boolean>>(emptyMap())
    var auxHousehold: Household? by mutableStateOf(null)

    fun getHouseholdById(id: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.getHouseHoldById(id)
                }
                if (response.isSuccessful && response.body() != null) household = response.body()
            } catch (e: Exception) { errorMessage = "Error: ${e.message}" } finally { isLoading = false }
        }
    }

    var lastUpdated by mutableStateOf(0L)

    fun getHouseholdsByRepresentativeId(id: Int, forceRefresh: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!forceRefresh && (now - lastUpdated) < 120_000L && households.isNotEmpty()) {
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.getHouseHoldByRepresentativeId(id)
                }
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.filterNotNull()
                    val debtsMap = coroutineScope {
                        list.map { household ->
                            async(Dispatchers.IO) {
                                val hId = household.id ?: return@async null
                                val membersRes = RetrofitClient.householdMemberWebService.getHouseholdMembersByHouseholdId(hId)
                                var hasDebts = false
                                if (membersRes.isSuccessful && membersRes.body() != null) {
                                    val members = membersRes.body()!!
                                    for (member in members) {
                                        val mcRes = RetrofitClient.memberContributionWebService.getMemberContributionsByMemberId(member.id!!)
                                        if (mcRes.isSuccessful && mcRes.body() != null) {
                                            val mcs = mcRes.body()!!
                                            for (mc in mcs) {
                                                val status = mc.status?.lowercase() ?: "pending"
                                                if (status != "done" && status != "paid" && status != "approved") {
                                                    hasDebts = true
                                                    break
                                                }
                                            }
                                        }
                                        if (hasDebts) break
                                    }
                                }
                                hId to hasDebts
                            }
                        }.awaitAll().filterNotNull().toMap()
                    }
                    withContext(Dispatchers.Main) {
                        householdHasDebtsMap = debtsMap
                        households = list
                        lastUpdated = System.currentTimeMillis()
                    }
                }
            } catch (e: Exception) { errorMessage = "Error: ${e.message}" } finally { isLoading = false }
        }
    }

    fun createHousehold(name: String, desc: String, currency: String, memberCount: Int, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val todayStr = sdf.format(java.util.Date())

            val newHousehold = CreateHouseHoldResource(
                name = name,
                representativeId = userId,
                currency = currency,
                description = desc,
                memberCount = memberCount,
                startDate = todayStr
            )

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.createHouseHold(newHousehold)
                }
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateHouseholdById(id: String, updatedHousehold: Household) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            auxHousehold = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.updateHouseHoldIdById(id, updatedHousehold)
                }
                if (response.isSuccessful && response.body() != null) {
                    auxHousehold = response.body()
                } else {
                    errorMessage = "Error al actualizar: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteHousehold(id: String, representativeId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.deleteHouseHold(id)
                }
                if (response.isSuccessful) {
                    getHouseholdsByRepresentativeId(representativeId)
                } else {
                    errorMessage = "Error al eliminar el hogar: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}