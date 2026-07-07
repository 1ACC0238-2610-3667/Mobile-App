package com.appsmoviles.splitly.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.splitly.model.beans.MemberContributionInfo
import com.appsmoviles.splitly.model.beans.Report
import com.appsmoviles.splitly.model.beans.ReportDetails
import com.appsmoviles.splitly.model.beans.ReportSummaryItem
import com.appsmoviles.splitly.model.beans.distribution.Bills
import com.appsmoviles.splitly.model.beans.distribution.Contribution
import com.appsmoviles.splitly.model.beans.distribution.MemberContribution
import com.appsmoviles.splitly.model.repository.ReportRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportViewModel(context: Context) : ViewModel() {
    private val repository = ReportRepository(context)
    private val gson = Gson()

    var reports by mutableStateOf<List<Report>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isOffline by mutableStateOf(false)

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val history = repository.getAllReports()
            withContext(Dispatchers.Main) {
                reports = history
                isLoading = false
            }
        }
    }

    fun createReport(
        householdName: String,
        bills: List<Bills>,
        contributionsMap: Map<Contribution, List<MemberContribution>>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val summaryItems = bills.map { bill ->
                val contribution = contributionsMap.entries.find { it.key.billId == bill.id }
                ReportSummaryItem(
                    billDescription = bill.description ?: "No description",
                    amount = bill.amount ?: 0.0,
                    contributions = contribution?.value?.map { 
                        MemberContributionInfo(it.memberId, it.amount) 
                    } ?: emptyList()
                )
            }

            val details = ReportDetails(
                householdName = householdName,
                billsCount = bills.size,
                summaryItems = summaryItems
            )

            val totalAmount = bills.sumOf { it.amount ?: 0.0 }
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            
            val newReport = Report(
                title = "Summary - $householdName",
                date = date,
                totalAmount = totalAmount,
                detailsJson = gson.toJson(details)
            )

            repository.saveReport(newReport)
            loadReports()
        }
    }
    
    fun getReportDetails(report: Report): ReportDetails {
        return gson.fromJson(report.detailsJson, ReportDetails::class.java)
    }
}
