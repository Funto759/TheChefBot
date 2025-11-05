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
import com.example.thechefbot.presentation.AuthFeat.util.LoginBoxes
import com.example.thechefbot.presentation.AuthFeat.util.LoginViewAuth
import dagger.hilt.android.internal.Contexts
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginUserScreen(modifier: Modifier = Modifier, navController: NavHostController, paddingValues: PaddingValues) {

    val context = LocalContext.current
    val viewModel = koinViewModel<LoginViewModel>()
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    val authUiState by viewModel.authStatus.collectAsStateWithLifecycle()

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
        context = context,
        modifier = modifier,
        viewModel = viewModel,
        loginUiState = loginUiState,
        authUiState = authUiState,
        navController = navController,
        paddingValues = paddingValues,
        loginAuthenticated = {
            viewModel.handleIntents(LoginEvents.NavigateToHomeScreen)
        }
    )
}


@Composable
fun ScreenLogin(modifier: Modifier = Modifier,
                navController: NavHostController,
                paddingValues: PaddingValues,
                context: Context,
                viewModel: LoginViewModel,
                loginUiState: LoginState,
                authUiState: UserLoginState,
                loginAuthenticated : () -> Unit
) {

    LoginView(
        modifier = modifier,
        paddingValues = paddingValues,
        viewModel = viewModel,
        email = authUiState.email,
        password = authUiState.password,
        authUiState = authUiState,
        context = context,
        navHostController = navController,
        passWordVisible = loginUiState.passwordVisible
    )
    }


@Composable
fun LoginView(modifier: Modifier = Modifier,
              paddingValues: PaddingValues,
              viewModel : LoginViewModel,
              email : String,
              password : String,
              authUiState : UserLoginState,
              context : Context,
              navHostController: NavHostController,
              passWordVisible : Boolean
              ) {

    val credentialManager = remember { CredentialManager.create(context) }
    Box(modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues)) {
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
                text = " Welcome back to News Deluxe",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = Color.White

            )

            Spacer(modifier = modifier.height(26.dp))

            val state = if (passWordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }

            EditableView(
                modifier = modifier,
                value = authUiState.email,
                hint = "Email",
                onValueChange = {
                    viewModel.handleIntents(LoginEvents.UpdateEmail(it))
                }
            )

            Spacer(modifier = modifier.height(18.dp))

            EditableView(
                modifier = modifier,
                value = authUiState.password,
                hint = "Password",
                onValueChange = {
                    viewModel.handleIntents(LoginEvents.UpdatePassword(it))
                },
                passWordVisible = passWordVisible,
                isPasswordField = true,
                togglePasswordVisibility = {
                    viewModel.handleIntents(LoginEvents.PasswordVisible)
                }
            )

            Spacer(modifier = modifier.height(8.dp))

            Text(
                text = "Forgot the password?",
                modifier = modifier
                    .clickable {
                       navHostController.navigate(Routes.Otp)
                    }
                    .align(Alignment.End)
                    .padding(end = 12.dp, start = 12.dp),
                color = Color.Gray
            )

            Spacer(modifier = modifier.height(18.dp))

            Button(
                shape = Shapes().large,
                onClick = {
                    viewModel.handleIntents(LoginEvents.LoginUser(email, password))
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp, start = 12.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = modifier.height(18.dp))
            Text(
                text = "Or continue with",
                color = Color.Gray
            )
            Spacer(modifier = modifier.height(18.dp))
            LoginBoxes(
                modifier = modifier,
                onGoogle = {
                    viewModel.handleIntents(
                        LoginEvents.GoogleSignIn(
                            credentialManager = credentialManager,
                            context = context,
                            false
                        )
                    )
                }
            )
            Spacer(modifier = modifier.height(18.dp))
            LoginViewAuth(
                modifier = modifier
                , text = "Sign Up"
                , onClick = {
                    navHostController.navigate(Routes.SignUp)
                })


        }
        when {
            authUiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }
    }
}





