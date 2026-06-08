package com.appsmoviles.splitly.model.beans.householdmanagement

data class Invitation(
    var id: Int,
    var email: String,
    var householdId: String,
    var description: String
)