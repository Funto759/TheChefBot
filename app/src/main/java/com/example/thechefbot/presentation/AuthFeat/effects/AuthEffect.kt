package com.example.thechefbot.presentation.AuthFeat.effects

import androidx.navigation3.runtime.NavKey


sealed interface AuthEffect{

    data class Toast(val message : String) : AuthEffect

    data class Navigate(val route : NavKey) : AuthEffect
}