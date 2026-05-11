package com.appsmoviles.splitly.model.beans.iam

data class User(
    var id:Int,
    var name: String,
    var email: String,
    var password: String,
    var role: String,
    var plan: String,
    var householdId: String,
    var token: String
    )