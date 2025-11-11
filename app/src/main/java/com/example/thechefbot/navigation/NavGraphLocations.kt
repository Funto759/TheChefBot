package com.example.thechefbot.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavGraphItems{

    @Serializable
    data object LoginScreen : NavKey
    @Serializable
    data object SignUpScreen : NavKey
    @Serializable
    data object ForgotPasswordScreen : NavKey
    @Serializable
    data object OtpScreen : NavKey

    @Serializable
    data object ChatBotScreen : NavKey
    @Serializable
    data object SettingScreen : NavKey
    @Serializable
    data object UserProfileScreen : NavKey



}