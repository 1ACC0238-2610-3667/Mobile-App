package com.appsmoviles.splitly.view.members

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.appsmoviles.splitly.model.beans.iam.User
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Members(
    context: Context,
    navController: NavHostController,
    viewModel: HouseholdMemberViewModel = viewModel()
) {
    var activeHouseholdId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        activeHouseholdId = prefs.getString("householdId", "") ?: ""

        if (activeHouseholdId.isNotEmpty()) {
            viewModel.getHouseholdMembersByHouseholdId(listOf(Household(id = activeHouseholdId)))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Miembros del Hogar", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (activeHouseholdId.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ve a 'Mis Hogares' y selecciona uno para administrar.", color = Color.Gray)
            }
        } else if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            val members = viewModel.householdMembers[activeHouseholdId] ?: emptyList()

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Integrantes activos en esta casa:", color = Color(0xFF64748B), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (members.isEmpty()) {
                    item { Text("No hay miembros unidos todavía.", color = Color.Gray) }
                } else {
                    items(members.filterNotNull()) { user ->
                        MemberItemCard(user)
                    }
                }
            }
        }
    }
}

@Composable
fun MemberItemCard(user: User) {
    val displayName = user.personName?.takeIf { it.isNotEmpty() } ?: "Usuario Nuevo"
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
                modifier = Modifier.size(48.dp).background(Color(0xFFE0F2FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.take(1).uppercase(),
                    color = Color(0xFF0284C7),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Text(text = user.email ?: "Sin correo", fontSize = 13.sp, color = Color(0xFF64748B))            }
            // Etiqueta de Rol
            Box(
                modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = user.role ?: "Member", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
            }
        }
    }
}