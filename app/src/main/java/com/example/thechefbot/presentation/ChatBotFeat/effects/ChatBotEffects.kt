package com.example.thechefbot.presentation.ChatBotFeat.effects

sealed interface ChatBotEffects{
    data class NavigateTo(val route: String) : ChatBotEffects

    data class ShowToast(val message: String) : ChatBotEffects
}
