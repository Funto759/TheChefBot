package com.example.thechefbot.presentation.AuthFeat.state


data class UserLoginState(
    val isLoading : Boolean = false,
    val authenticated : Boolean = false,
    val unAuthenticated : Boolean = false,
    val errorMessage : String? = null,
    val errorStatus : Boolean = false,
    val logInSuccessful : Boolean = false,
    val signUpSuccessful : Boolean = false,
)