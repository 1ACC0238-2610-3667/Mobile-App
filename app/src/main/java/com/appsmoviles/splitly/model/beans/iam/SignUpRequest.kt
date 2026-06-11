package com.appsmoviles.splitly.model.beans.iam

import com.appsmoviles.splitly.model.beans.householdmanagement.Household

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String,
    val plan: Int = 0,
    val household: String = ""
)