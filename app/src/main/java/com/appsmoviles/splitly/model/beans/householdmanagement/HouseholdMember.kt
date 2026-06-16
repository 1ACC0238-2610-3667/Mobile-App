package com.appsmoviles.splitly.model.beans.householdmanagement

data class HouseholdMember(
    var id: String? = null,
    var householdId: String? = null,
    var userId: Int? = null,
    var isRepresentative: Boolean? = null,
    var joinedAt: String? = null,
    var income: Double? = null
)