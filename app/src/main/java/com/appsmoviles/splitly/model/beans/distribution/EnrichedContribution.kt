package com.appsmoviles.splitly.model.beans.distribution

data class EnrichedContribution(
    val memberContributionId: String,
    val concept: String,
    val amount: Double,
    val status: String,
    val deadline: String,
    val payedAt: String
)