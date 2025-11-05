package com.example.thechefbot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thechefbot.presentation.ChatBotFeat.screen.ChatBotScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationGuide(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavRoute.RecipeScreen,
        modifier = Modifier
    ) {
        composable <NavRoute.RecipeScreen>{ ChatBotScreen(navHostController = navController) }
    }

}


sealed interface NavRoute{
    @Serializable
    data object RecipeScreen : NavRoute
}