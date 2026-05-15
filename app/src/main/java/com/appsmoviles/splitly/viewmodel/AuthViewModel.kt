// Ubicación: com.appsmoviles.splitly.viewmodel.AuthViewModel.kt
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

class AuthViewModel(private val context: Context) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val prefs = context.applicationContext.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)

    fun login(email: String, pas: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Requiere que RetrofitClient.initialize(context) haya sido invocado en la App o MainActivity
                val request = LoginRequest(email, pas)
                val response = RetrofitClient.webService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    // Almacenamos el token de inmediato para que las peticiones subsecuentes lo utilicen
                    prefs.edit().putString("token", authData.token).apply()

                    // Solicitamos el perfil completo del usuario para consolidar variables (Réplica de log-in.vue)
                    val profileResponse = RetrofitClient.userWebService.getUserProfile(authData.id)
                    val resolvedUser = if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        profileResponse.body()!!
                    } else {
                        authData
                    }

                    user = resolvedUser
                    val finalHouseholdId = resolvedUser.householdId ?: authData.householdId ?: ""
                    val finalPlan = (resolvedUser.plan ?: authData.plan ?: "FREE").uppercase()
                    val finalRole = resolvedUser.role ?: authData.role ?: "Representative"

                    // Persistimos el estado global serializado para consumo de los dashboards
                    val editor = prefs.edit()
                    editor.putString("householdId", finalHouseholdId)

                    val userJson = JSONObject().apply {
                        put("id", resolvedUser.id)
                        put("name", resolvedUser.name ?: "Usuario")
                        put("email", resolvedUser.email ?: email)
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

    fun signUp(newUser: User, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.webService.signUp(newUser)
                if (response.isSuccessful) {
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

    fun clearError() { errorMessage = null }
}