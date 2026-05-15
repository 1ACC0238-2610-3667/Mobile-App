package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.util.LocalTranslation
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    nav: NavHostController
) {
    val context = LocalContext.current
    val t = LocalTranslation.current

    LaunchedEffect(Unit) {
        settingsViewModel.loadSettings(context)
    }

    val showDeleteDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(t["settings_title"] ?: "Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Preferences Section ──
            SectionCard(title = t["language"] ?: "Language") {
                PreferenceSwitchRow(
                    icon = Icons.Default.Language,
                    label = t["language_en"] ?: "English",
                    subLabel = t["language_es"] ?: "Español",
                    checked = settingsViewModel.language == "es",
                    onCheckedChange = { isSpanish ->
                        settingsViewModel.updateLanguage(if (isSpanish) "es" else "en")
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                PreferenceSwitchRow(
                    icon = Icons.Default.NightsStay,
                    label = t["dark_mode"] ?: "Dark Mode",
                    checked = settingsViewModel.darkMode,
                    onCheckedChange = { settingsViewModel.updateDarkMode(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                PreferenceSwitchRow(
                    icon = Icons.Default.Notifications,
                    label = t["email_notifications"] ?: "Email Notifications",
                    checked = settingsViewModel.notificationsEnabled,
                    onCheckedChange = { settingsViewModel.updateNotifications(it) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Action Buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { settingsViewModel.resetSettings(context) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(t["reset"] ?: "Reset")
                }
                Button(
                    onClick = { settingsViewModel.saveSettings(context) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(t["save_changes"] ?: "Save Changes")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Summary Section ──
            SectionCard(title = t["summary"] ?: "Summary") {
                SummaryRow(t["current_language"] ?: "Current Language",
                    if (settingsViewModel.language == "en") (t["en"] ?: "English") else (t["es"] ?: "Español"))
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                SummaryRow(t["current_dark_mode"] ?: "Dark Mode",
                    if (settingsViewModel.darkMode) (t["enabled"] ?: "Enabled") else (t["disabled"] ?: "Disabled"))
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                SummaryRow(t["current_notifications"] ?: "Notifications",
                    if (settingsViewModel.notificationsEnabled) (t["enabled"] ?: "Enabled") else (t["disabled"] ?: "Disabled"))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Registration Info Section ──
            SectionCard(title = t["registration_info"] ?: "Registration Info") {
                val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                val userId = prefs.getInt("user_id", 0)
                val userName = prefs.getString("user_name", "") ?: ""
                val userRole = authViewModel.user?.role ?: prefs.getString("user_role", "Member") ?: "Member"
                val createdAt = prefs.getString("created_at", "—") ?: "—"
                val lastUpdated = prefs.getString("last_updated", "—") ?: "—"

                SummaryRow(t["account_id"] ?: "Account ID", "#$userId")
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                SummaryRow(t["user_type"] ?: "User Type", userRole)
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                SummaryRow(t["created_at"] ?: "Created At", createdAt)
                if (lastUpdated != "—") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                    SummaryRow(t["last_updated"] ?: "Last Updated", lastUpdated)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Danger Zone ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = (t["danger_zone"] ?: "Danger Zone").uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = t["delete_warning"] ?: "This action will permanently delete your account...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showDeleteDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            (t["delete_account"] ?: "Delete Account").uppercase(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // ── Delete Confirmation Dialog ──
    if (showDeleteDialog.value) {
        Dialog(onDismissRequest = { showDeleteDialog.value = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = t["confirm_delete"] ?: "Are you sure?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDeleteDialog.value = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(t["cancel"] ?: "Cancel")
                        }
                        Button(
                            onClick = {
                                showDeleteDialog.value = false
                                val userId = authViewModel.user?.id ?: context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE).getInt("user_id", 0)
                                settingsViewModel.deleteAccount(userId, context) {
                                    nav.navigate("LogIn") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !settingsViewModel.isLoading
                        ) {
                            if (settingsViewModel.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text(t["confirm"] ?: "Confirm")
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Success Dialog ──
    if (settingsViewModel.successMessage != null) {
        Dialog(onDismissRequest = { settingsViewModel.clearMessages() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t[settingsViewModel.successMessage ?: "changes_saved"] ?: "Changes saved",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { settingsViewModel.clearMessages() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }

    // ── Error Dialog ──
    if (settingsViewModel.errorMessage != null) {
        Dialog(onDismissRequest = { settingsViewModel.clearMessages() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t[settingsViewModel.errorMessage ?: "unknown_error"] ?: "Error",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { settingsViewModel.clearMessages() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subLabel: String? = null
) {
    var internalChecked by remember { mutableStateOf(checked) }
    internalChecked = checked

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 14.sp)
            if (subLabel != null) {
                Text(text = subLabel, fontSize = 11.sp, color = Color.Gray)
            }
        }
        Switch(
            checked = internalChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
