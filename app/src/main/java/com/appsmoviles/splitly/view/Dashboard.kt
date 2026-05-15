// Ubicación: com.appsmoviles.splitly.view.Dashboard.kt
package com.appsmoviles.splitly.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel
import java.util.Locale

@Composable
fun Dashboard(
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(context) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1E6DFF))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            // Sección de Bienvenida (Top Header)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bienvenido, ${uiState.userName}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Administra tu hogar con claridad",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = "Plan ${uiState.userPlan}",
                                color = Color(0xFF1E40AF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = "Hogar Primario: ${uiState.householdId}",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjetas de Métricas (KPI Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Miembros",
                    value = uiState.totalMembers.toString(),
                    trendText = "↑ 0% vs mes pasado",
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Gastos Totales",
                    value = String.format(Locale.US, "S/ %.0f", uiState.totalExpenses),
                    trendText = "—",
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f),
                    isNeutralTrend = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MetricCard(
                title = "Aportes Totales",
                value = String.format(Locale.US, "S/ %.0f", uiState.totalContributions),
                trendText = "↑ 0% acumulado",
                icon = Icons.Default.BarChart,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Accesos Rápidos (Color Cards con gradientes)
            val actionItems = listOf(
                DashboardActionItem("Gestionar Miembros", "Administra los miembros del hogar", listOf(Color(0xFF1E6DFF), Color(0xFF6D5BFF)), Icons.Default.People, "Members"),
                DashboardActionItem("Gestionar Gastos", "Administra los gastos del hogar", listOf(Color(0xFFFF8C3A), Color(0xFFFFB703)), Icons.Default.AccountBalanceWallet, "Expenses"),
                DashboardActionItem("Gestionar Aportes", "Administra los aportes del hogar", listOf(Color(0xFF6D5BFF), Color(0xFFB07BFF)), Icons.Default.BarChart, "Contributions"),
                DashboardActionItem("Configuracion", "Ajusta las preferencias del hogar", listOf(Color(0xFF06B6D4), Color(0xFF22D3EE)), Icons.Default.Settings, "Settings"),
                DashboardActionItem("Gestionar Hogares", "Crea y administra tus hogares", listOf(Color(0xFF22C55E), Color(0xFF16A34A)), Icons.Default.Home, "Households")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(actionItems) { item ->
                    ActionGradientCard(item = item, onClick = { onNavigate(item.targetRoute) })
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    trendText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isNeutralTrend: Boolean = false
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A), modifier = Modifier.padding(top = 2.dp))
                }
            }
            Text(
                text = trendText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isNeutralTrend) Color(0xFF94A3B8) else Color(0xFF22C55E),
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun ActionGradientCard(
    item: DashboardActionItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(item.gradientColors))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = item.subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

data class DashboardActionItem(
    val title: String,
    val subtitle: String,
    val gradientColors: List<Color>,
    val icon: ImageVector,
    val targetRoute: String
)