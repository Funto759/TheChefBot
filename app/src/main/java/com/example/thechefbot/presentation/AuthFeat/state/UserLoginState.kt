package com.example.thechefbot.presentation.AuthFeat.state


data class UserLoginState(
    val email : String = "",
    val password : String = "",
    val passwordVisible : Boolean = false,
    val signUpEmail : String = "",
    val signUpPassword : String = "",
    val signUpPasswordVisible : Boolean = false,
    val signUpFullName : String = "",
    val signUpPhoneNumber : String = "",
    val isLoading : Boolean = false,
    val authenticated : Boolean = false,
    val unAuthenticated : Boolean = false,
    val errorMessage : String? = null,
    val errorStatus : Boolean = false,
    val logInSuccessful : Boolean = false,
    val signUpSuccessful : Boolean = false,
)