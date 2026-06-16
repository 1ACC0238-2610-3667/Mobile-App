
package com.appsmoviles.splitly.model.beans.iam

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val id: Int,
    val email: String,
    @SerializedName("personName") val name: String?,
    val houseHoldId: String?,
    val token: String,
    val role: String?,
    val plan: String?
)