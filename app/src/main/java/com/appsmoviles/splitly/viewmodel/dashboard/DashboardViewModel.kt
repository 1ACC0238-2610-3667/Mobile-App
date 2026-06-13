package com.appsmoviles.splitly.viewmodel.dashboard

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DashboardViewModel : ViewModel() {

    var userName by mutableStateOf("User")

    var email by mutableStateOf("")
    var totalBillsCount by mutableStateOf(0)
    var totalBillsAmount by mutableStateOf(0.0)
    var totalHouseholdsCount by mutableStateOf(0)
    var totalMembersCount by mutableStateOf(0)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadSummary(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            isLoading = true
            errorMessage = null
            
            val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)

            val userJsonStr = prefs.getString("user", null)
            Log.i("User data:", "It contains: $userJsonStr")
            var userId = -1
            if (!userJsonStr.isNullOrEmpty()) {
                try {
                    val json = JSONObject(userJsonStr)
                    userName = json.optString("name", "User")
                    email = json.optString("email", "Email")
                    userId = json.optInt("id", -1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (userId != -1) {
                try {
                    val householdsRes = withContext(Dispatchers.IO) {
                        RetrofitClient.householdWebService.getHouseHoldByRepresentativeId(userId)
                    }

                    Log.i("HouseholdsRes: ", "$householdsRes")

                    if (householdsRes.isSuccessful && householdsRes.body() != null) {
                        val householdsList = householdsRes.body()!!
                        totalHouseholdsCount = householdsList.size
                        
                        var billsCount = 0
                        var billsAmount = 0.0
                        var membersCount = 0

                        for (household in householdsList) {
                            val hId = household.id
                            
                            // Get Members
                            val membersRes = withContext(Dispatchers.IO) {
                                RetrofitClient.householdMember.getHouseholdMembersByHouseholdId(hId)
                            }
                            if (membersRes.isSuccessful && membersRes.body() != null) {
                                membersCount += membersRes.body()!!.size
                            }

                            // Get Bills
                            val billsRes = withContext(Dispatchers.IO) {
                                RetrofitClient.billWebService.getBillByHouseHoldId(hId)
                            }
                            if (billsRes.isSuccessful && billsRes.body() != null) {
                                val bills = billsRes.body()!!
                                billsCount += bills.size
                                billsAmount += bills.sumOf { it.amount }
                            }
                        }
                        
                        totalMembersCount = membersCount
                        totalBillsCount = billsCount
                        totalBillsAmount = billsAmount
                    } else {
                        errorMessage = "Failed to load households"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                }
            } else {
                errorMessage = "User not logged in"
            }
            isLoading = false
        }
    }
}
