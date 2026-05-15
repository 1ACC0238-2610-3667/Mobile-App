// Ubicación: com.appsmoviles.splitly.model.beans.iam.User.kt
package com.appsmoviles.splitly.model.beans.iam

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String?,
    @SerializedName("token") val token: String?,
    // Soporte nativo para ambas convenciones del backend
    @SerializedName("houseHoldId", alternate = ["householdId"]) val householdId: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("plan") val plan: String?,
    @SerializedName("name") val name: String? = "Usuario",
    @SerializedName("isNewUser") val isNewUser: Boolean = false
)