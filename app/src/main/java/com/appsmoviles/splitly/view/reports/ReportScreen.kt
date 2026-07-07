package com.appsmoviles.splitly.view.reports

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.appsmoviles.splitly.model.beans.Report
import com.appsmoviles.splitly.model.beans.ReportDetails
import com.appsmoviles.splitly.viewmodel.BillViewModel
import com.appsmoviles.splitly.viewmodel.ReportViewModel
import com.appsmoviles.splitly.viewmodel.contributions.ContributionViewModel
import com.appsmoviles.splitly.viewmodel.household.HouseholdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    reportViewModel: ReportViewModel,
    householdViewModel: HouseholdViewModel,
    billViewModel: BillViewModel,
    contributionViewModel: ContributionViewModel,
    isOnline: Boolean
) {
    var selectedReport by remember { mutableStateOf<Report?>(null) }
    var showDetails by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6366F1),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (isOnline) {
                FloatingActionButton(
                    onClick = {
                        val household = householdViewModel.household
                        if (household != null) {
                            val allBills = billViewModel.billsList
                            reportViewModel.createReport(
                                householdName = household.name ?: "Household",
                                bills = allBills,
                                contributionsMap = contributionViewModel.contributions
                            )
                        }
                    },
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Report")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            if (reportViewModel.reports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No reports generated yet", color = Color.Gray)
                        if (!isOnline) {
                            Text("Connect to internet to create one", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reportViewModel.reports) { report ->
                        ReportItem(report) {
                            selectedReport = report
                            showDetails = true
                        }
                    }
                }
            }
        }
    }

    if (showDetails && selectedReport != null) {
        val details = reportViewModel.getReportDetails(selectedReport!!)
        ReportDetailsDialog(details, selectedReport!!) {
            showDetails = false
        }
    }
}

@Composable
fun ReportItem(report: Report, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(report.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(report.date, color = Color.Gray, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$${String.format("%.2f", report.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsDialog(details: ReportDetails, report: Report, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = {
            Column {
                Text(report.title, fontWeight = FontWeight.Bold)
                Text(report.date, fontSize = 12.sp, color = Color.Gray)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Text("Total: $${String.format("%.2f", report.totalAmount)}", 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 20.sp,
                        color = Color(0xFF6366F1)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
                items(details.summaryItems) { item ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(item.billDescription, fontWeight = FontWeight.SemiBold)
                            Text("$${String.format("%.2f", item.amount)}")
                        }
                        item.contributions.forEach { contribution ->
                            Row(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Member ${contribution.memberId}", fontSize = 12.sp, color = Color.Gray)
                                Text("$${String.format("%.2f", contribution.amount)}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    )
}
