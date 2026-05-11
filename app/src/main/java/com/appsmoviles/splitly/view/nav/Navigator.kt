package com.appsmoviles.splitly.view.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.Contributions
import com.appsmoviles.splitly.view.Dashboard
import com.appsmoviles.splitly.view.Expenses
import com.appsmoviles.splitly.view.Households
import com.appsmoviles.splitly.view.MainScreen
import com.appsmoviles.splitly.view.Members
import com.appsmoviles.splitly.view.Settings
import com.appsmoviles.splitly.view.iam.LogIn
import com.appsmoviles.splitly.view.iam.SignUp
import com.appsmoviles.splitly.viewmodel.AuthViewModel

@Composable
fun Navigator(authViewModel: AuthViewModel){


    var rememberScreen = rememberNavController()

    NavHost(navController = rememberScreen, startDestination = "LogIn"){
        composable("LogIn") { LogIn(rememberScreen, authViewModel) }
        composable("SignUp") { SignUp(rememberScreen, authViewModel) }
        composable("Main") { MainScreen(rememberScreen) }
    }
}