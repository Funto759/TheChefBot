package com.example.thechefbot.presentation.AuthFeat.events

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import com.example.thechefbot.presentation.SettingsFeat.data.AppUser

sealed interface LoginEvents{


    // Resets the error Status when login failed

    data object ResetErrorStatus : LoginEvents

    // Resets the error Status when sign_up failed
    data object ResetSignUpErrorStatus : LoginEvents

    // Handle Logging out the User from the account

    data class SignOut(val context: Context) : LoginEvents

    // Handles Google Sign In
    data class GoogleSignIn(val credentialManager: CredentialManager, val context: Context, val fromSignUp: Boolean = false) : LoginEvents

    // Gets the Authentification status of the User

    data object GetAuthStatus : LoginEvents

    // Performs the Login of the User

    data object LoginUser : LoginEvents

    // Performs the Sign Up of the User

    data object SignUpUser : LoginEvents


    // Handles if the password is visible or not for login
    data object PasswordVisible: LoginEvents

    // Handles if the password is visible or not for sign_up
    data object SignUpPasswordVisible: LoginEvents

    // Updates the users password field

    data class UpdatePassword(val password : String) : LoginEvents

    // Updates the users Full name field
    data class UpdateFullName(val fullName : String) : LoginEvents

    // Updates the users Phone Number field
    data class UpdatePhoneNumber(val phoneNumber : String) : LoginEvents

    // Updates the users sign_up password field


    data class SignUpUpdatePassword(val password : String) : LoginEvents

    // Updates the users sign_up Full name field
    data class UpdateEmail(val email : String) : LoginEvents


    data class SignUpUpdateEmail(val email : String) : LoginEvents

    data object DeleteLastSession : LoginEvents


    data class UpdateUser(
      val user : AppUser
    ) : LoginEvents


}