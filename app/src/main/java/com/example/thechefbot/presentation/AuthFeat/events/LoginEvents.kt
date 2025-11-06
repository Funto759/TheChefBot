package com.example.thechefbot.presentation.AuthFeat.events

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import com.example.thechefbot.presentation.SettingsFeat.data.AppUser

sealed interface LoginEvents{

    data object ResetErrorStatus : LoginEvents
    data object ResetSignUpErrorStatus : LoginEvents

    data class SignOut(val context: Context) : LoginEvents
    data class GoogleSignIn(val credentialManager: CredentialManager, val context: Context, val fromSignUp: Boolean = false) : LoginEvents

    data object GetAuthStatus : LoginEvents
    data object LoginUser : LoginEvents
    data object SignUpUser : LoginEvents
    data object NavigateToHomeScreen : LoginEvents
    data object NavigateToLoginScreen : LoginEvents
    data object NavigateToRegisterScreen : LoginEvents

    data object PasswordVisible: LoginEvents
    data object SignUpPasswordVisible: LoginEvents
    data object NavigateToForgotPasswordScreen : LoginEvents
    data class UpdatePassword(val password : String) : LoginEvents
    data class UpdateFullName(val fullName : String) : LoginEvents
    data class UpdatePhoneNumber(val phoneNumber : String) : LoginEvents

    data class SignUpUpdatePassword(val password : String) : LoginEvents
    data class UpdateEmail(val email : String) : LoginEvents
    data class SignUpUpdateEmail(val email : String) : LoginEvents

    data object DeleteLastSession : LoginEvents


    data class UpdateUser(
      val user : AppUser
    ) : LoginEvents


}