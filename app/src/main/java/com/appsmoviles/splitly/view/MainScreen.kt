package com.appsmoviles.splitly.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.nav.AppNavigationBar
import com.appsmoviles.splitly.view.nav.Drawer
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(rootNav: NavHostController, dashboardViewModel: DashboardViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Obtenemos el contexto aquí para pasarlo al Dashboard

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
                // AQUÍ: Inyectamos ViewModel, Context y el Controlador de Navegación
                composable("Dashboard") { Dashboard(dashboardViewModel, context, navController) }
                composable("Expenses") { Expenses() }
                composable("Households") { Households() }
                composable("Members") { Members() }
                composable("Settings") { Settings() }
                composable("Contributions") { Contributions() }
            }
        }
    }
}