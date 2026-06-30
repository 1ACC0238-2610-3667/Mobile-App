package com.appsmoviles.splitly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.beans.householdmanagement.Invitation
import com.appsmoviles.splitly.model.beans.iam.User
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MemberWithDebt(
    val user: User,
    val memberId: String,
    val pendingDebt: Double
)

class HouseholdMemberViewModel: ViewModel() {

    var isLoading by mutableStateOf(true)
    var errorMessage: String? by mutableStateOf(null)

    var householdMembers: MutableMap<String, List<MemberWithDebt>> = mutableMapOf()
    var invitationResponse: Invitation? by mutableStateOf(null)

    var lastUpdated by mutableStateOf(0L)

    fun getHouseholdMembersByHouseholdId(households: List<Household?>, forceRefresh: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!forceRefresh && (now - lastUpdated) < 120_000L && householdMembers.isNotEmpty()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) { // Debe correr en IO
            withContext(Dispatchers.Main) {
                isLoading = true
                errorMessage = null
            }
            try {
                val usersRes = RetrofitClient.userWebService.getAllUsers()
                val users = if (usersRes.isSuccessful && usersRes.body() != null) usersRes.body()!! else arrayListOf()

                for (household in households) {
                    val hId = household?.id ?: continue

                    val auxHouseholdMembersRes = RetrofitClient.householdMemberWebService.getHouseholdMembersByHouseholdId(hId)
                    if (auxHouseholdMembersRes.isSuccessful && auxHouseholdMembersRes.body() != null) {
                        val auxHouseholdMembers = auxHouseholdMembersRes.body()!!

                        val listPerHousehold = coroutineScope {
                            auxHouseholdMembers.map { hm ->
                                val memberId = hm.id
                                async(Dispatchers.IO) {
                                    val user = users.find { it.id == hm.userId } ?: return@async null
                                    var pendingDebt = 0.0
                                    if (memberId != null) {
                                        val mcRes = RetrofitClient.memberContributionWebService.getMemberContributionsByMemberId(memberId)
                                        if (mcRes.isSuccessful && mcRes.body() != null) {
                                            val mcs = mcRes.body()!!
                                            for (mc in mcs) {
                                                val status = mc.status?.lowercase() ?: "pending"
                                                if (status != "done" && status != "paid" && status != "approved") {
                                                    pendingDebt += mc.amount
                                                }
                                            }
                                        }
                                    }
                                    MemberWithDebt(user, memberId ?: "", pendingDebt)
                                }
                            }.awaitAll().filterNotNull()
                        }

                        householdMembers[hId] = listPerHousehold
                    }
                }

                withContext(Dispatchers.Main) {
                    isLoading = false
                    lastUpdated = System.currentTimeMillis()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Error: ${e.message}"
                    isLoading = false
                }
            }
        }
    }

    fun createInvitation(invitation: Invitation) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading = true
                errorMessage = null
            }
            try {
                val response = RetrofitClient.invitationWebService.createInvitation(invitation)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        invitationResponse = response.body()
                    } else {
                        errorMessage = "Error: ${response.code()} Message: ${response.message()}"
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Error: ${e.message}"
                    isLoading = false
                }
            }
        }
    }
}