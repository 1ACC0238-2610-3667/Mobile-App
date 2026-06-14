package com.appsmoviles.splitly.model.client

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject

object CredentialsSessionManager {

    private lateinit var sharedPreferences: SharedPreferences

    private var tokenStr: String = ""

    private var userId: Int by mutableIntStateOf(-1)

    private var language: String by mutableStateOf("")
    private var darkMode: Boolean by mutableStateOf(false)
    private var notificationEnabled: Boolean by mutableStateOf(false)

    fun init(context: Context){
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        sharedPreferences = prefs
        tokenStr = prefs.getString("token", "") ?: ""
        language = prefs.getString("language", "")?: ""
        darkMode = prefs.getBoolean("darkMode", false)
        notificationEnabled = prefs.getBoolean("notificationEnabled", false)
        val userJsonStr = prefs.getString("user", null)
        if(!userJsonStr.isNullOrEmpty()) {
            val json = JSONObject(userJsonStr)
            userId = json.optInt("id", -1)
        } else {
            userId = -1
        }
    }

    fun getToken() : String{
        return tokenStr
    }

    fun getIdFromUser(): Int{
        return userId
    }


    fun logout() {
        userId = -1
        tokenStr = ""
        sharedPreferences.edit().clear().apply()
    }

    fun getLanguage1(): String{
        return language
    }

    fun setLanguage1(auxLanguage: String){
        language = auxLanguage
        sharedPreferences.edit().putString("language", auxLanguage).apply()
    }
    fun getDarkMode1(): Boolean{
        return darkMode
    }

    fun setDarkMode1(auxDarkMode: Boolean){
        darkMode = auxDarkMode
        sharedPreferences.edit().putBoolean("darkMode", auxDarkMode).apply()
    }
    fun getNotificationEnabled1(): Boolean{
        return notificationEnabled
    }

    fun setNotificationsState(auxNotif: Boolean){
        notificationEnabled = auxNotif
        sharedPreferences.edit().putBoolean("notificationEnabled", auxNotif).apply()
    }
}
