package com.appsmoviles.splitly.view.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel // <-- Importante para instanciar ViewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.MainScreen
import com.appsmoviles.splitly.view.MemberDashboard
import com.appsmoviles.splitly.view.MyContributionsScreen
import com.appsmoviles.splitly.view.iam.LogIn
import com.appsmoviles.splitly.view.iam.SignUp
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.MyContributionsViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel

@Composable
fun Navigator(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    context: Context,
    navController: NavHostController // Este suele usarse como root si lo pasas desde MainActivity
) {
    val rememberScreen = rememberNavController()

    val myContributionsViewModel: MyContributionsViewModel = viewModel()

    NavHost(navController = rememberScreen, startDestination = "LogIn") {
        composable("LogIn") { LogIn(rememberScreen, authViewModel) }
        composable("SignUp") { SignUp(rememberScreen, authViewModel) }

        composable("Main") { MainScreen(rememberScreen, dashboardViewModel) }

        composable("member_dashboard") { MemberDashboard(dashboardViewModel, LocalContext.current, rememberScreen) }

        composable("my_contributions") {
            MyContributionsScreen(viewModel = myContributionsViewModel, context = LocalContext.current)
        }
    }
}