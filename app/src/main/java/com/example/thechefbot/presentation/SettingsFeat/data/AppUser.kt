package com.example.thechefbot.presentation.SettingsFeat.data

data class AppUser(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val role: String = "user",          // custom
    val full_name: String? = null,            // custom
    val bio: String? = null,            // custom
    val phone_number: String? = null,            // custom
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)