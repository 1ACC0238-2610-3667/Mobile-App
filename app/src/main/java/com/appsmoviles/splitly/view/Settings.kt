package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.model.beans.appmanagement.Settings
import com.appsmoviles.splitly.model.client.CredentialsSessionManager
import com.appsmoviles.splitly.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(viewModel: SettingsViewModel, context: Context, navHostController: NavHostController) {

    DisposableEffect(Unit) {
        viewModel.loadSettings(context)

        onDispose {
            viewModel.settings?.let { currentSettings ->
                val newSettings = Settings(
                    currentSettings.id,
                    currentSettings.userId,
                    CredentialsSessionManager.getLanguage1(),
                    CredentialsSessionManager.getDarkMode1(),
                    CredentialsSessionManager.getNotificationEnabled1(),
                    currentSettings.createdAt,
                    currentSettings.updatedAt
                )
                viewModel.updateSettings(newSettings)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Preferences",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )

                        SettingsCard {
                            SettingsToggleRow(
                                icon = Icons.Default.DarkMode,
                                title = "Dark Mode",
                                checked = CredentialsSessionManager.getDarkMode1(),
                                onCheckedChange = {
                                    CredentialsSessionManager.setDarkMode1(it)
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color(0xFFF1F5F9)
                            )
                            SettingsToggleRow(
                                icon = Icons.Default.Notifications,
                                title = "Enable Notifications",
                                checked = CredentialsSessionManager.getNotificationEnabled1(),
                                onCheckedChange = {
                                    CredentialsSessionManager.setNotificationsState(it)
                                }
                            )
                        }

                        Text(
                            text = "Regional",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )

                        SettingsCard {
                            SettingsActionRow(
                                icon = Icons.Default.Language,
                                title = "Language",
                                value = CredentialsSessionManager.getLanguage1(),
                                onClick = {
                                    // Logic for changing language could go here
                                    // For now, let's just cycle between EN and ES as an example
                                    val nextLang =
                                        if (CredentialsSessionManager.getLanguage1() == "English") "Spanish" else "English"
                                    CredentialsSessionManager.setLanguage1(nextLang)
                                }
                            )
                        }


                    }

        }
    }

}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        content = content
    )
}

@Composable
fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6366F1))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, color = Color(0xFF1E293B))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1)
            )
        )
    }
}

@Composable
fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF6366F1))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 16.sp, color = Color(0xFF1E293B))
            }
            Text(text = value, fontSize = 14.sp, color = Color(0xFF64748B))
        }
    }
}
