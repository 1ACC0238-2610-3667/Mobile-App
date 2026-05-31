package com.appsmoviles.splitly.model.beans.distribution

data class MemberContribution(
    val id: String?,
    val contributionId: String?,
    val memberId: String?,
    val amount: Double,
    val status: String?,
    val payedAt: String?
)