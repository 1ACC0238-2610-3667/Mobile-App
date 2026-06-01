package com.appsmoviles.splitly.model.beans.appmanagement

data class Settings(
    var id: Int,
    var userId: Int,
    var language: String,
    var darkMode: Boolean,
    var notificationEnabled: Boolean,
    var createdAt: String,
    var updatedAt: String
)