package com.appsmoviles.splitly.view.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import android.content.Context
import com.appsmoviles.splitly.R
import com.appsmoviles.splitly.util.LocalTranslation

@Composable
fun Drawer(nav: NavHostController, onCloseDrawer: () -> Unit, onLogOut: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE) }
    val t = LocalTranslation.current

    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp, vertical = 24.dp)
    ) {
        // Drawer Header
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splitlylogo),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = t["app_title"] ?: "Splitly",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = t["app_subtitle"] ?: "Manage your expenses easily",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))

        val menuItems = listOf(
            DrawerItem(t["dashboard"] ?: "Dashboard", "Dashboard", Icons.Rounded.Dashboard),
            DrawerItem(t["households"] ?: "Households", "Households", Icons.Rounded.House),
            DrawerItem(t["expenses"] ?: "Expenses", "Expenses", Icons.Rounded.Wallet),
            DrawerItem(t["contributions"] ?: "Contributions", "Contributions", Icons.Rounded.BarChart),
            DrawerItem(t["settings"] ?: "Settings", "Settings", Icons.Rounded.Settings)
        )

        menuItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            
            NavigationDrawerItem(
                label = { 
                    Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(start = 8.dp)
                    ) 
                },
                selected = selected,
                onClick = {
                    nav.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    onCloseDrawer()
                },
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                },
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    unselectedContainerColor = Color.Transparent,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))

        NavigationDrawerItem(
            label = { 
                Text(
                    text = t["log_out"] ?: "Log Out",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 8.dp)
                ) 
            },
            selected = false,
            onClick = {
                sharedPreferences.edit().clear().apply()
                onLogOut()
            },
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                ) 
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

data class DrawerItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
