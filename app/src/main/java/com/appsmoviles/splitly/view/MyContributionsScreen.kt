package com.appsmoviles.splitly.view

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.distribution.EnrichedContribution
import com.appsmoviles.splitly.utils.LocalTranslations
import com.appsmoviles.splitly.viewmodel.MyContributionsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MyContributionsScreen(viewModel: MyContributionsViewModel, context: Context) {
    val strings = LocalTranslations.current

    var showIncomeDialog by remember { mutableStateOf(false) }
    var incomeInput by remember { mutableStateOf("") }

    // Estados para el diálogo de pago
    var showPayDialog by remember { mutableStateOf(false) }
    var selectedQuotaToPay by remember { mutableStateOf<EnrichedContribution?>(null) }
    var payAmountInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.loadData(context)
        }
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6366F1))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = strings["my_contributions_title"] ?: "",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(strings["declared_salary"] ?: "", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = String.format(Locale.US, "S/ %,.2f", viewModel.currentIncome),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                    IconButton(onClick = {
                        incomeInput = viewModel.currentIncome.toString()
                        showIncomeDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = strings["edit_salary_button"] ?: "", tint = Color(0xFF6366F1))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SummaryItem(strings["assigned"] ?: "", viewModel.totalAssigned, Color(0xFF0F172A))
                        SummaryItem(strings["paid"] ?: "", viewModel.totalPaid, Color(0xFF10B981))
                        SummaryItem(strings["pending"] ?: "", viewModel.totalPending, Color(0xFFEF4444))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val progress = if (viewModel.totalAssigned > 0) (viewModel.totalPaid / viewModel.totalAssigned).toFloat() else 0f
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFE2E8F0)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% ${strings["paid"] ?: ""}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(strings["pending_quotas_title"] ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(8.dp))
            if (viewModel.pendingList.isEmpty()) {
                Text(strings["no_pending_debt"] ?: "", color = Color.Gray, fontSize = 14.sp)
            } else {
                viewModel.pendingList.forEach { quota ->
                    QuotaCard(
                        quota = quota,
                        isPending = true,
                        onPayClick = {
                            selectedQuotaToPay = quota
                            payAmountInput = quota.amount.toString() // Pre-llenar con el total
                            showPayDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(strings["payment_history_title"] ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(8.dp))
            if (viewModel.historyList.isEmpty()) {
                Text(strings["no_payments_registered"] ?: "", color = Color.Gray, fontSize = 14.sp)
            } else {
                viewModel.historyList.forEach { quota ->
                    QuotaCard(quota = quota, isPending = false)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showIncomeDialog) {
        Dialog(onDismissRequest = { showIncomeDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(strings["update_salary_title"] ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(strings["salary_description"] ?: "", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = incomeInput,
                        onValueChange = { incomeInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(strings["amount_label"] ?: "") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showIncomeDialog = false }) {
                            Text(strings["cancel_button"] ?: "", color = Color.Gray)
                        }
                        Button(onClick = {
                            val newAmount = incomeInput.toDoubleOrNull() ?: 0.0
                            viewModel.updateIncome(context, newAmount)
                            showIncomeDialog = false
                        }) {
                            Text(strings["save_button"] ?: "")
                        }
                    }
                }
            }
        }
    }

    if (showPayDialog && selectedQuotaToPay != null) {
        Dialog(onDismissRequest = { showPayDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(strings["make_payment_title"] ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${strings["payment_for"] ?: ""} ${selectedQuotaToPay!!.concept}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = payAmountInput,
                        onValueChange = { payAmountInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(strings["payment_amount_label"] ?: "") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showPayDialog = false }) {
                            Text(strings["cancel_button"] ?: "", color = Color.Gray)
                        }
                        Button(onClick = {
                            val amount = payAmountInput.toDoubleOrNull() ?: 0.0
                            viewModel.payQuota(context, selectedQuotaToPay!!.memberContributionId, amount)
                            showPayDialog = false
                        }) {
                            Text(strings["pay_button"] ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryItem(title: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = String.format(Locale.US, "S/ %,.2f", amount),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun QuotaCard(quota: EnrichedContribution, isPending: Boolean, onPayClick: () -> Unit = {}) {
    val strings = LocalTranslations.current
    
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPending) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isPending) Color(0xFFF59E0B) else Color(0xFF10B981),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(quota.concept, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                if (isPending) {
                    Text("${strings["deadline_label"] ?: ""} ${quota.deadline.take(10)}", fontSize = 12.sp, color = Color.Gray)
                } else {
                    Text("${strings["paid_date_label"] ?: ""} ${quota.payedAt.take(10)}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "S/ %,.2f", quota.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPending) Color(0xFFEF4444) else Color(0xFF0F172A)
                )

                if (isPending) {
                    Spacer(modifier = Modifier.height(4.dp))
                    if (quota.status.equals("Review", ignoreCase = true)) {
                        Text(
                            text = strings["review_status"] ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    } else {
                        Button(
                            onClick = onPayClick,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text(strings["pay_button"] ?: "", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}