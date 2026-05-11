package com.appsmoviles.splitly.model.beans.householdmanagement

import android.icu.util.Currency

data class Household(
    var id: String,
    var name: String,
    var representativeId: Int,
    var currency: String,
    var description: String,
    var memberCount: Int,
    var startDate: String
)