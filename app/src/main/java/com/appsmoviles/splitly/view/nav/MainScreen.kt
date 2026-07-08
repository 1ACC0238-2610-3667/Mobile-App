package com.appsmoviles.splitly.view.nav

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appsmoviles.splitly.view.Contributions
import com.appsmoviles.splitly.view.SettingsScreen
import com.appsmoviles.splitly.view.dashboard.Dashboard
import com.appsmoviles.splitly.view.expenses.Expenses
import com.appsmoviles.splitly.view.households.Households
import com.appsmoviles.splitly.view.members.Members
import com.appsmoviles.splitly.view.income.IncomeScreen
import com.appsmoviles.splitly.view.reports.ReportScreen
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import com.appsmoviles.splitly.viewmodel.ReportViewModel
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    rootNav: NavHostController,
    dashboardViewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    householdViewModel: HouseholdViewModel,
    householdMemberViewModel: HouseholdMemberViewModel,
    billViewModel: BillViewModel,
    contributionViewModel: ContributionViewModel,
    authViewModel: AuthViewModel,
    reportViewModel: ReportViewModel,
    context: Context
) {
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
                            authViewModel.logout(context) {
                                rootNav.navigate("LogIn") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold { padding ->
            NavHost(
                navController = navController,
                startDestination = "Dashboard",
                modifier = Modifier.padding()
            ) {
                composable("Dashboard") {
                    Dashboard(
                        viewModel = dashboardViewModel,
                        context = context,
                        navController = navController,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("Expenses") {
                    Expenses(
                        context = context,
                        navController = navController,
                        contributionViewModel = contributionViewModel,
                        dashboardViewModel = dashboardViewModel,
                        billViewModel = billViewModel,
                        householdMemberViewModel = householdMemberViewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("Households") {
                    Households(
                        context = context,
                        navController = navController,
                        viewModel = householdViewModel,
                        dashboardViewModel = dashboardViewModel,
                        householdMemberViewModel = householdMemberViewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("Members") {
                    Members(
                        context = context,
                        navController = navController,
                        viewModel = householdMemberViewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("Settings") {
                    SettingsScreen(
                        context = context,
                        navController = rootNav,
                        settingsViewModel = settingsViewModel,
                        authViewModel = authViewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigateToIncome = { navController.navigate("Income") }
                    )
                }
                composable("Contributions") {
                    Contributions(
                        context = context,
                        navController = navController,
                        billViewModel = billViewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("Reports") {
                    ReportScreen(
                        reportViewModel = reportViewModel,
                        householdViewModel = householdViewModel,
                        billViewModel = billViewModel,
                        contributionViewModel = contributionViewModel,
                        isOnline = true
                    )
                }
                composable("Income") {
                    IncomeScreen(
                        context = context,
                        navController = navController,
                        householdMemberViewModel = householdMemberViewModel
                    )
                }
            }
        }
    }
}

