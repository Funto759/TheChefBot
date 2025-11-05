package com.example.thechefbot.presentation.AuthFeat.state

data class LoginState(
    val isLoading : Boolean = false,
    val success : Boolean = false,
    val signUpSuccess : Boolean = false,
    val errorStatus : Boolean = false,
    val signUpErrorStatus : Boolean = false,
    val errorMessage : String? = null,
    val signUpErrorMessage : String? = null,
   val navigateToHomeScreen : Boolean = false,
   val navigateToLoginScreen : Boolean = false,
    val passwordVisible : Boolean = false,
    val signUpPasswordVisible : Boolean = false,
    val authenticated : Boolean = false,
    val unAuthenticated : Boolean = false,
)