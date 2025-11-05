package com.example.thechefbot.presentation.splashScreenFeat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R
import com.example.thechefbot.ui.theme.TheChefBotTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChefBotSplashScreen(onSplashComplete: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Phase 1: Icon scales in with rotation
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(1000, easing = FastOutSlowInEasing)
            )
        }

        delay(800)

        // Phase 2: Text fades in
        showText = true
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )

        // Phase 3: Pulse effect
        delay(500)
        launch {
            scale.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(300)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(300)
            )
        }

        delay(1000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.purple_200),
                        colorResource(R.color.pink)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground), // Your bot icon
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .rotate(rotation.value),
                tint = Color.Unspecified
            )

            AnimatedVisibility(
                visible = showText,
                enter = fadeIn() + expandVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(alpha.value)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "PanDaBot",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.orange)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your AI Assistant",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorResource(R.color.orange)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun Preview(){
    TheChefBotTheme {
        ChefBotSplashScreen {  }
    }
}