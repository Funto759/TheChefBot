package com.example.thechefbot.presentation.SettingsFeat.events

import com.example.thechefbot.presentation.SettingsFeat.data.AppUser

sealed interface SettingEvents {


    // Updates the users Full name field
    data class UpdateFullName(val fullName : String) : SettingEvents

    // Updates the users Phone Number field
    data class UpdatePhoneNumber(val phoneNumber : String) : SettingEvents

    // Updates the users Email field
    data class UpdateEmail(val email : String) : SettingEvents

    // Updates the users Bio field
    data class UpdateBio(val bio : String) : SettingEvents

    // Updates the users PhotoUrl field
    data class UpdatePhotoUrl(val photoUrl : String) : SettingEvents
    data object ConnectListener : SettingEvents


    // Updates the User Data from fireBase
    data class UpdateUser(val user : AppUser) : SettingEvents

    // Updates the state of if the fields are editable or not

    data class IsEditable (val isEditable : Boolean) : SettingEvents

    // Handle the event where a back button is clicked

    data class OnBackPressed(val onBackPressed : Boolean) : SettingEvents

    data object DeleteLastSession : SettingEvents

    data class ToggleTheme(val enabled : Boolean) : SettingEvents


}