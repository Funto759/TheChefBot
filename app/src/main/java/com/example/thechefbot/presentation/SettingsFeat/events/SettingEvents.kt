package com.example.thechefbot.presentation.SettingsFeat.events

import com.example.thechefbot.presentation.SettingsFeat.data.AppUser

sealed interface SettingEvents {

    data class UpdateFullName(val fullName : String) : SettingEvents
    data class UpdatePhoneNumber(val phoneNumber : String) : SettingEvents
    data class UpdateEmail(val email : String) : SettingEvents
    data class UpdateBio(val bio : String) : SettingEvents
    data class UpdatePhotoUrl(val photoUrl : String) : SettingEvents
    data object ConnectListener : SettingEvents
    data class UpdateUser(val user : AppUser) : SettingEvents

    data class IsEditable (val isEditable : Boolean) : SettingEvents

    data class OnBackPressed(val onBackPressed : Boolean) : SettingEvents

    data object DeleteLastSession : SettingEvents
}