package com.example.thechefbot.presentation.SettingsFeat.state


import com.example.thechefbot.presentation.SettingsFeat.data.AppUser

data class SettingsState(
    val isLoading : Boolean = false,
    val fullName : String = "",
    val bio : String = "",
    val email : String = "",
    val phone : String = "",
    val user : AppUser? = null,
    val errorStatus : Boolean = false,
    val errorMessage : String = "",
    val successMessage : String = "",
    val isEditable : Boolean = false,
    val onBackPressed: Boolean = false,
    val isDark : Boolean = false

)
