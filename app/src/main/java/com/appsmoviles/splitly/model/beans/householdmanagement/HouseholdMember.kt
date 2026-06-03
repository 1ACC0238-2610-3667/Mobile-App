package com.appsmoviles.splitly.model.beans.householdmanagement

data class HouseholdMember(
    var id: String,
    var householdId: String,
    var userId: Int,
    var isRepresentative: Boolean,
    var joinedAt: String,
    var income: Number
)