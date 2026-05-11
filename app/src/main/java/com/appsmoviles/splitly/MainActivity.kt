package com.appsmoviles.splitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.appsmoviles.splitly.ui.theme.SplitlyTheme
import com.appsmoviles.splitly.view.nav.Navigator
import com.appsmoviles.splitly.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val authViewModel by viewModels<AuthViewModel>()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitlyTheme {
                Navigator(authViewModel)
            }
        }
    }
}
