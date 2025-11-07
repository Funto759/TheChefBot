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
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents
import com.example.thechefbot.presentation.AuthFeat.model.LoginViewModel
import com.example.thechefbot.presentation.AuthFeat.state.LoginState
import com.example.thechefbot.presentation.AuthFeat.state.UserLoginState
import com.example.thechefbot.presentation.AuthFeat.util.BoxItems
import com.example.thechefbot.presentation.AuthFeat.util.EditableView
import com.example.thechefbot.presentation.AuthFeat.util.LoginViewAuth
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpUserScreen(modifier: Modifier = Modifier, navController: NavHostController, paddingValues: PaddingValues) {
    val viewModel = koinViewModel<LoginViewModel>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    val authUiState by viewModel.authStatus.collectAsStateWithLifecycle()

    SignUpLogin(
        viewModel = viewModel,
        navController = navController,
        paddingValues = paddingValues,
        context = context,
        credentialManager = credentialManager,
        loginUiState = loginUiState,
        authUiState = authUiState
    )
}


@Composable
fun SignUpLogin(modifier: Modifier = Modifier,
                loginUiState: LoginState,
                authUiState: UserLoginState,
                viewModel: LoginViewModel,
                context: Context,
                credentialManager: CredentialManager,
                navController: NavHostController,
                paddingValues: PaddingValues
) {
    when{
        loginUiState.navigateToHomeScreen -> {
            navController.navigate(Routes.Tabs){
                popUpTo(Routes.Login) {
                    inclusive = true
                }
            }
        }
        loginUiState.navigateToLoginScreen -> {
            navController.navigate(NavRoute.RecipeScreen)
        }
        loginUiState.signUpErrorStatus -> {
            Toast.makeText(LocalContext.current, loginUiState.signUpErrorMessage, Toast.LENGTH_SHORT).show()
            viewModel.handleIntents(LoginEvents.ResetSignUpErrorStatus)
        }

    }

    Box(modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues)) {

        SignUpContent(modifier
            , authUiState
            , loginUiState
            , viewModel
            , onSignUpClick = {
                viewModel.handleIntents(LoginEvents.SignUpUser)
            }
            , onGoogleClick = {
                viewModel.handleIntents(LoginEvents.GoogleSignIn(credentialManager = credentialManager, context = context,fromSignUp = true))
            }
            , goToSignUp = {
                navController.navigate(Routes.Login)
            }
        )
        when{
            authUiState.isLoading ->{
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }

    }
}

@Composable
fun SignUpContent(
    modifier: Modifier = Modifier,
    authUiState: UserLoginState,
    loginUiState: LoginState,
    viewModel: LoginViewModel,
    onSignUpClick : () -> Unit,
    onGoogleClick : () -> Unit,
    goToSignUp : () -> Unit
) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(modifier = modifier.height(58.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Image",
            modifier = modifier.height(100.dp)
        )
        Spacer(modifier = modifier.height(8.dp))

        Text(
            text = " SignUp to get Started",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = Color.White

        )

        Spacer(modifier = modifier.height(26.dp))

        // Sign Up edit fields view

        SignUpTextFields(
            modifier = modifier,
            email = authUiState.signUpEmail,
            password = authUiState.signUpPassword,
            passWordVisible = loginUiState.signUpPasswordVisible,
            viewModel = viewModel,
            fullName = authUiState.signUpFullName,
            phoneNumber = authUiState.signUpPhoneNumber
        )


        Spacer(modifier = modifier.height(18.dp))


        // Sign Up button

        Button(
            shape = Shapes().large,
            onClick = {
                onSignUpClick()
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(end = 12.dp, start = 12.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
        ) {
            Text(text = "SignUp")
        }

        Spacer(modifier = modifier.height(18.dp))
        Text(
            text = "Or continue with",
            color = Color.Gray
        )
        Spacer(modifier = modifier.height(18.dp))

        // Google login button
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(end = 12.dp, start = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {


            BoxItems(modifier.clickable{
      onGoogleClick()
            }, R.drawable.ic_google, text = "Google")

        }
        Spacer(modifier = modifier.height(18.dp))

        // Go to login view
        LoginViewAuth(
            modifier = modifier,
            onClick = {
               goToSignUp()
            },
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
    viewModel: LoginViewModel
) {

    EditableView(
        modifier = modifier,
        value = fullName,
        hint = "Full Name",
        onValueChange = {
            viewModel.handleIntents(LoginEvents.UpdateFullName(it))
        }
    )

    Spacer(modifier = modifier.height(18.dp))

    EditableView(
        modifier = modifier,
        value = phoneNumber,
        hint = "Phone Number",
        onValueChange = {
            viewModel.handleIntents(LoginEvents.UpdatePhoneNumber(it))
        }
    )

    Spacer(modifier = modifier.height(18.dp))

    EditableView(
        modifier = modifier,
        value = email,
        hint = "Email",
        onValueChange = {
            viewModel.handleIntents(LoginEvents.SignUpUpdateEmail(it))
        }
    )

    Spacer(modifier = modifier.height(18.dp))

    EditableView(
        modifier = modifier,
        value = password,
        hint = "Password",
        onValueChange = {
            viewModel.handleIntents(LoginEvents.SignUpUpdatePassword(it))
        },
        passWordVisible = passWordVisible,
        isPasswordField = true,
        togglePasswordVisibility = {
            viewModel.handleIntents(LoginEvents.SignUpPasswordVisible)
        }
    )
}
