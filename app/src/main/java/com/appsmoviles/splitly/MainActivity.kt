package com.appsmoviles.splitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.model.client.OkHttpClientObject
import com.appsmoviles.splitly.ui.theme.SplitlyTheme
import com.appsmoviles.splitly.view.nav.Navigator
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import com.appsmoviles.splitly.viewmodel.ReportsViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel

class MainActivity : ComponentActivity() {

    private val dashboardViewModel by viewModels<DashboardViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val householdViewModel by viewModels<HouseholdViewModel>()
    private val householdMemberViewModel by viewModels<HouseholdMemberViewModel>()
    private val billViewModel by viewModels<BillViewModel>()

    private val contributionViewModel by viewModels<ContributionViewModel>()

    private val reportViewModel by viewModels<ReportsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        OkHttpClientObject.init(this)
        CredentialsSessionManager.init(this)

        enableEdgeToEdge()
        setContent {
            val isDarkMode = com.appsmoviles.splitly.model.client.CredentialsSessionManager.getDarkMode1()
            val language = com.appsmoviles.splitly.model.client.CredentialsSessionManager.getLanguage1()
            val translations = if (language == "en") com.appsmoviles.splitly.utils.Translations.en else com.appsmoviles.splitly.utils.Translations.es

            androidx.compose.runtime.CompositionLocalProvider(com.appsmoviles.splitly.utils.LocalTranslations provides translations) {
                SplitlyTheme(darkTheme = isDarkMode) {
                    Navigator(authViewModel,
                        dashboardViewModel, settingsViewModel, householdViewModel, householdMemberViewModel,
                        billViewModel, contributionViewModel, reportViewModel,this)
                }
            }
        }
    }
}