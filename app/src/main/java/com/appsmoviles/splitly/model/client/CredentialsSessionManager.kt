package com.appsmoviles.splitly.model.client

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject

object CredentialsSessionManager {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tokenStr: String

    private var userId: Int by mutableIntStateOf(0)

    fun init(context: Context){
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        sharedPreferences = prefs
        tokenStr = prefs.getString("token", "") ?: ""
        val userJsonStr = prefs.getString("user", null)
        if(!userJsonStr.isNullOrEmpty()) {
            val json = JSONObject(userJsonStr)
            userId = json.optInt("id", -1)
        }

    }

    fun getToken() : String{
        if (!::tokenStr.isInitialized){
            throw IllegalStateException("TokenSessionManager not initialized. Call init(context) first.")
        }
        return tokenStr
    }

    fun getIdFromUser(): Int{
        if(userId != 0){
            throw IllegalStateException("UserId not initialized. Call init(context) first.")
        }
        return userId
    }
}