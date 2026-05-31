package com.appsmoviles.splitly.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.iam.LoginRequest
import com.appsmoviles.splitly.model.beans.iam.SignUpRequest
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
                    val token = authData.token ?: ""
                    val userId = authData.id

                    val profileResponse = RetrofitClient.webService.getUserById("Bearer $token", userId)

                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        val userProfile = profileResponse.body()!!
                        user = userProfile

                        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                        val editor = prefs.edit()

                        val finalHouseholdId = userProfile.houseHoldId ?: ""
                        val finalPlan = (userProfile.plan ?: "FREE").uppercase()
                        val finalRole = userProfile.role ?: "Member"
                        val finalName = userProfile.name ?: "Usuario"

                        editor.putBoolean("is_logged_in", true)
                        editor.putString("token", token)
                        editor.putString("householdId", finalHouseholdId)
                        editor.putInt("user_id", userId)

                        val userJson = JSONObject().apply {
                            put("id", userId)
                            put("name", finalName)
                            put("email", userProfile.email ?: email)
                            put("role", finalRole)
                            put("plan", finalPlan)
                            put("householdId", finalHouseholdId)
                        }

                        editor.putString("user", userJson.toString())
                        editor.apply()

                        onSuccess()
                    } else {
                        errorMessage = "Error al descargar el perfil"
                    }
                } else {
                    errorMessage = "Error de autenticación: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    fun signUp(context: Context, request: SignUpRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.webService.signUp(request)
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    user = authData

                    // Guardar los datos de sesión para el Dashboard
                    val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                    val editor = prefs.edit()

                    editor.putString("token", authData.token)
                    val finalHouseholdId = authData.houseHoldId ?: request.householdId
                    editor.putString("householdId", finalHouseholdId)

                    val userJson = JSONObject().apply {
                        put("id", authData.id)
                        put("name", authData.name ?: request.name)
                        put("email", authData.email ?: request.email)
                        put("role", request.role)
                        put("plan", "FREE")
                    }
                    editor.putString("user", userJson.toString())
                    editor.apply()

                    onSuccess()
                } else {
                    errorMessage = "Error en el registro: ${response.code()} - Verifica los datos."
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}