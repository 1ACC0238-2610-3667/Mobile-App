package com.appsmoviles.splitly.viewmodel

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

class AuthViewModel : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, pas: String, onSuccess: () -> Unit) {
        Log.d("EMAIL", "$email")
        Log.d("PASS", "$pas")

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = LoginRequest(email, pas)
                val response = RetrofitClient.webService.login(request)
                Log.d("Response","$response")
                if (response.isSuccessful) {
                    user = response.body()
                    onSuccess()
                } else {
                    errorMessage = "Login failed: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
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
                    errorMessage = "Sign up failed: ${response.code()}"
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
