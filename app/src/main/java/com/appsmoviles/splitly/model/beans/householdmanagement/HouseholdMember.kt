package com.appsmoviles.splitly.model.beans.householdmanagement

data class HouseholdMember(
    var householdId: String,
    var userId: Int,
    var isRepresentative: Boolean,
    var income: Number
)