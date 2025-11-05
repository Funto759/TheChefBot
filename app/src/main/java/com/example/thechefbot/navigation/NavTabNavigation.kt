package com.example.thechefbot.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thechefbot.presentation.AuthFeat.ui.LoginUserScreen
import com.example.thechefbot.presentation.ChatBotFeat.screen.ChatBotScreen
import com.example.thechefbot.presentation.SettingsFeat.ui.ProfileScreen
import com.example.thechefbot.presentation.SettingsFeat.ui.SettingsScreen
import kotlin.text.orEmpty

@Composable
fun NavTabNav(navController: NavHostController
              , modifier: Modifier = Modifier
              , onSignOut : (Boolean) -> Unit = {}) {
    val tabsOwner = LocalViewModelStoreOwner.current!!
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        route = Routes.Tabs,
    ) {


        composable (Routes.Login){
            Scaffold (modifier = Modifier.fillMaxSize()){ paddingValues ->
                LoginUserScreen(navController = navController, paddingValues = paddingValues)
            }
        }

        composable(Routes.Home) {
            ChatBotScreen(modifier = Modifier, navHostController = navController)
        }
        composable(Routes.Profile)  {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                SettingsScreen(navHostController = navController, paddingValues = it) { paddingValues ->
                    onSignOut(paddingValues)
                }
            }
        }



        composable(Routes.UserProfile) {
            Scaffold (modifier = Modifier.fillMaxSize()) { paddingValues ->
                ProfileScreen(navHostController = navController, paddingValues = paddingValues)
            }
        }


    }


}

object Routes {
    const val Login = "login"

    const val SignUp = "signUp"
    const val Otp = "otp"
    const val Tabs = "tabs"
    const val Home = "home"

    const val Profile = "profile"

    const val UserProfile = "userProfile"

}