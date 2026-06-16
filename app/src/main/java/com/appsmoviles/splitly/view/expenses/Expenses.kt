package com.appsmoviles.splitly.view.expenses

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(
    context: Context,
    navController: NavHostController,
    contributionViewModel: ContributionViewModel = viewModel()
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val today = Calendar.getInstance()
    val deadlineDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 15) }

    var activeHouseholdId by remember { mutableStateOf("") }
    var creatorId by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("splitly_prefs", Context.MODE_PRIVATE)
        activeHouseholdId = prefs.getString("householdId", "") ?: ""
        val userStr = prefs.getString("user", null)
        if (userStr != null) {
            try {
                creatorId = JSONObject(userStr).optInt("id", -1)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Gasto", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Agrega un nuevo recibo y Splitly lo dividirá automáticamente entre los miembros de tu hogar.",
                color = Color(0xFF64748B),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción del Gasto (Ej. Internet)") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF6366F1)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Monto Total") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF10B981)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = "Vence en 15 días",
                        onValueChange = { },
                        label = { Text("Fecha límite para miembros") },
                        leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFF59E0B)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false, // Solo lectura por ahora
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (contributionViewModel.isLoading) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            } else {
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull()
                        if (description.isBlank() || parsedAmount == null || parsedAmount <= 0) {
                            Toast.makeText(context, "Por favor llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (activeHouseholdId.isEmpty() || creatorId == -1) {
                            Toast.makeText(context, "Error de sesión. Vuelve a ingresar.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        contributionViewModel.createFullExpense(
                            householdId = activeHouseholdId,
                            description = description,
                            totalAmount = parsedAmount,
                            creatorId = creatorId,
                            paymentDate = sdf.format(today.time),
                            deadline = sdf.format(deadlineDate.time)
                        ) {
                            Toast.makeText(context, "¡Gasto dividido y notificado con éxito!", Toast.LENGTH_LONG).show()
                            description = ""
                            amount = ""
                            navController.navigate("Dashboard") { popUpTo("Expenses") { inclusive = true } }                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dividir Gasto", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (contributionViewModel.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = contributionViewModel.errorMessage!!, color = Color.Red, fontSize = 14.sp)
                }
            }
        }
    }
}