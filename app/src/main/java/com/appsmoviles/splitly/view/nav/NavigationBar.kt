package com.appsmoviles.splitly.view.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavigationBar(navController: NavHostController, onOpenDrawer: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val dashboardSelected = currentDestination?.hierarchy?.any { it.route == "Dashboard" } == true
        NavigationBarItem(
            selected = dashboardSelected,
            label = {
                Text(
                    text = "Dashboard",
                    fontSize = 12.sp,
                    fontWeight = if (dashboardSelected) FontWeight.Bold else FontWeight.Normal
                )
            },
            onClick = {
                navController.navigate("Dashboard") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            selected = false,
            label = {
                Text(
                    text = "More",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            onClick = {
                onOpenDrawer()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }
        )

        val settingsSelected = currentDestination?.hierarchy?.any { it.route == "Settings" } == true
        NavigationBarItem(
            selected = settingsSelected,
            label = {
                Text(
                    text = "Settings",
                    fontSize = 12.sp,
                    fontWeight = if (settingsSelected) FontWeight.Bold else FontWeight.Normal
                )
            },
            onClick = {
                navController.navigate("Settings") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}
