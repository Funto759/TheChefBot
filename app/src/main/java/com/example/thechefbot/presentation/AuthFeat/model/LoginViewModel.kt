package com.example.thechefbot.presentation.AuthFeat.model

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thechefbot.R
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents
import com.example.thechefbot.presentation.AuthFeat.state.LoginState
import com.example.thechefbot.presentation.AuthFeat.state.UserLoginState
import com.example.thechefbot.presentation.ChatBotFeat.model.SessionPrefs
import com.example.thechefbot.presentation.SettingsFeat.data.AppUser
import com.example.thechefbot.presentation.SettingsFeat.events.SettingEvents
import com.example.thechefbot.presentation.SettingsFeat.model.UserRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.text.isNotEmpty


class LoginViewModel (
    private val repo: UserRepository,
    private val auth: FirebaseAuth,
    private val sessionPrefs: SessionPrefs,
) : ViewModel() {

    private val _authStatus = MutableStateFlow(UserLoginState())
    val authStatus = _authStatus.asStateFlow()

    private val _loginUiState = MutableStateFlow(LoginState())
    val loginUiState = _loginUiState.asStateFlow()


    init {
        handleIntents(LoginEvents.GetAuthStatus)
    }

    fun updateFields(phone_number: String? = null,photoUrl: String? = null,email : String? = null,fullName: String? = null, bio: String? = null, onDone: (Boolean, String?) -> Unit) {
        val map = mapOf(
            "photoUrl" to photoUrl,
            "email" to email,
            "full_name" to fullName,
            "bio" to bio,
            "updatedAt" to System.currentTimeMillis(),
            "phone_number" to phone_number
        )
        repo.updateCurrentUser(map, onDone)
    }



    fun createOrMergeUser(extra: AppUser? = null, onDone: (Boolean, String?) -> Unit) {
        repo.upsertCurrentUser(extra, onDone)
    }


    private fun launchCredentialManager(credentialManager: CredentialManager,context: Context,fromSignUp: Boolean = false) {
        viewModelScope.launch {
            if (fromSignUp) {
                if (_authStatus.value.signUpFullName.isNotEmpty() && _authStatus.value.signUpPhoneNumber.isNotEmpty()) {
                    println("Launching Credential Manager")
                } else {
                    _loginUiState.value = _loginUiState.value.copy(
                        signUpSuccess = false,
                        signUpErrorStatus = true,
                        signUpErrorMessage = "Full name and Phone number cannot be empty"
                    )
                    return@launch
                }
            }
//            val webClientId = context.getString(R.string.default_web_client_id)
            val webClientId = "context.getString(R.string.default_web_client_id)"

            // [START create_credential_manager_request]
            // Instantiate a Google sign-in request
            val googleIdOption = GetGoogleIdOption.Builder()
                // Your server's client ID, not your Android client ID.
                .setServerClientId(webClientId)
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(false)
                .build()

            // Create the Credential Manager request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            // [END create_credential_manager_request]

            viewModelScope.launch {
                try {
                    // Launch Credential Manager UI
                    val result = credentialManager.getCredential(
                        context = context,
                        request = request
                    )

                    // Extract credential from the result returned by Credential Manager
                    handleSignIn(result.credential,fromSignUp)
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
                }
            }

        }
    }

    private fun handleSignIn(credential: Credential,fromSignUp: Boolean = false) {
        // Check if credential is of type Google ID
        println("Credential is of type Google ID")
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            onGoogleIdToken(googleIdTokenCredential.idToken,fromSignUp)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }


    fun onGoogleIdToken(idToken: String?,fromSignUp: Boolean = false) {
        if (idToken.isNullOrEmpty()) {
            _loginUiState.update { it.copy(errorStatus = true, errorMessage = "Google token is null") }
            return
        }
        _loginUiState.update { it.copy(isLoading = true, errorStatus = false, errorMessage = null) }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                println("User logged in successfully")
                if (fromSignUp){
                    handleIntents(LoginEvents.UpdateUser(
                        user = AppUser(
                            full_name = _authStatus.value.signUpFullName,
                            phone_number = _authStatus.value.signUpPhoneNumber,
                            bio = "",
                            photoUrl = "",
                        )
                    ))
                }else {
                    handleIntents(LoginEvents.NavigateToHomeScreen)
                }
            }
            .addOnFailureListener { e ->
                println(e.message)
                _loginUiState.update { it.copy(isLoading = false, errorStatus = true, errorMessage = e.message) }
            }
    }


    fun onEmailChange(newEmail: String) {
        _authStatus.update {
            it.copy(
                email = newEmail
            )
        }
    }
    fun onSignUpEmailChange(newEmail: String) {
        _authStatus.update {
            it.copy(
                signUpEmail = newEmail
            )
        }
    }


    fun onPasswordChange(newPassword: String) {
        _authStatus.update {
            it.copy(
                password = newPassword
            )
        }
    }

    fun onSignUpPasswordChange(newPassword: String) {
        _authStatus.update {
            it.copy(
                signUpPassword = newPassword
            )
        }
    }

    fun onSignUpFullNameChange(newFullName: String) {
        _authStatus.update {
            it.copy(
                signUpFullName = newFullName
            )
        }
    }

    fun onSignUpPhoneNumberChange(newPhoneNumber: String) {
        _authStatus.update {
            it.copy(
                signUpPhoneNumber = newPhoneNumber
            )
        }
    }

    fun loginUserConfirmation(){
        if (_authStatus.value.email.isNotEmpty() && _authStatus.value.password.isNotEmpty()){
         loginUser(_authStatus.value.email,_authStatus.value.password)
        }else{
            _loginUiState.value = _loginUiState.value.copy(success = false, errorStatus = true, errorMessage = "Email and password cannot be empty")

        }
    }

    fun signUpUserConfirmation(){
        if (_authStatus.value.signUpEmail.isNotEmpty() && _authStatus.value.signUpPassword.isNotEmpty() && _authStatus.value.signUpFullName.isNotEmpty() && _authStatus.value.signUpPhoneNumber.isNotEmpty()){
          signUpUser(_authStatus.value.signUpEmail,_authStatus.value.signUpPassword)
        }
        else if (_authStatus.value.signUpPassword.length <= 8){
            _loginUiState.value = _loginUiState.value.copy(signUpSuccess = false, signUpErrorStatus = true, signUpErrorMessage = "Password must be at least 8 characters")
        } else{
            _loginUiState.value = _loginUiState.value.copy(signUpSuccess = false, signUpErrorStatus = true, signUpErrorMessage = "Email, Full name, Phone number and Password cannot be empty")

        }
    }

    fun getAuthStatus(){
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true)
            if (auth.currentUser == null) {
                println("User is not logged in")
                _loginUiState.value = _loginUiState.value.copy(
                    unAuthenticated = true,
                    isLoading = false,
                    authenticated = false
                )
            } else {
                println("User is logged in")
                _loginUiState.value = _loginUiState.value.copy(
                    authenticated = true,
                    isLoading = false,
                    unAuthenticated = false
                )
            }
        }
    }


    fun loginUser(email:String, password:String){
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true)

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        println("User logged in successfully")
                        createOrMergeUser(onDone = {
                                success, message ->
                            if (success){
                                println("User created successfully")
                                handleIntents(LoginEvents.NavigateToHomeScreen)
                            }else{
                                println("User creation failed")
                                _loginUiState.value = _loginUiState.value.copy(
                                    isLoading = false,
                                    signUpSuccess = false,
                                    signUpErrorStatus = true,
                                    signUpErrorMessage = message
                                )
                            }
                        })
                    }else{
                        println("User login failed")
                        _loginUiState.value = _loginUiState.value.copy(
                            isLoading = false,
                            success = false,
                            errorStatus = true,
                            errorMessage = task.exception?.message
                        )
                    }
                }
        }

    }

    fun signUpUser(email:String, password:String){
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true)

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        println("User created successfully")
                       handleIntents(LoginEvents.UpdateUser(
                           user = AppUser(
                               full_name = _authStatus.value.signUpFullName,
                               phone_number = _authStatus.value.signUpPhoneNumber,
                               email = email,
                               bio = "",
                               photoUrl = "",
                           )
                       ))
                    }else{
                        println("User creation failed")
                        _loginUiState.value = _loginUiState.value.copy(
                            isLoading = false,
                            signUpSuccess = false,
                            signUpErrorStatus = true,
                            signUpErrorMessage = task.exception?.message
                        )
                    }
                }
        }

    }


    fun signOut(context: Context){
        viewModelScope.launch {
            auth.signOut()
            // Clear saved Google session (so the chooser shows again next time)
            val cm = CredentialManager.create(context)
            try {
                cm.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) { /* ignore */ }
            getAuthStatus()
        }
    }

    fun togglePasswordVisibility(){
        _loginUiState.update {
            it.copy(signUpPasswordVisible = !it.signUpPasswordVisible, success = false, signUpErrorStatus = false )
        }
    }
    fun toggleSignUpPasswordVisibility(){
        _loginUiState.update {
            it.copy(passwordVisible = !it.passwordVisible, signUpSuccess = false, signUpErrorStatus = false )
        }
    }

    fun deleteLastSession(){
        viewModelScope.launch {
            sessionPrefs.clearLastSession()
        }
    }




    fun handleIntents(events : LoginEvents){
        when(events){
            is LoginEvents.DeleteLastSession -> {
                deleteLastSession()
            }
            is LoginEvents.LoginUser -> loginUserConfirmation()
            LoginEvents.NavigateToForgotPasswordScreen -> TODO()
            LoginEvents.NavigateToHomeScreen -> _loginUiState.value = _loginUiState.value.copy(navigateToHomeScreen = true)
            LoginEvents.NavigateToRegisterScreen -> TODO()
            is LoginEvents.UpdateEmail -> onEmailChange(events.email)
            is LoginEvents.UpdatePassword -> onPasswordChange(events.password)
            is LoginEvents.UpdateFullName -> onSignUpFullNameChange(events.fullName)
            is LoginEvents.UpdatePhoneNumber -> onSignUpPhoneNumberChange(events.phoneNumber)
            is LoginEvents.PasswordVisible -> togglePasswordVisibility()
            LoginEvents.NavigateToLoginScreen -> _loginUiState.value = _loginUiState.value.copy(navigateToLoginScreen = true)
            is LoginEvents.SignUpPasswordVisible -> toggleSignUpPasswordVisibility()
            is LoginEvents.SignUpUpdateEmail -> onSignUpEmailChange(events.email)
            is LoginEvents.SignUpUpdatePassword -> onSignUpPasswordChange(events.password)
            is LoginEvents.SignUpUser -> signUpUserConfirmation()
            LoginEvents.GetAuthStatus -> getAuthStatus()
            is LoginEvents.SignOut -> {
                deleteLastSession()
                signOut(events.context)
            }
            LoginEvents.ResetErrorStatus -> _loginUiState.value = _loginUiState.value.copy(errorStatus = false, errorMessage = null)
            LoginEvents.ResetSignUpErrorStatus -> _loginUiState.value = _loginUiState.value.copy(signUpErrorStatus = false, signUpErrorMessage = null)
            is LoginEvents.GoogleSignIn -> launchCredentialManager(events.credentialManager,events.context,fromSignUp = events.fromSignUp)
            is LoginEvents.UpdateUser -> updateFields(
                phone_number = events.user.phone_number,
                photoUrl = events.user.photoUrl,
                email = events.user.email,
                fullName = events.user.full_name,
                bio = events.user.bio
            ){ok,mess ->
                if (ok) {
                    handleIntents(LoginEvents.NavigateToHomeScreen)
                }
            }
        }
    }
}