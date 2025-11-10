package com.example.thechefbot.presentation.AuthFeat.state

data class LoginState(
    val isLoading : Boolean = false,
    val success : Boolean = false,
    val signUpSuccess : Boolean = false,
    val errorStatus : Boolean = false,
    val signUpErrorStatus : Boolean = false,
    val errorMessage : String? = null,
    val signUpErrorMessage : String? = null,
    val passwordVisible : Boolean = false,
    val signUpPasswordVisible : Boolean = false,
    val authenticated : Boolean = false,
    val unAuthenticated : Boolean = false,
    val email : String = "",
    val password : String = "",
    val signUpEmail : String = "",
    val signUpPassword : String = "",
    val signUpFullName : String = "",
    val signUpPhoneNumber : String = "",
)