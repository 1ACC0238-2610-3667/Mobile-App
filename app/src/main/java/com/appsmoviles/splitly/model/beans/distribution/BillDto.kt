// Ubicación: com.appsmoviles.splitly.model.beans.distribution.BillDto.kt
package com.appsmoviles.splitly.model.beans.distribution

import com.google.gson.annotations.SerializedName

data class BillDto(
    @SerializedName("id") val id: String?,
    @SerializedName("amount") val amount: String?,
    @SerializedName("householdId") val householdId: String?
)