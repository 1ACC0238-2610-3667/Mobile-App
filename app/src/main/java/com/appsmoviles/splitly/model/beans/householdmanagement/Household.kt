package com.appsmoviles.splitly.model.beans.householdmanagement

data class Household(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val memberCount: Int? = null,
    val representativeId: Int? = null,
    val currency: String? = null,
    val startDate: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)