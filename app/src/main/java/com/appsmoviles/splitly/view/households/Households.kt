package com.appsmoviles.splitly.view.households

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    viewModel: HouseholdViewModel = viewModel()
) {
    var userId by remember { mutableStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Hogares", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (userId != -1) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Hogar")
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Crea un hogar, copia el código e invita a tus roomies.",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (viewModel.households.isEmpty()) {
                    item {
                        Text("No administras ningún hogar todavía. Toca el botón + para crear uno.", color = Color.Gray)
                    }
                } else {
                    items(viewModel.households.filterNotNull()) { household ->
                        HouseholdCard(context, household, navController)
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
                        viewModel.getHouseholdsByRepresentativeId(userId) // Recargamos la lista
                        Toast.makeText(context, "Hogar creado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun HouseholdCard(context: Context, household: Household, navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(Color(0xFFE0E7FF), RoundedCornerShape(8.dp)).padding(10.dp)) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF4F46E5))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = household.name ?: "Hogar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text(text = "${household.memberCount ?: 0} Miembros", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }

                Button(
                    onClick = {
                        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("householdId", household.id).apply()
                        Toast.makeText(context, "Administrando: ${household.name}", Toast.LENGTH_SHORT).show()
                        navController.navigate("Dashboard")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Administrar", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = household.id ?: "Generando...", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = Color(0xFF334155))
                IconButton(
                    onClick = {
                        household.id?.let {
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("Código", it))
                            Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = Color(0xFF6366F1))
                }
            }
        }
    }
}