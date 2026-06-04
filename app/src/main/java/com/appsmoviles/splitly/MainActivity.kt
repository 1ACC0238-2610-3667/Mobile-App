package com.appsmoviles.splitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.appsmoviles.splitly.model.client.OkHttpClientObject
import com.appsmoviles.splitly.ui.theme.SplitlyTheme
import com.appsmoviles.splitly.view.nav.Navigator
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.HouseholdViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel

class MainActivity : ComponentActivity() {

    private val dashboardViewModel by viewModels<DashboardViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val householdViewModel by viewModels<HouseholdViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            SplitlyTheme {
                OkHttpClientObject.init(this)
                Navigator(authViewModel, dashboardViewModel, settingsViewModel, householdViewModel, this)
            }
        }
    }
}