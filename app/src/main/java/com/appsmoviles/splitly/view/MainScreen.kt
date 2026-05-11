package com.appsmoviles.splitly.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.nav.Drawer
import com.appsmoviles.splitly.view.nav.AppNavigationBar
import kotlinx.coroutines.launch

@Composable
fun MainScreen(rootNav: NavHostController) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                composable("Dashboard") { Dashboard() }
                composable("Expenses") { Expenses() }
                composable("Households") { Households() }
                composable("Members") { Members() }
                composable("Settings") { Settings() }
                composable("Contributions") { Contributions() }
            }
        }
    }
}
