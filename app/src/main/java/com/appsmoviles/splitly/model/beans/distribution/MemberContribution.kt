package com.appsmoviles.splitly.model.beans.distribution

data class MemberContribution(
    val id: String? = null,
    val contributionId: String,
    val memberId: String,
    val amount: Double,
    val status: String? = "Pending",
    val payedAt: String? = null
)