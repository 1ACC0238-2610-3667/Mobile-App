package com.appsmoviles.splitly.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    var language by mutableStateOf("en")
        private set

    var darkMode by mutableStateOf(false)
        private set

    var notificationsEnabled by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun loadSettings(context: Context) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        language = prefs.getString("language", "en") ?: "en"
        darkMode = prefs.getBoolean("dark_mode", false)
        notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
    }

    fun updateLanguage(value: String) {
        language = value
    }

    fun updateDarkMode(value: Boolean) {
        darkMode = value
    }

    fun updateNotifications(value: Boolean) {
        notificationsEnabled = value
    }

    fun saveSettings(context: Context) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("language", language)
            putBoolean("dark_mode", darkMode)
            putBoolean("notifications_enabled", notificationsEnabled)
            putString("last_updated", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
            apply()
        }
        successMessage = "changes_saved"
    }

    fun resetSettings(context: Context) {
        loadSettings(context)
    }

    fun deleteAccount(userId: Int, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.webService.deleteUser(userId)
                if (response.isSuccessful) {
                    context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                        .edit().clear().apply()
                    onSuccess()
                } else {
                    errorMessage = "delete_error"
                }
            } catch (e: Exception) {
                Log.e("SettingsVM", "Delete account error", e)
                errorMessage = "delete_error"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
