package com.appsmoviles.splitly.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.householdmanagement.HouseholdMember
import com.appsmoviles.splitly.model.beans.householdmanagement.Invitation
import com.appsmoviles.splitly.model.beans.iam.User
import com.appsmoviles.splitly.model.client.RetrofitClient
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.collections.forEach

class HouseholdMemberViewModel: ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    var householdMembers: MutableMap<String, ArrayList<User?>> = mutableMapOf()

    var invitationResponse: Invitation? by mutableStateOf(null)


    fun getHouseholdMembersByHouseholdId(viewModel: HouseholdViewModel, id: Int){
            viewModelScope.launch(Dispatchers.Main) {
                isLoading = true
                errorMessage = null
                try {


                    //In case the members view is the one to be
                    //accessed first by some weird or unknow reason
                    if (viewModel.households.isEmpty()) {
                        withContext(Dispatchers.IO) {
                            viewModel.getHouseholdsByRepresentativeId(id)
                        }
                    } else {
                        val auxHouseholds = viewModel.households
                        auxHouseholds.forEach { households ->
                            val usersProfileListPerHousehold: ArrayList<User?> by mutableStateOf(arrayListOf())
                            val auxHouseholdMembers = RetrofitClient
                                .householdMemberWebService.getHouseholdMembersByHouseholdId(households!!.id).body() as ArrayList<HouseholdMember>
                            auxHouseholdMembers.forEach { householdMembersList ->
                                usersProfileListPerHousehold.add(
                                    RetrofitClient.userWebService
                                        .getUserProfile(householdMembersList.userId).body() as User)
                            }
                            householdMembers[households.id] = usersProfileListPerHousehold

                            Log.i("HouseholdMembers", "$householdMembers")
                        }
                    }
                }catch (e: Exception){
                    errorMessage = "Error: ${e.message}"
                }finally {
                    isLoading = false
                }
            }
    }

    fun createInvitation(invitation: Invitation){
        viewModelScope.launch(Dispatchers.Main) {
            isLoading = true
            errorMessage = null

            try {
                val response = withContext(Dispatchers.IO){
                    RetrofitClient.invitationWebService.createInvitation(invitation)
                }

                if(response.isSuccessful && response.body() != null ){
                     invitationResponse = response.body()!!
                }else{
                    errorMessage = "Error: ${response.code()} Message: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }

    }


}