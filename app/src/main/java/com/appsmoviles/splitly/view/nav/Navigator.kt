package com.appsmoviles.splitly.view.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.iam.LogIn
import com.appsmoviles.splitly.view.iam.SignUp
import com.appsmoviles.splitly.view.reports.OfflineReportScreen
import com.appsmoviles.splitly.view.reports.ReportScreen
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import com.appsmoviles.splitly.viewmodel.ReportViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@Composable
fun Navigator(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    householdViewModel: HouseholdViewModel,
    householdMemberViewModel: HouseholdMemberViewModel,
    billViewModel: BillViewModel,
    contributionViewModel: ContributionViewModel,
    reportViewModel: ReportViewModel,
    context: Context
) {

    val rememberScreen = rememberNavController()
    val scope = rememberCoroutineScope()


    NavHost(navController = rememberScreen,
        startDestination = "LogIn") {
        composable("LogIn") { LogIn(rememberScreen, authViewModel) }
        composable("SignUp") { SignUp(rememberScreen, authViewModel) }
        composable("OfflineReportScreen") { OfflineReportScreen(reportViewModel, context, rememberScreen) }
        composable("Main") {
            MainScreen(
                rootNav = rememberScreen,
                dashboardViewModel = dashboardViewModel,
                settingsViewModel = settingsViewModel,
                householdViewModel = householdViewModel,
                householdMemberViewModel = householdMemberViewModel,
                billViewModel = billViewModel,
                contributionViewModel = contributionViewModel,
                authViewModel = authViewModel,
                reportViewModel = reportViewModel,
                context = context,
                scope = scope
            )
        }
    }
}
