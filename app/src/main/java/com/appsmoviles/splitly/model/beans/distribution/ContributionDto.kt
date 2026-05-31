package com.appsmoviles.splitly.model.beans.distribution

import com.google.gson.annotations.SerializedName

data class ContributionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("amount") val amount: String?,
    @SerializedName("householdId") val householdId: String?
)