package com.appsmoviles.splitly.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel // Necesario para instanciar el ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.nav.AppNavigationBar
import com.appsmoviles.splitly.view.nav.Drawer
import com.appsmoviles.splitly.viewmodel.MyContributionsViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(rootNav: NavHostController, dashboardViewModel: DashboardViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        dashboardViewModel.loadInternalData(context)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Drawer(
                    nav = navController,
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    },
                    onLogOut = {
                        scope.launch {
                            drawerState.close()
                            rootNav.navigate("LogIn") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                AppNavigationBar(
                    navController = navController,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "Dashboard",
                modifier = Modifier.padding(padding)
            ) {

                composable("Dashboard") {
                    if (dashboardViewModel.userRole.equals("Representative", ignoreCase = true) ||
                        dashboardViewModel.userRole.equals("Admin", ignoreCase = true)) {

                        Dashboard(viewModel = dashboardViewModel, context = context, nav = navController)
                    } else {
                        MemberDashboard(viewModel = dashboardViewModel, context = context, nav = navController)
                    }
                }

                composable("Expenses") { Expenses() }
                composable("Households") { Households() }
                composable("Members") { Members() }
                composable("Settings") { Settings() }
                composable("Contributions") { Contributions() }

                composable("my_contributions") {
                    val myContributionsViewModel: MyContributionsViewModel = viewModel()
                    MyContributionsScreen(viewModel = myContributionsViewModel, context = context)
                }

                composable("household_details") {
                    Households()
                }
            }
        }
    }
}