package com.appsmoviles.splitly.view.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.Contributions
import com.appsmoviles.splitly.view.Dashboard
import com.appsmoviles.splitly.view.Expenses
import com.appsmoviles.splitly.view.Households
import com.appsmoviles.splitly.view.Members
import com.appsmoviles.splitly.view.Settings
import com.appsmoviles.splitly.view.auth.LogIn
import com.appsmoviles.splitly.view.auth.SignUp

@Composable
fun Navigator(){

    var rememberScreen = rememberNavController()

    NavHost(navController = rememberScreen, startDestination = "LogIn"){
        composable("LogIn") { LogIn(rememberScreen) }
        composable("SignUp") { SignUp(rememberScreen) }
        composable("Contributions") { Contributions() }
        composable("Dashboard") { Dashboard() }
        composable("Expenses") { Expenses() }
        composable("Households") { Households() }
        composable("Members") { Members() }
        composable("Settings") { Settings() }

    }
}