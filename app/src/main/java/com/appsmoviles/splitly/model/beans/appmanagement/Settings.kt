package com.appsmoviles.splitly.model.beans.appmanagement

data class Settings(
    var userId: Int,
    var language: String,
    var darkMode: Boolean,
    var notificationEnabled: Boolean
)