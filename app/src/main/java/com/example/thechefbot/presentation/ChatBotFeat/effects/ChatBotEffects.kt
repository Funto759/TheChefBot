package com.example.thechefbot.presentation.ChatBotFeat.effects

import androidx.navigation3.runtime.NavKey

sealed interface ChatBotEffects{
    data class NavigateTo(val route: NavKey) : ChatBotEffects

    data class ShowToast(val message: String) : ChatBotEffects
}
