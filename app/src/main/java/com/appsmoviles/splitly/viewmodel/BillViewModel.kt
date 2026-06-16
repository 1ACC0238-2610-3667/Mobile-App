package com.appsmoviles.splitly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    var billsList by mutableStateOf<List<Bills>>(emptyList())

    fun getBillByHouseHoldId(householdId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.billWebService.getBillByHouseHoldId(householdId)
                }
                if (response.isSuccessful && response.body() != null) {
                    billsList = response.body()!!
                } else {
                    errorMessage = "Error al cargar historial: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}