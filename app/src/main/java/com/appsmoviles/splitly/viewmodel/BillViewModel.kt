package com.appsmoviles.splitly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.collections.forEach

class BillViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)

    var errorMessage: String? by mutableStateOf(null)

    var amountOfBills: Int by mutableIntStateOf(0)

    var householdBills: MutableMap<String, ArrayList<Bills>> = mutableMapOf()


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

                        householdBills[householdId] = response.body() as ArrayList<Bills>

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

    fun createBill(bills: Bills, onSuccess: ()->Unit){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response =withContext(Dispatchers.IO){
                    RetrofitClient.billWebService.createBill(bills)
                }
                if (response.isSuccessful){
                    onSuccess()
                }else{
                    errorMessage = "Error: ${response.code()}"
                }
            }catch (e: Exception){
                errorMessage = "Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }
    }

    fun deleteBill(id: String, onSuccess: () -> Unit){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO){
                    RetrofitClient.billWebService.deleteBill(id)
                }
                if(response.isSuccessful && response.body() != null){
                    onSuccess()
                }else{
                    errorMessage = "Error: ${response.code()}"
                }
            }catch (e: Exception){
                errorMessage = "Error: ${e.message}"
            }finally {
                isLoading = false
            }
        }
    }
}