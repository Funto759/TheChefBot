package com.example.thechefbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.thechefbot.navigation.NavigationGuide
import com.example.thechefbot.presentation.splashScreenFeat.ChefBotSplashScreen
import com.example.thechefbot.ui.theme.TheChefBotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { false }
        }
        enableEdgeToEdge()
        setContent {
            TheChefBotTheme {
                val navController = rememberNavController()
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    ChefBotSplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {

                    NavigationGuide(navController)
                }
            }
        }
    }
}
