package com.appsmoviles.splitly.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.User
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel : ViewModel() {

    var user: User? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    fun login(context: Context, email: String, pas: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = LoginRequest(email, pas)

                val response = RetrofitClient.webService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    user = authData

                    val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                    val editor = prefs.edit()

                    editor.putString("token", authData.token)

                    val finalHouseholdId = authData.houseHoldId ?: ""
                    val finalPlan = (authData.plan ?: "FREE").uppercase()
                    val finalRole = authData.role ?: "Representative"

                    editor.putString("householdId", finalHouseholdId)

                    val userJson = JSONObject().apply {
                        put("id", authData.id)
                        put("name", authData.name ?: "Usuario")
                        put("email", authData.email ?: email)
                        put("role", finalRole)
                        put("plan", finalPlan)
                        put("householdId", finalHouseholdId)
                    }
                    editor.putString("user", userJson.toString())
                    editor.apply()

                    onSuccess()
                } else {
                    errorMessage = "Error de autenticación: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
                Log.e("AuthViewModel", "Excepción en Login", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(context: Context, newUser: User, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.webService.signUp(newUser)
                if (response.isSuccessful && response.body() != null) {
                    user = response.body()
                    onSuccess()
                } else {
                    errorMessage = "Error en el registro: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}