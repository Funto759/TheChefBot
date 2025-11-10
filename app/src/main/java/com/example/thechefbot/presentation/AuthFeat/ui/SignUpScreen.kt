package com.example.thechefbot.presentation.AuthFeat.ui

import android.content.Context
import android.inputmethodservice.Keyboard
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thechefbot.R
import com.example.thechefbot.navigation.NavRoute
import com.example.thechefbot.navigation.Routes
import com.example.thechefbot.presentation.AuthFeat.effects.AuthEffect
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents
import com.example.thechefbot.presentation.AuthFeat.model.LoginViewModel
import com.example.thechefbot.presentation.AuthFeat.state.LoginState
import com.example.thechefbot.presentation.AuthFeat.util.EditableView
import com.example.thechefbot.presentation.AuthFeat.util.LoginActions
import com.example.thechefbot.presentation.AuthFeat.util.LoginBoxes
import com.example.thechefbot.presentation.AuthFeat.util.LoginViewAuth
import com.example.thechefbot.presentation.AuthFeat.util.WelcomeHeader
import com.example.thechefbot.ui.theme.TheChefBotTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpUserScreen(modifier: Modifier = Modifier, navController: NavHostController, paddingValues: PaddingValues) {
    val viewModel = koinViewModel<LoginViewModel>()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AuthEffect.Toast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is AuthEffect.Navigate -> {
                    if (effect.route == Routes.Tabs) {
                        navController.navigate(effect.route) {
                            popUpTo(Routes.SignUp) {
                                inclusive = true
                            }
                        }
                    }else if (effect.route == "Sign_Out"){
                        println("Sign Out")
                    }else {
                        navController.navigate(effect.route) {
                        }
                    }

                }
            }
        }
    }

    SignUpLogin(
        paddingValues = paddingValues
        , isLoading = loginUiState.isLoading
        , email = loginUiState.signUpEmail
        , password = loginUiState.signUpPassword
        , fullName = loginUiState.signUpFullName
        , phoneNumber = loginUiState.signUpPhoneNumber
        , passWordVisible = loginUiState.signUpPasswordVisible
        , onSignUpClick = { viewModel.handleIntents(LoginEvents.SignUpUser) }
        , onGoogleClick = { viewModel.handleIntents(LoginEvents.GoogleSignIn(credentialManager = credentialManager, context = context,fromSignUp = true)) }
        , goToSignUp = { viewModel.sendEffect(AuthEffect.Navigate(Routes.Login)) },
        updateEmail = { viewModel.handleIntents(LoginEvents.SignUpUpdateEmail(it)) },
        updatePassword = { viewModel.handleIntents(LoginEvents.SignUpUpdatePassword(it)) },
        updateFullName = { viewModel.handleIntents(LoginEvents.UpdateFullName(it)) },
        updatePhoneNumber = { viewModel.handleIntents(LoginEvents.UpdatePhoneNumber(it)) },
        togglePasswordVisibility = { viewModel.handleIntents(LoginEvents.SignUpPasswordVisible) }
    )
}


@Composable
fun SignUpLogin(modifier: Modifier = Modifier,
                isLoading : Boolean = false,
                paddingValues: PaddingValues,
                email: String,
                password: String,
                fullName: String,
                phoneNumber: String,
                passWordVisible: Boolean,
                onSignUpClick : () -> Unit,
                onGoogleClick : () -> Unit,
                goToSignUp : () -> Unit,
                updateEmail: (String) -> Unit = {},
                updatePassword: (String) -> Unit = {},
                updateFullName: (String) -> Unit = {},
                updatePhoneNumber: (String) -> Unit = {},
                togglePasswordVisibility: () -> Unit = {}
) {


    Box(modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues)) {
        SignUpContent(modifier = modifier
            , email = email
            , password = password
            , fullName = fullName,
            phoneNumber = phoneNumber,
            passWordVisible = passWordVisible
            , onSignUpClick = onSignUpClick
            , onGoogleClick = onGoogleClick
            , goToSignUp = goToSignUp,
            updateEmail = updateEmail,
            updatePassword = updatePassword,

            updateFullName = updateFullName,
            updatePhoneNumber = updatePhoneNumber,
            togglePasswordVisibility = togglePasswordVisibility
        )

        if (isLoading){
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }


    }
}

@Composable
fun SignUpContent(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    fullName: String,
    phoneNumber: String,
    passWordVisible: Boolean,
    onSignUpClick : () -> Unit,
    onGoogleClick : () -> Unit,
    goToSignUp : () -> Unit,
    updateEmail: (String) -> Unit = {},
    updatePassword: (String) -> Unit = {},
    updateFullName: (String) -> Unit = {},
    updatePhoneNumber: (String) -> Unit = {},
    togglePasswordVisibility: () -> Unit = {}
) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Header
        WelcomeHeader(modifier = modifier, label = " SignUp to get Started")

        Spacer(modifier = modifier.height(26.dp))

        // Sign Up edit fields view
        SignUpTextFields(
            modifier = modifier,
            email = email,
            password = password,
            passWordVisible = passWordVisible,
            fullName = fullName,
            phoneNumber = phoneNumber,
            updateEmail = updateEmail,
            updatePassword = updatePassword,
            updateFullName = updateFullName,
            updatePhoneNumber = updatePhoneNumber,
            togglePasswordVisibility = togglePasswordVisibility
        )

        LoginActions(modifier = modifier, label = "Sign Up", onLoginClick = onSignUpClick)

        // Google login button
        LoginBoxes(
            modifier = modifier,
            onGoogle = onGoogleClick
        )
        // Go to login view
        LoginViewAuth(
            modifier = modifier,
            onClick =goToSignUp,
            text = "Login"
        )

    }
}

@Composable
fun SignUpTextFields(
    modifier: Modifier,
    email: String,
    password: String,
    fullName: String,
    phoneNumber: String,
    passWordVisible: Boolean,
    updateEmail: (String) -> Unit = {},
    updatePassword: (String) -> Unit = {},
    updateFullName: (String) -> Unit = {},
    updatePhoneNumber: (String) -> Unit = {},
    togglePasswordVisibility: () -> Unit = {}
) {

    EditableView(
        modifier = modifier,
        value = fullName,
        hint = "Full Name",
        onValueChange = updateFullName
    )

    Spacer(modifier = modifier.height(18.dp))

    EditableView(
        modifier = modifier,
        value = phoneNumber,
        hint = "Phone Number",
        onValueChange = updatePhoneNumber
    )

    Spacer(modifier = modifier.height(18.dp))

    EditableView(
        modifier = modifier,
        value = email,
        hint = "Email",
        onValueChange = updateEmail
    )

    Spacer(modifier = modifier.height(18.dp))

    EditableView(
        modifier = modifier,
        value = password,
        hint = "Password",
        onValueChange = updatePassword,
        passWordVisible = passWordVisible,
        isPasswordField = true,
        togglePasswordVisibility = togglePasswordVisibility
    )

}


@Preview
@Composable
fun previewLoginScreenUi(){
    TheChefBotTheme {
        SignUpLogin(
            paddingValues = PaddingValues(0.dp)
            , isLoading = false,
            email = ""
            , password = ""
            , fullName = ""
            , phoneNumber = ""
            , passWordVisible = true
            , onSignUpClick = {}
            , onGoogleClick = {}
            , goToSignUp = {}
        )
    }
}
