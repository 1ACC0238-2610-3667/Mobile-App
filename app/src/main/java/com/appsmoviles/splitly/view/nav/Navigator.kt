package com.appsmoviles.splitly.view.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.iam.LogIn
import com.appsmoviles.splitly.view.iam.SignUp
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel

@Composable
fun Navigator(authViewModel: AuthViewModel, dashboardViewModel: DashboardViewModel,
              settingsViewModel: SettingsViewModel, householdViewModel: HouseholdViewModel,
              householdMemberViewModel: HouseholdMemberViewModel, billViewModel: BillViewModel,
              contributionViewModel: ContributionViewModel, context: Context) {

    val rememberScreen = rememberNavController()

    NavHost(navController = rememberScreen, startDestination = "LogIn") {
        composable("LogIn") { LogIn(rememberScreen, authViewModel) }
        composable("SignUp") { SignUp(rememberScreen, authViewModel) }
        composable("Main") {
            MainScreen(rememberScreen, dashboardViewModel, settingsViewModel,
                householdViewModel, householdMemberViewModel, billViewModel, contributionViewModel, context) }
    }
}