package com.example.thechefbot.presentation.AuthFeat.ui

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
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpUserScreen(modifier: Modifier = Modifier, navController: NavHostController, paddingValues: PaddingValues) {
    val viewModel = koinViewModel<LoginViewModel>()
    SignUpLogin(
//        viewModel = viewModel,
        navController = navController,
        paddingValues = paddingValues
    )
}


@Composable
fun SignUpLogin(modifier: Modifier = Modifier,
//                viewModel: LoginViewModel,
                navController: NavHostController,
                paddingValues: PaddingValues
) {

    val viewModel = koinViewModel<LoginViewModel>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val email by viewModel.signUp_email.collectAsState()
    val password by viewModel.signUp_password.collectAsState()
    val fullName by viewModel.signUp_fullName.collectAsState()
    val phoneNumber by viewModel.signUp_phone_number.collectAsState()
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    val authUiState by viewModel.authStatus.collectAsStateWithLifecycle()

    var passWordVisible by rememberSaveable { mutableStateOf(false) }


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
//        loginUiState.signUpSuccess -> {
//            viewModel.handleIntents(LoginEvents.NavigateToLoginScreen)
//        }
        loginUiState.signUpPasswordVisible -> {
            passWordVisible = loginUiState.signUpPasswordVisible
        }
        !loginUiState.signUpPasswordVisible -> {
            passWordVisible = loginUiState.signUpPasswordVisible
        }

    }

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
                text = " SignUp to get Started",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = Color.White

            )

            Spacer(modifier = modifier.height(26.dp))

            val state = if (passWordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }


            SignUpTextFields(
                modifier = modifier,
                email = email,
                password = password,
                passwordState = state,
                passWordVisible = passWordVisible,
                viewModel = viewModel,
                fullName = fullName,
                phoneNumber = phoneNumber
            )


            Spacer(modifier = modifier.height(18.dp))

            Button(
                shape = Shapes().large,
                onClick = {
                    viewModel.handleIntents(LoginEvents.SignUpUser(email, password))
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
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp, start = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoxItems(modifier.clickable{
                    viewModel.handleIntents(LoginEvents.GoogleSignIn(credentialManager = credentialManager, context = context,fromSignUp = true))
                }, R.drawable.ic_google, text = "Google")

            }
            Spacer(modifier = modifier.height(18.dp))
            LoginView(modifier = modifier, navController = navController)


        }

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
fun SignUpTextFields(
    modifier: Modifier,
    email: String,
    password: String,
    fullName: String,
    phoneNumber: String,
    passwordState: VisualTransformation,
    passWordVisible: Boolean,
    viewModel: LoginViewModel
) {

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = colorResource(R.color.orange),
        ),
        value = fullName,
        onValueChange = {
            viewModel.handleIntents(LoginEvents.UpdateFullName(it))
        },
        label = {
            Text(
                text = "Full Name"
            )
        }

    )

    Spacer(modifier = modifier.height(18.dp))


    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = colorResource(R.color.orange),
        ),
        value = phoneNumber,
        onValueChange = {
            viewModel.handleIntents(LoginEvents.UpdatePhoneNumber(it))
        },
        label = {
            Text(
                text = "Phone Number"
            )
        }

    )

    Spacer(modifier = modifier.height(18.dp))



    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = colorResource(R.color.orange),
        ),
        value = email,
        onValueChange = {
            viewModel.handleIntents(LoginEvents.SignUpUpdateEmail(it))
        },
        label = {
            Text(
                text = "Email"
            )
        }

    )

    Spacer(modifier = modifier.height(18.dp))

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        visualTransformation = passwordState,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = colorResource(R.color.orange),
        ),
        value = password,
        onValueChange = {
            viewModel.handleIntents(LoginEvents.SignUpUpdatePassword(it))
        },
        label = {
            Text(
                text = "Password"
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                viewModel.handleIntents(LoginEvents.SignUpPasswordVisible(!passWordVisible))
            }) {
                if (passWordVisible) {
                    Icon(
                        painter = painterResource(id = R.drawable.eye_show_svgrepo_com),
                        contentDescription = null,
                        modifier = modifier.height(20.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.eye_off_svgrepo_com),
                        contentDescription = null,
                        modifier = modifier.height(20.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun LoginView(modifier: Modifier, navController: NavHostController) {

    Row(
        modifier = modifier.clickable{
            navController.navigate(Routes.Login)
        }
    ) {
        Text(text = "Don't have an account?",
            color = Color.Gray,
            modifier = modifier.padding(5.dp)
        )
        Text(text = "Login",
            color = colorResource(R.color.orange),
            modifier = modifier.padding(5.dp)
        )
    }
}


//@Preview
//@Composable
//fun LoginUserScreenPreview() {
//    ScreenLoginDesign( navController = NavHostController(LocalContext.current))
//}