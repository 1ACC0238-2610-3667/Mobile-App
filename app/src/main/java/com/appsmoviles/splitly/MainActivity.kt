package com.appsmoviles.splitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.appsmoviles.splitly.ui.theme.SplitlyTheme
import com.appsmoviles.splitly.view.nav.Navigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitlyTheme {
                Navigator()
            }
        }
    }
}
