package com.appsmoviles.splitly.model.beans

data class Report(
    val id: Int? = null,
    val title: String,
    val date: String,
    val totalAmount: Double,
    val detailsJson: String // This will store the serialized list of bills/contributions
)

// Helper class for the JSON structure
data class ReportDetails(
    val householdName: String,
    val billsCount: Int,
    val summaryItems: List<ReportSummaryItem>
)

data class ReportSummaryItem(
    val billDescription: String,
    val amount: Double,
    val contributions: List<MemberContributionInfo>
)

data class MemberContributionInfo(
    val memberId: String,
    val amount: Double
)
