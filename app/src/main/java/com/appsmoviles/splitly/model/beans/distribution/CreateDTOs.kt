package com.appsmoviles.splitly.model.beans.distribution

data class CreateHouseHoldResource(
    val name: String,
    val representativeId: Int,
    val currency: String,
    val description: String,
    val memberCount: Int,
    val startDate: String
)

data class CreateBillResource(
    val houseHoldId: String,
    val description: String,
    val amount: Double,
    val createdBy: Int,
    val paymentDate: String
)

data class CreateContributionResource(
    val billId: String,
    val householdId: String,
    val description: String,
    val deadlineForMembers: String,
    val strategy: Int
)

data class CreateMemberContributionResource(
    val contributionId: String,
    val memberId: String,
    val amount: Double
)