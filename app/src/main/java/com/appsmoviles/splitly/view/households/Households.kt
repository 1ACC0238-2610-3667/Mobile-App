package com.appsmoviles.splitly.view.households

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.model.beans.householdmanagement.Household
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Households(
    context: Context,
    navController: NavHostController,
    viewModel: HouseholdViewModel = viewModel(),
    dashboardViewModel: com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel = viewModel(),
    householdMemberViewModel: com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val translations = com.appsmoviles.splitly.utils.LocalTranslations.current
    var userId by remember { mutableStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                val userStr = prefs.getString("user", null)
                if (userStr != null) {
                    try {
                        userId = JSONObject(userStr).optInt("id", -1)
                        if (userId != -1) {
                            viewModel.getHouseholdsByRepresentativeId(userId)
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(translations["my_households"] ?: "Mis Hogares", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (userId != -1) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading && viewModel.lastUpdated > 0L,
            onRefresh = {
                if (userId != -1) {
                    viewModel.getHouseholdsByRepresentativeId(userId, forceRefresh = true)
                }
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = translations["household_invite_hint"] ?: "Crea un hogar, copia el código e invita a tus roomies.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (viewModel.households.isEmpty() && !viewModel.isLoading) {
                    item {
                        Text(
                            text = translations["no_households_hint"] ?: "You don't manage any household yet. Tap + to create one.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(viewModel.households.filterNotNull()) { household ->
                        val hasPendingDebts = viewModel.householdHasDebtsMap[household.id] ?: false
                        HouseholdCard(context, household, navController, dashboardViewModel, householdMemberViewModel, hasPendingDebts)
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (showDialog) {
            HouseholdDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, desc, currency, memberCount ->
                    viewModel.createHousehold(
                        name = name,
                        desc = desc,
                        currency = currency,
                        memberCount = memberCount,
                        userId = userId
                    ) {
                        showDialog = false
                        viewModel.lastUpdated = 0L
                        viewModel.getHouseholdsByRepresentativeId(userId, forceRefresh = true)
                        Toast.makeText(context, translations["household_created_success"] ?: "Hogar creado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun HouseholdCard(
    context: Context,
    household: Household,
    navController: NavHostController,
    dashboardViewModel: com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel,
    householdMemberViewModel: com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel,
    hasPendingDebts: Boolean = false
) {
    val translations = com.appsmoviles.splitly.utils.LocalTranslations.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp)).padding(10.dp)) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = household.name ?: "Hogar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${household.memberCount ?: 0} ${translations["members_count"] ?: "Miembros"}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val dotColor = if (hasPendingDebts) Color(0xFFEF4444) else Color(0xFF10B981)
                            val statusText = if (hasPendingDebts) (translations["pending_debts"] ?: "Deudas pendientes") else (translations["no_debts"] ?: "Al día")
                            
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(dotColor, CircleShape)
                            )
                            Text(
                                text = statusText,
                                fontSize = 12.sp,
                                color = dotColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                        val prevActiveId = prefs.getString("householdId", "")
                        prefs.edit().putString("householdId", household.id).apply()
                        if (prevActiveId != household.id) {
                            // Invalidate caching for active household details
                            dashboardViewModel.lastUpdated = 0L
                            householdMemberViewModel.lastUpdated = 0L
                        }
                        Toast.makeText(context, "${translations["administering"] ?: "Administrando:"} ${household.name}", Toast.LENGTH_SHORT).show()
                        navController.navigate("Dashboard")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(translations["administer"] ?: "Administrar", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = household.id ?: "Generando...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        household.id?.let {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("Código", it))
                            Toast.makeText(context, translations["code_copied"] ?: "Código copiado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}