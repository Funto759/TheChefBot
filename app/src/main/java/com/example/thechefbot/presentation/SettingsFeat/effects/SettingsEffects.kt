package com.example.thechefbot.presentation.SettingsFeat.effects

import androidx.navigation3.runtime.NavKey

sealed interface SettingsEffects{
    data class NavigateTo(val route : NavKey?) : SettingsEffects
    data class ShowToast(val message : String) : SettingsEffects
}