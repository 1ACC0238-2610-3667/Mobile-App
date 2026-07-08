package com.appsmoviles.splitly.model.beans.householdmanagement

data class UpdateHouseholdMemberResource(
    var householdId: String? = null,
    var userId: Int? = null,
    var isRepresentative: Boolean? = null,
    var income: Double? = null
)
