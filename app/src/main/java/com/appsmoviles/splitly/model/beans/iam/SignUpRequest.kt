package com.appsmoviles.splitly.model.beans.iam

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String,
    val plan: Int,
    val householdId: String
)