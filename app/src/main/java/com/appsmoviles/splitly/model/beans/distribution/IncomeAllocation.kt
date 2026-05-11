package com.appsmoviles.splitly.model.beans.distribution

data class IncomeAllocation(
    var userId: Int,
    var household: String,
    var percentage: Double
)