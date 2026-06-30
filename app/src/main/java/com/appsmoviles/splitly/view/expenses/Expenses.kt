package com.appsmoviles.splitly.view.expenses

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    contributionViewModel: ContributionViewModel = viewModel(),
    dashboardViewModel: com.appsmoviles.splitly.viewmodel.dashboard.DashboardViewModel = viewModel(),
    billViewModel: com.appsmoviles.splitly.viewmodel.BillViewModel = viewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val translations = com.appsmoviles.splitly.utils.LocalTranslations.current
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val displayFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val today = Calendar.getInstance()
    var deadlineCalendar by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 15) }) }

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

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                deadlineCalendar = newCal
            },
            deadlineCalendar.get(Calendar.YEAR),
            deadlineCalendar.get(Calendar.MONTH),
            deadlineCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            val minCal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            datePicker.minDate = minCal.timeInMillis
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(translations["register_expense_title"] ?: "Registrar Gasto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = translations["register_expense_hint"] ?: "Agrega un nuevo recibo y Splitly lo dividirá automáticamente entre los miembros de tu hogar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(translations["expense_desc_placeholder"] ?: "Descripción del Gasto (Ej. Internet)") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(translations["total_amount"] ?: "Monto Total") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF10B981)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    ) {
                        OutlinedTextField(
                            value = displayFormat.format(deadlineCalendar.time),
                            onValueChange = { },
                            label = { Text(translations["deadline_label"] ?: "Fecha límite para miembros") },
                            leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFF59E0B)) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = Color(0xFFF59E0B)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (contributionViewModel.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull()
                        if (description.isBlank() || parsedAmount == null || parsedAmount <= 0) {
                            Toast.makeText(context, translations["fill_fields_correctly"] ?: "Por favor llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (activeHouseholdId.isEmpty() || creatorId == -1) {
                            Toast.makeText(context, translations["session_error"] ?: "Error de sesión. Vuelve a ingresar.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        contributionViewModel.createFullExpense(
                            householdId = activeHouseholdId,
                            description = description,
                            totalAmount = parsedAmount,
                            creatorId = creatorId,
                            paymentDate = sdf.format(today.time),
                            deadline = sdf.format(deadlineCalendar.time)
                        ) {
                            Toast.makeText(context, translations["expense_created_success"] ?: "¡Gasto dividido y notificado con éxito!", Toast.LENGTH_LONG).show()
                            description = ""
                            amount = ""
                            dashboardViewModel.lastUpdated = 0L
                            billViewModel.lastUpdated = 0L
                            navController.navigate("Dashboard") { popUpTo("Expenses") { inclusive = true } }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(translations["split_expense_btn"] ?: "Dividir Gasto", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (contributionViewModel.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = contributionViewModel.errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }
        }
    }
}