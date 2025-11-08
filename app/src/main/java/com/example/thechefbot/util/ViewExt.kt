package com.example.thechefbot.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier, visibility : Boolean, text: String, onClick : () -> Unit){
    CenterAlignedTopAppBar(
        title = {
            Text(
                modifier = modifier.padding(5.dp),
                text = text,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.orange)
            )
        },
        navigationIcon = {
            if (visibility) {
                Image(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = modifier.clickable {
                        onClick
                    }.padding(5.dp)
                )
            }
        }
    )
}





@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 1.0f),
                    Color.LightGray.copy(alpha = 0.2f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}


fun Modifier.shimmer(
    cornerRadius: Dp = 10.dp,
    shimmerAlpha: Float = 0.6f,
    durationMillis: Int = 1600
): Modifier = composed {
    val shimmerColors = listOf(
        Color.DarkGray.copy(alpha = 0.30f),
        colorResource(R.color.orange).copy(alpha = 0.85f),
        Color.DarkGray.copy(alpha = 0.30f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val offsetX by transition.animateFloat(
        initialValue = -800f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
        ),
        label = "offsetX"
    )

    val radiusPx = with(LocalDensity.current) { cornerRadius.toPx() }

    drawWithContent {
        // draw original child first
        drawContent()

        // build a wide diagonal brush that sweeps across the child’s bounds
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(offsetX, 0f),
            end = Offset(offsetX + size.width / 1.5f, size.height)
        )

        // overlay on top of the content
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            size = size,
            alpha = shimmerAlpha
        )
    }
}