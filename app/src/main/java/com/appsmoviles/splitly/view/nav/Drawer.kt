package com.appsmoviles.splitly.view.nav

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.appsmoviles.splitly.R
import com.appsmoviles.splitly.utils.LocalTranslations
import org.json.JSONObject
import java.util.Locale

@Composable
fun Drawer(nav: NavHostController, onCloseDrawer: () -> Unit, onLogOut: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE) }
    val strings = LocalTranslations.current

    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var userName by remember { mutableStateOf(strings["user_default"] ?: "Usuario") }
    var userRole by remember { mutableStateOf("Member") }

    LaunchedEffect(Unit) {
        val userStr = sharedPreferences.getString("user", null)
        if (userStr != null) {
            val json = JSONObject(userStr)
            val rawName = json.optString("name", "")
            val email = json.optString("email", "")

            userName = rawName.ifBlank {
                email.substringBefore("@").replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()) else it.toString() }
            }.ifBlank { "Usuario" }

            userRole = json.optString("role", "Member")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp, vertical = 24.dp)
    ) {
        // Drawer Header
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
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
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = userRole,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))

        val menuItems = if (userRole.equals("Representative", ignoreCase = true) || userRole.equals("Admin", ignoreCase = true)) {
            listOf(
                DrawerItem(strings["dashboard_nav"] ?: "", "Main", Icons.Rounded.Dashboard),
                DrawerItem(strings["households_nav"] ?: "", "Households", Icons.Rounded.House),
                DrawerItem(strings["expenses_nav"] ?: "", "Expenses", Icons.Rounded.Wallet),
                DrawerItem(strings["contributions_nav"] ?: "", "Contributions", Icons.Rounded.BarChart),
                DrawerItem(strings["settings_nav"] ?: "", "Settings", Icons.Rounded.Settings)
            )
        } else {
            listOf(
                DrawerItem(strings["dashboard_nav"] ?: "", "Main", Icons.Rounded.Dashboard),
                DrawerItem(strings["my_quotas_nav"] ?: "", "my_contributions", Icons.Rounded.Payments), // <-- Nueva vista del miembro
                DrawerItem(strings["household_nav"] ?: "", "household_details", Icons.Rounded.House),
                DrawerItem(strings["settings_nav"] ?: "", "Settings", Icons.Rounded.Settings)
            )
        }

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
            label = { Text(text = strings["logout_button"] ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp)) },
            selected = false,
            onClick = {
                sharedPreferences.edit().clear().apply()
                onLogOut()
            },
            icon = { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
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