package com.appsmoviles.splitly.model.beans.iam

import com.google.gson.annotations.SerializedName
data class User(
    var id: Int,
    var email: String?,
    var token: String?,
    var houseHoldId: String?,
    var role: String?,
    var plan: String?,
    @SerializedName("personName") var name: String?,
    var isNewUser: Boolean
)