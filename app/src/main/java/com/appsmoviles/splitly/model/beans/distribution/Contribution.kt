package com.appsmoviles.splitly.model.beans.distribution

data class Contribution(
    var id: String?,
    var billId: String?,
    var householdId: String?,
    var description: String?,
    var deadlineForMembers: String?,
    var strategy: Int,
)