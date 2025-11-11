package com.example.thechefbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.thechefbot.navigation.NavigationGraph
import com.example.thechefbot.presentation.ChatBotFeat.model.ThemePrefs
import com.example.thechefbot.presentation.SettingsFeat.model.SettingsViewModel
import com.example.thechefbot.presentation.splashScreenFeat.ChefBotSplashScreen
import com.example.thechefbot.ui.theme.TheChefBotTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {

    private val theme: ThemePrefs by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { false }
        }
        enableEdgeToEdge()
        val theme = ThemePrefs(this)
        setContent {
            val isDark by theme.isDarkFlow.collectAsState()

            LaunchedEffect(isDark) {
                println("MainActivity theme changed to: $isDark")
            }

            TheChefBotTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    ChefBotSplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {

                    NavigationGraph()
                }
            }
        }
    }
}
