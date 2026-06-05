package com.appsmoviles.splitly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.householdmanagement.HouseholdMember
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class HouseholdMemberViewModel: ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    var householdMembers: MutableMap<String, ArrayList<HouseholdMember>> = mutableMapOf()


    fun getHouseholdMembersByHouseholdId(viewModel: HouseholdViewModel, id: Int){
            viewModelScope.launch(Dispatchers.Main) {
                isLoading = true
                errorMessage = null
                //In case the members view is the one to be
                //accessed first by some weird or unknow reason
                if (viewModel.households.isNullOrEmpty()){
                    withContext(Dispatchers.IO){
                        viewModel.getHouseholdsByRepresentativeId(id)
                    }
                }else{
                    val auxHouseholds = viewModel.households
                    auxHouseholds.forEach {
                        val auxHouseholdMembers = RetrofitClient
                            .householdMemberWebService.getHouseholdMembersByHouseholdId(it!!.id) as ArrayList<HouseholdMember>
                        householdMembers[it.id] = auxHouseholdMembers
                    }
                }
            }







    }
}