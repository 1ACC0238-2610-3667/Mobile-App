package com.appsmoviles.splitly.model.beans.distribution

data class Bills(
    var householdId: String,
    var description: String,
    var amount: Int,
    var createdBy: Int,
    var paymentDate: String
)