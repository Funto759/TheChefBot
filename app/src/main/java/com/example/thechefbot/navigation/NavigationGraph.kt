package com.example.thechefbot.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.thechefbot.presentation.AuthFeat.ui.ForgotPasswordScreen
import com.example.thechefbot.presentation.AuthFeat.ui.LoginUserScreen
import com.example.thechefbot.presentation.AuthFeat.ui.SignUpUserScreen
import com.example.thechefbot.presentation.ChatBotFeat.ui.ChatBotScreen
import com.example.thechefbot.presentation.SettingsFeat.ui.ProfileMainScreen
import com.example.thechefbot.presentation.SettingsFeat.ui.SettingsMainScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(NavGraphItems.LoginScreen)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        entryProvider = {key ->
            when(key){
                is NavGraphItems.LoginScreen ->{
                    NavEntry(
                        key = key
                    ){
                        Scaffold (modifier = Modifier.fillMaxSize()){
                            LoginUserScreen(backStack = backStack, paddingValues = it)
                        }
                    }
                }
                is NavGraphItems.SignUpScreen ->{
                    NavEntry(
                        key = key
                    ){
                        Scaffold (modifier = Modifier.fillMaxSize()) {
                            SignUpUserScreen(backStack = backStack, paddingValues = it)
                        }
                    }
                } is NavGraphItems.ForgotPasswordScreen ->{
                    NavEntry(
                        key = key
                    ){
                        Scaffold (modifier = Modifier.fillMaxSize()) {
                            ForgotPasswordScreen(backStack = backStack, paddingValues = it)
                        }
                    }
                } is NavGraphItems.ChatBotScreen ->{
                    NavEntry(
                        key = key
                    ){
                        ChatBotScreen(backStack = backStack,modifier = Modifier)
                    }
                }
                is NavGraphItems.SettingScreen ->{
                    NavEntry(
                        key = key
                    ){
                        SettingsMainScreen(backStack = backStack)
                    }
                } is NavGraphItems.UserProfileScreen ->{
                    NavEntry(
                        key = key
                    ){
                        ProfileMainScreen(backStack = backStack)
                    }
                }
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )

}




