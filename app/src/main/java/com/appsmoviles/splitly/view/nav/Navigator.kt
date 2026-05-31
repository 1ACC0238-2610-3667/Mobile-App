package com.appsmoviles.splitly.view.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.MainScreen
import com.appsmoviles.splitly.view.iam.LogIn
import com.appsmoviles.splitly.view.iam.SignUp
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel

@Composable
fun Navigator(authViewModel: AuthViewModel, dashboardViewModel: DashboardViewModel) {

    val rememberScreen = rememberNavController()

    NavHost(navController = rememberScreen, startDestination = "LogIn") {
        composable("LogIn") { LogIn(rememberScreen, authViewModel) }
        composable("SignUp") { SignUp(rememberScreen, authViewModel) }
        // Pasamos el DashboardViewModel a MainScreen
        composable("Main") { MainScreen(rememberScreen, dashboardViewModel) }
    }
}