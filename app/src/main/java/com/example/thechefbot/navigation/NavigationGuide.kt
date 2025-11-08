package com.example.thechefbot.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.thechefbot.presentation.AuthFeat.model.LoginViewModel
import com.example.thechefbot.presentation.AuthFeat.ui.ForgotPasswordScreen
import com.example.thechefbot.presentation.AuthFeat.ui.LoginUserScreen
import com.example.thechefbot.presentation.AuthFeat.ui.SignUpUserScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationGuide(navController: NavHostController
) {

    val status by rememberSaveable { mutableStateOf(false) }

    val viewModel = koinViewModel<LoginViewModel>()
    val authStatus by viewModel.loginUiState.collectAsStateWithLifecycle()

    val startDestination  = when{
        authStatus.unAuthenticated -> Routes.Login
        authStatus.authenticated -> Routes.Tabs
        else -> Routes.Login
    }


//    val startDestination2 = if (status) NavigationGuide.HomeScreen else NavigationGuide.LoginScreen
    NavHost(navController = navController, startDestination = startDestination, builder = {

        composable(Routes.Login){

            Scaffold (modifier = Modifier.fillMaxSize()){
                LoginUserScreen(navController = navController, paddingValues = it)
            }
        }

        composable(Routes.SignUp) {
            Scaffold (modifier = Modifier.fillMaxSize()) {
                SignUpUserScreen(navController = navController, paddingValues = it)
            }
        }

        composable(Routes.Otp) {
            Scaffold (modifier = Modifier.fillMaxSize()) {
                ForgotPasswordScreen(navController = navController, paddingValues = it)
            }
        }

        composable(Routes.Tabs){
            val tabNavController = rememberNavController()
            NavTabNav(navController = tabNavController,
                onSignOut = {
                    navController.navigate(Routes.Login) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true   // clear Tabs from back stack
                        }
                        launchSingleTop = true
                    }
                })
        }

    })
}



sealed interface NavRoute{
    @Serializable
    data object RecipeScreen : NavRoute
}