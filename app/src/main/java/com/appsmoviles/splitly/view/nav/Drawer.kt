package com.appsmoviles.splitly.view.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.R

@Composable
fun Drawer(nav: NavHostController){
    Box(
        modifier = Modifier
            .height(180.dp)
            .width(180.dp)
            .padding(25.dp)
    ){
        Image(
            painter = painterResource(id= R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
                .size(128.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray,CircleShape)
        )
    }

    Text(
        text = "Splitly",
        fontSize = 25.sp,
        modifier = Modifier.padding(15.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(8.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
            )
        },
        label = {
            Text(
                text="Account",
                fontSize = 16.sp,
                modifier = Modifier.padding(15.dp)
            )
        },
        selected = false,
        onClick = {}
    )



    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
            )
        },
        label = {
            Text(
                text="Datos",
                fontSize = 16.sp,
                modifier = Modifier.padding(15.dp)
            )
        },
        selected = false,
        onClick = {}
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = null,
            )
        },
        label = {
            Text(
                text="Ajustes",
                fontSize = 16.sp,
                modifier = Modifier.padding(15.dp)
            )
        },
        selected = false,
        onClick = {}
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
            )
        },
        label = {
            Text(
                text="Descargas",
                fontSize = 16.sp,
                modifier = Modifier.padding(15.dp)
            )
        },
        selected = false,
        onClick = {}
    )

    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(8.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
            )
        },
        label = {
            Text(
                text="Salir",
                fontSize = 16.sp,
                modifier = Modifier.padding(15.dp)
            )
        },
        selected = false,
        onClick = {}
    )


}