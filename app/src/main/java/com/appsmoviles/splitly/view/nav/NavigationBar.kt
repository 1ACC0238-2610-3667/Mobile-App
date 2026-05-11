package com.appsmoviles.splitly.view.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun NavigationBar(nav: NavHostController, onOpenDrawer:()-> Unit){

    NavigationBar {

        NavigationBarItem(
            selected = false,
            label = {
                Text(text = "Dashboard")
            },
            onClick = {

            },
            icon = {
                Icon(
                    Icons.Default.Dashboard,
                    contentDescription = null
                )
            }
        )

        NavigationBarItem(
            selected = false,
            label = {
                Text(text = "More")
            },
            onClick = {
                onOpenDrawer()
            },
            icon = {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null
                )
            }
        )

        NavigationBarItem(
            selected = false,
            label = {
                Text(text = "Settings")
            },
            onClick = {

            },
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null
                )
            }
        )

    }

}

