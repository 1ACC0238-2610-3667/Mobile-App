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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HouseholdMemberViewModel: ViewModel() {

    var isLoading by mutableStateOf(true)
    var errorMessage: String? by mutableStateOf(null)

    var householdMembers: MutableMap<String, ArrayList<User?>> = mutableMapOf()
    var invitationResponse: Invitation? by mutableStateOf(null)

    fun getHouseholdMembersByHouseholdId(households: List<Household?>) {
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

                        val usersProfileListPerHousehold = arrayListOf<User?>()
                        for (hm in auxHouseholdMembers) {
                            usersProfileListPerHousehold.add(users.find { it.id == hm.userId })
                        }

                        householdMembers[hId] = usersProfileListPerHousehold
                    }
                }

                withContext(Dispatchers.Main) { isLoading = false }
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