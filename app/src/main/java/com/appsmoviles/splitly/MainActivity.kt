// Ubicación: com.appsmoviles.splitly.MainActivity.kt
package com.appsmoviles.splitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appsmoviles.splitly.model.client.RetrofitClient
import com.appsmoviles.splitly.ui.theme.SplitlyTheme
import com.appsmoviles.splitly.view.nav.Navigator
import com.appsmoviles.splitly.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INICIALIZACIÓN CRÍTICA DE RED: Permite interceptar el token nativamente
        RetrofitClient.initialize(this)

        // 2. Instanciamos AuthViewModel inyectando el contexto nativo para que pueda usar SharedPreferences
        val authViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(applicationContext) as T
                }
            }
        )[AuthViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SplitlyTheme {
                Navigator(authViewModel)
            }
        }
    }
}