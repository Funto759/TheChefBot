package com.example.thechefbot.presentation.SettingsFeat.effects

sealed interface SettingsEffects{
    data class NavigateTo(val route : String?) : SettingsEffects
    data class ShowToast(val message : String) : SettingsEffects
}