package com.appsmoviles.splitly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class BillViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)

    var errorMessage: String? by mutableStateOf(null)

    var amountOfBills: Int by mutableIntStateOf(0)



    fun getAmountOfBillsByHouseholdIds(householdIds: List<String>){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            amountOfBills = 0
            try {
                var count = 0
                val totalBills = withContext(Dispatchers.IO) {
                    householdIds.forEach { householdId ->
                        val response =
                            RetrofitClient.billWebService
                                .getBillByHouseHoldId(householdId)

                        if(response.isSuccessful){
                            count += (response.body() as ArrayList<Bills>).size ?: 0
                        }else{
                            errorMessage = "Error:  ${response.message()}"
                        }
                    }
                    count
                }
                amountOfBills = totalBills
            }catch (e: Exception){
                errorMessage = "Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }

    }
}