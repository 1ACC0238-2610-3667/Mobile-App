package com.appsmoviles.splitly.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.appmanagement.Settings
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.model.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SettingsViewModel: ViewModel() {

    var settings by mutableStateOf<Settings?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadSettings(context: Context) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        val userJsonStr = prefs.getString("user", null)
        
        if (!userJsonStr.isNullOrEmpty()) {
            try {
                val json = JSONObject(userJsonStr)
                val userId = json.optInt("id", -1)
                
                if (userId != -1) {
                    isLoading = true
                    viewModelScope.launch {
                        try {
                            val response = RetrofitClient.settingsWebService.getSettingByUserId(userId)
                            if (response.isSuccessful && response.body() != null) {
                                val loaded = response.body()!!
                                settings = loaded
                                syncLocalManager(loaded)
                            } else {
                                // Default or create new
                                val defaultLang = CredentialsSessionManager.getLanguage1().ifEmpty { "es" }
                                val defaultDark = CredentialsSessionManager.getDarkMode1()
                                val defaultNotif = CredentialsSessionManager.getNotificationEnabled1()
                                
                                val newSettingsResponse = withContext(Dispatchers.IO){
                                    RetrofitClient.settingsWebService.createSettings(
                                        Settings(
                                            id = 0,
                                            userId = userId,
                                            language = defaultLang,
                                            darkMode = defaultDark,
                                            notificationEnabled = defaultNotif,
                                            createdAt = "",
                                            updatedAt = ""
                                        ))
                                }
                                if (newSettingsResponse.isSuccessful && newSettingsResponse.body() != null) {
                                    val created = newSettingsResponse.body()!!
                                    settings = created
                                    syncLocalManager(created)
                                } else {
                                    errorMessage = "Error loading settings"
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message
                            // If network failure, load whatever is local
                            settings = Settings(
                                id = 0,
                                userId = userId,
                                language = CredentialsSessionManager.getLanguage1(),
                                darkMode = CredentialsSessionManager.getDarkMode1(),
                                notificationEnabled = CredentialsSessionManager.getNotificationEnabled1(),
                                createdAt = "",
                                updatedAt = ""
                            )
                        } finally {
                            isLoading = false
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error parsing user data"
            }
        }
    }

    fun updateSettings(updatedSettings: Settings) {
        // Optimistic local update (instant reaction)
        syncLocalManager(updatedSettings)
        settings = updatedSettings

        viewModelScope.launch {
            try {
                val response = RetrofitClient.settingsWebService.updateSetting(updatedSettings.id, updatedSettings)
                if (response.isSuccessful && response.body() != null) {
                    val saved = response.body()!!
                    settings = saved
                    syncLocalManager(saved)
                } else {
                    errorMessage = "Error syncing settings with server"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    private fun syncLocalManager(s: Settings) {
        CredentialsSessionManager.setDarkMode1(s.darkMode)
        CredentialsSessionManager.setLanguage1(s.language)
        CredentialsSessionManager.setNotificationsState(s.notificationEnabled)
    }
}
