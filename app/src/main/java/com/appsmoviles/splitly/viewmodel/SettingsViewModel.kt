package com.appsmoviles.splitly.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.appmanagement.Settings
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
                            if (response.isSuccessful) {
                                settings = response.body()
                            } else if(response.body() == null) {
                                val newSettingsResponse = withContext(Dispatchers.IO){
                                    RetrofitClient.settingsWebService.createSettings(
                                        Settings(
                                            id = 0,
                                            userId = userId,
                                            language = "",
                                            darkMode = false,
                                            notificationEnabled = false,
                                            createdAt = "",
                                            updatedAt = ""

                                        ))
                                }

                                settings = newSettingsResponse.body()
                            }else{
                                errorMessage = "Error loading settings"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message
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
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.settingsWebService.updateSetting(updatedSettings.id, updatedSettings)
                if (response.isSuccessful) {
                    settings = response.body()
                } else {
                    errorMessage = "Error updating settings"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
