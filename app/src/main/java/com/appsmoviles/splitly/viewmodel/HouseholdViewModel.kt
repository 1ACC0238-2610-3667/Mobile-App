package com.appsmoviles.splitly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HouseholdViewModel : ViewModel() {

    var household: Household? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)
    var households: ArrayList<Household> by mutableStateOf(arrayListOf())

    var auxHousehold: Household? by mutableStateOf(null)


    fun getHouseholdById(id: String){
        viewModelScope.launch(Dispatchers.Main) {
            isLoading = true
            errorMessage = null

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.getHouseHoldById(id)
                }
                if (response.isSuccessful  && response.body() != null)
                    household = response.body() as Household
                else
                    errorMessage = "Error: ${response.code()}"

            }catch (e: Exception){
                errorMessage = " Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }

    }

    fun getHouseholdsByRepresentativeId(id: Int){
        viewModelScope.launch(Dispatchers.Main) {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.householdWebService.getHouseHoldByRepresentativeId(id)
                }
                if (response.isSuccessful  && response.body() != null)
                    households = response.body() as ArrayList<Household>
                else
                    errorMessage = "Error: ${response.code()}"

            }catch (e: Exception){
                errorMessage = " Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }
    }

    fun updateHouseholdById(id: String, household: Household) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            auxHousehold = null
            try {
                val response = withContext(Dispatchers.IO){
                    RetrofitClient.householdWebService.updateHouseHoldIdById(id, household)
                }

                if (response.isSuccessful && response.body()!= null)
                    auxHousehold = response.body()
                else
                    errorMessage = "Error: ${response.code()}"
            }catch (e: Exception){
                errorMessage = "Error  ${e.message}"
            }finally {
                isLoading = false
            }
        }
    }

    fun createHousehold(household: Household){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            auxHousehold = null
            try {
                val response = withContext(Dispatchers.IO){
                    RetrofitClient.householdWebService.createHouseHold(household)
                }

                if(response.isSuccessful && response.body()!= null){
                    auxHousehold = response.body()
                }else{
                    errorMessage = "Error: (${response.code()}) ${response.message()} "
                }
            }catch(e: Exception) {
                errorMessage = "Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }
    }

}