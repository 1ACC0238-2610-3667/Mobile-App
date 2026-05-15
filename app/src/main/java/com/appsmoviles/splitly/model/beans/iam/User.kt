package com.appsmoviles.splitly.model.beans.iam

data class User(
    var id: Int,
    var email: String?,
    var token: String?,
    var houseHoldId: String?,
    var role: String?,
    var plan: String?,
    var name: String?,
    var isNewUser: Boolean
)