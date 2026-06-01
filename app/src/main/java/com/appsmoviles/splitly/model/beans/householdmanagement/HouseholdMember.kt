package com.appsmoviles.splitly.model.beans.householdmanagement
data class HouseholdMember(
    val id: String,
    val householdId: String,
    val userId: Int,
    val isRepresentative: Boolean,
    val income: Double
)