package com.appsmoviles.splitly.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
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
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel

import java.util.Locale

@Composable
fun Dashboard(viewModel: DashboardViewModel, context: Context, navController: NavHostController) {

    LaunchedEffect(Unit) {
        viewModel.loadSummary(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome back,",
            fontSize = 16.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = viewModel.userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else if (viewModel.errorMessage != null) {
            Text(text = viewModel.errorMessage!!, color = Color.Red)
        } else {
            // Summary Cards
            SummaryCard(
                title = "Total Households",
                value = viewModel.totalHouseholdsCount.toString(),
                icon = Icons.Default.Home,
                iconColor = Color(0xFF6366F1)
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryCard(
                title = "Total Members",
                value = viewModel.totalMembersCount.toString(),
                icon = Icons.Default.Group,
                iconColor = Color(0xFF10B981)
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryCard(
                title = "Total Bills",
                value = viewModel.totalBillsCount.toString(),
                icon = Icons.Default.Receipt,
                iconColor = Color(0xFFF59E0B)
            )
            Spacer(modifier = Modifier.height(12.dp))
            SummaryCard(
                title = "Total Expenses",
                value = "$ ${String.format(Locale.US, "%.2f", viewModel.totalBillsAmount)}",
                icon = Icons.Default.Payments,
                iconColor = Color(0xFFEF4444)
            )
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, icon: ImageVector, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 14.sp, color = Color(0xFF64748B))
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
        }
    }
}
