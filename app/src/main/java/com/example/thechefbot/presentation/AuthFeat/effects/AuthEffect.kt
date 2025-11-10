package com.example.thechefbot.presentation.AuthFeat.effects

import com.example.thechefbot.navigation.Routes


sealed interface AuthEffect{

    data class Toast(val message : String) : AuthEffect

    data class Navigate(val route : String) : AuthEffect
}