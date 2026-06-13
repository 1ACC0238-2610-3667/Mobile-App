package com.appsmoviles.splitly.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.SignUpRequest
import com.appsmoviles.splitly.model.beans.iam.User
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

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
                    editor.putBoolean("is_logged_in", true)
                    editor.putString("email", email)

                    val userJson = JSONObject().apply {
                        put("id", authData.id)
                        put("name", authData.name ?: "Usuario")
                        put("email", email ?: email)
                        put("role", finalRole)
                        put("plan", finalPlan)
                        put("householdId", finalHouseholdId)
                    }
                    editor.putString("user", userJson.toString())
                    editor.apply()

                    CredentialsSessionManager.init(context)

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

    fun signUp(context: Context, newSignUpRequest: SignUpRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.webService.signUp(newSignUpRequest)
                }
                Log.d("SignUp Response - ","response message: ${response.message()}")
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = "Error en el registro: ${response.code()}"
                    Log.d("errorMessage ","errorMessage: $errorMessage")
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
                Log.d("errorMessage ","errorMessage: $errorMessage")
            } finally {
                isLoading = false
            }
        }
    }


    fun clearError() {
        errorMessage = null
    }
}