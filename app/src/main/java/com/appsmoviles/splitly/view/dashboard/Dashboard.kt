package com.appsmoviles.splitly.view.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.viewmodel.dashboard.ApprovalItem
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel
import java.util.Locale

@Composable
fun Dashboard(viewModel: DashboardViewModel, context: Context, navController: NavHostController) {

    LaunchedEffect(Unit) {
        if(viewModel.isLoading) viewModel.loadSummary(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Hola de nuevo,", fontSize = 16.sp, color = Color(0xFF64748B))
        Text(text = viewModel.email, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else if (viewModel.errorMessage != null) {
            Text(text = viewModel.errorMessage!!, color = Color.Red)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmallMetricCard(modifier = Modifier.weight(1f), title = "Hogares", value = "${viewModel.totalHouseholdsCount}", icon = Icons.Default.Home, color = Color(0xFF6366F1))
                        SmallMetricCard(modifier = Modifier.weight(1f), title = "Miembros", value = "${viewModel.totalMembersCount}", icon = Icons.Default.Group, color = Color(0xFF0EA5E9))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Salud Financiera", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MoneyCard(modifier = Modifier.weight(1f), title = "Recaudado", amount = viewModel.totalCollected, icon = Icons.Rounded.CheckCircle, color = Color(0xFF10B981))
                        MoneyCard(modifier = Modifier.weight(1f), title = "Por Cobrar", amount = viewModel.totalPending, icon = Icons.Rounded.Warning, color = Color(0xFFEF4444))
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Requieren tu Aprobación", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    }
                    Text("Pagos notificados por los miembros.", fontSize = 14.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (viewModel.approvalsNeeded.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No hay pagos pendientes de revisión.", color = Color(0xFF94A3B8))
                            }
                        }
                    }
                } else {
                    items(viewModel.approvalsNeeded) { item ->
                        ApprovalCard(
                            item = item,
                            onApprove = { viewModel.approvePayment(context, item.contributionId) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun SmallMetricCard(modifier: Modifier, title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 12.sp, color = Color(0xFF64748B))
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
        }
    }
}

@Composable
fun MoneyCard(modifier: Modifier = Modifier, title: String, amount: Double, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("S/ ${String.format(Locale.US, "%.2f", amount)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun ApprovalCard(item: ApprovalItem, onApprove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.memberName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Text("Hogar: ${item.householdName}", fontSize = 12.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Text("S/ ${String.format(Locale.US, "%.2f", item.amount)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFFF59E0B))
            }
            Button(
                onClick = onApprove,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Aprobar")
            }
        }
    }
}