package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.appsmoviles.splitly.viewmodel.BillViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contributions(
    context: Context,
    navController: NavHostController,
    billViewModel: BillViewModel = viewModel()
) {
    var activeHouseholdId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        activeHouseholdId = prefs.getString("householdId", "") ?: ""
        if (activeHouseholdId.isNotEmpty()) {
            billViewModel.getBillByHouseHoldId(activeHouseholdId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Gastos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (activeHouseholdId.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Selecciona un hogar en 'Mis Hogares'", color = Color.Gray)
            }
        } else if (billViewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            val bills = billViewModel.billsList

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Todos los recibos creados en este hogar:", color = Color(0xFF64748B), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (bills.isEmpty()) {
                    item { Text("No has registrado ningún gasto.", color = Color.Gray) }
                } else {
                    items(bills.filterNotNull()) { bill ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)).padding(10.dp)
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFFD97706))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = bill.description ?: "Gasto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))

                                    val dateStr = try {
                                        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                        val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                                        val date = parser.parse(bill.paymentDate ?: "")
                                        if (date != null) formatter.format(date) else "Sin fecha"
                                    } catch (e: Exception) { "Sin fecha" }

                                    Text(text = dateStr, fontSize = 13.sp, color = Color(0xFF64748B))
                                }
                                Text(text = "S/ ${"%.2f".format(bill.amount ?: 0.0)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }
        }
    }
}