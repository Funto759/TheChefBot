package com.example.thechefbot.presentation.AuthFeat.ui

import android.content.Context
import android.inputmethodservice.Keyboard
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.thechefbot.R
import com.example.thechefbot.navigation.Routes
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents
import com.example.thechefbot.presentation.AuthFeat.model.LoginViewModel
import com.example.thechefbot.presentation.AuthFeat.state.LoginState
import com.example.thechefbot.presentation.AuthFeat.state.UserLoginState
import com.example.thechefbot.presentation.AuthFeat.util.BoxItems
import com.example.thechefbot.presentation.AuthFeat.util.EditableView
import com.example.thechefbot.presentation.AuthFeat.util.ForgotPasswordText
import com.example.thechefbot.presentation.AuthFeat.util.LoginActions
import com.example.thechefbot.presentation.AuthFeat.util.LoginBoxes
import com.example.thechefbot.presentation.AuthFeat.util.LoginViewAuth
import com.example.thechefbot.presentation.AuthFeat.util.WelcomeHeader
import com.example.thechefbot.ui.theme.TheChefBotTheme
import dagger.hilt.android.internal.Contexts
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginUserScreen(modifier: Modifier = Modifier, navController: NavHostController, paddingValues: PaddingValues) {

    val context = LocalContext.current
    val viewModel = koinViewModel<LoginViewModel>()
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    val authUiState by viewModel.authStatus.collectAsStateWithLifecycle()
    val credentialManager = remember { CredentialManager.create(context) }


    when{
        loginUiState.navigateToHomeScreen -> {
            navController.navigate(Routes.Tabs){
                popUpTo(Routes.SignUp) {
                    inclusive = true
                }
            }
        }
        loginUiState.errorStatus -> {
            Toast.makeText(LocalContext.current, loginUiState.errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.handleIntents(LoginEvents.ResetErrorStatus)
        }
    }

    ScreenLogin(
        modifier = modifier,
        paddingValues = paddingValues,
        isLoading = authUiState.isLoading,
        email =authUiState.email,
        password = authUiState.password,
        passWordVisible = authUiState.passwordVisible,
        onEmailChange = { viewModel.handleIntents(LoginEvents.UpdateEmail(it)) },
        onPasswordChange = { viewModel.handleIntents(LoginEvents.UpdatePassword(it)) },
        onPasswordVisibilityToggle = { viewModel.handleIntents(LoginEvents.PasswordVisible) },
        onForgotPasswordClick = { navController.navigate(Routes.Otp) },
        onLoginClick = { viewModel.handleIntents(LoginEvents.LoginUser) },
        onSignUpClick = { navController.navigate(Routes.SignUp) },
        onGoogleSignIn = {
            viewModel.handleIntents(
                LoginEvents.GoogleSignIn(
                    credentialManager = credentialManager,
                    context = context,
                    false
                )
            )
        }
    )
}


@Composable
fun ScreenLogin(modifier: Modifier = Modifier,
                paddingValues: PaddingValues,
                isLoading : Boolean = false,
                email : String,
                password : String,
                passWordVisible : Boolean,
                onEmailChange: (String) -> Unit ={},
                onPasswordChange: (String) -> Unit ={},
                onPasswordVisibilityToggle: () -> Unit ={},
                onForgotPasswordClick: () -> Unit ={},
                onLoginClick: () -> Unit ={},
                onSignUpClick: () -> Unit ={},
                onGoogleSignIn: () -> Unit ={}
) {
    LoginContent(
        modifier = modifier,
        paddingValues = paddingValues,
        isLoading = isLoading,
        email = email,
        password = password,
        onEmailChange = onEmailChange,
        onPasswordChange = onPasswordChange,
        passWordVisible = passWordVisible,
        onPasswordVisibilityToggle = onPasswordVisibilityToggle,
        onForgotPasswordClick = onForgotPasswordClick,
        onLoginClick = onLoginClick,
        onSignUpClick = onSignUpClick,
        onGoogleSignIn = onGoogleSignIn
    )

    }

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    email: String,
    password: String,
    isLoading : Boolean = false,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    passWordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignIn: () -> Unit
) {

    Box(modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues)) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Welcome Header
            WelcomeHeader(modifier = modifier)
            // Enter email edit view
            EditableView(
                modifier = modifier,
                value = email,
                hint = "Email",
                onValueChange = onEmailChange
            )
            /// Spacer
            Spacer(modifier = modifier.height(18.dp))
            // Enter Password Edit View
            EditableView(
                modifier = modifier,
                value = password,
                hint = "Password",
                onValueChange = onPasswordChange,
                passWordVisible = passWordVisible,
                isPasswordField = true,
                togglePasswordVisibility = onPasswordVisibilityToggle
            )

            ForgotPasswordText(modifier = modifier, onForgotPasswordClick = onForgotPasswordClick)
            LoginActions(modifier = modifier, onLoginClick = onLoginClick)

            // Google login button
            LoginBoxes(
                modifier = modifier,
                onGoogle = {
                  onGoogleSignIn()
                }
            )

            // Sign Up View
            LoginViewAuth(
                modifier = modifier
                , text = "Sign Up"
                , onClick = onSignUpClick)
        }
        if (isLoading){
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
        }
    }
}


@Preview
@Composable
fun previewLoginScreen(){
    TheChefBotTheme {
        ScreenLogin(
            paddingValues = PaddingValues(0.dp),
            isLoading = false,
            email = "",
            password = "",
            passWordVisible = true,
        )

    }
}

