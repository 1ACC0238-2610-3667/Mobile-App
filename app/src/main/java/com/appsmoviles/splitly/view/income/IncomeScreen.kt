package com.appsmoviles.splitly.view.income

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.viewmodel.HouseholdMemberViewModel
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    context: Context,
    navController: NavHostController,
    householdMemberViewModel: HouseholdMemberViewModel
) {
    val translations = com.appsmoviles.splitly.utils.LocalTranslations.current
    var income by remember { mutableStateOf("") }
    
    var activeHouseholdId by remember { mutableStateOf("") }
    var currentMemberId by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
    val prefHouseholdId = prefs.getString("householdId", "") ?: ""
    val userStr = prefs.getString("user", null)
    
    LaunchedEffect(prefHouseholdId) {
        if (prefHouseholdId.isNotEmpty()) {
            activeHouseholdId = prefHouseholdId
            val members = householdMemberViewModel.householdMembers[prefHouseholdId]
            if (members.isNullOrEmpty()) {
                householdMemberViewModel.getHouseholdMembersByHouseholdId(
                    listOf(com.appsmoviles.splitly.model.beans.householdmanagement.Household(id = prefHouseholdId)),
                    forceRefresh = true
                )
            }
        }
    }

    val userId = if (userStr != null) {
        try { JSONObject(userStr).optInt("id", -1) } catch (e: Exception) { -1 }
    } else -1
    val userEmail = if (userStr != null) {
        try { JSONObject(userStr).optString("email", "") } catch (e: Exception) { "" }
    } else ""

    val lastUpdated = householdMemberViewModel.lastUpdated
    val membersList = householdMemberViewModel.householdMembers[activeHouseholdId]

    val currentMember = remember(membersList, lastUpdated, userId, userEmail) {
        membersList?.find { 
            (it.user.id != null && it.user.id == userId) || 
            (userEmail.isNotEmpty() && it.user.email == userEmail) 
        }
    }

    LaunchedEffect(currentMember) {
        if (currentMember != null) {
            currentMemberId = currentMember.memberId
            if (!initialLoadDone && currentMember.income > 0) {
                income = currentMember.income.toString()
                initialLoadDone = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(translations["my_income_title"] ?: "Mi Ingreso Mensual", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = translations["income_desc"] ?: "Define tu ingreso para habilitar la división proporcional de gastos.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = income,
                onValueChange = { income = it },
                label = { Text(translations["income_amount"] ?: "Ingreso (Ej. 1500)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF10B981)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (householdMemberViewModel.isLoading && currentMemberId.isEmpty()) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = {
                        val parsedIncome = income.toDoubleOrNull()
                        if (parsedIncome == null || parsedIncome <= 0) {
                            Toast.makeText(context, translations["invalid_income"] ?: "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (currentMemberId.isEmpty()) {
                            Toast.makeText(context, translations["member_not_found"] ?: "No se encontró el perfil en este hogar.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        saving = true
                        householdMemberViewModel.updateMemberIncome(
                            memberId = currentMemberId,
                            income = parsedIncome
                        ) { success ->
                            saving = false
                            if (success) {
                                Toast.makeText(context, translations["income_saved"] ?: "Ingreso guardado exitosamente", Toast.LENGTH_SHORT).show()
                                householdMemberViewModel.lastUpdated = 0L 
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, translations["income_error"] ?: "Error al guardar el ingreso", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !saving
                ) {
                    if (saving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(end = 8.dp))
                    }
                    Text(translations["save_btn"] ?: "Guardar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (householdMemberViewModel.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = householdMemberViewModel.errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }
        }
    }
}
