package com.appsmoviles.splitly.model.beans.distribution

data class Bills(
    var id: String?,
    var houseHoldId: String?,
    var description: String?,
    var amount: Double,
    var createdBy: Int,
    var paymentDate: String?,
    var createdAt: String?,
    var updatedAt: String?
)