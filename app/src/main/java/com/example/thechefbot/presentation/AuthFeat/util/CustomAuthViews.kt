package com.example.thechefbot.presentation.AuthFeat.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R

@Composable
fun LoginViewAuth(
    modifier: Modifier
    , onClick : () -> Unit
    ,text : String
) {

    Row(
        modifier = modifier.clickable{
            onClick()
        }
    ) {
        Text(text = "Don't have an account?",
            color = Color.Gray,
            modifier = modifier.padding(5.dp)
        )
        Text(text = text,
            color = colorResource(R.color.orange),
            modifier = modifier.padding(5.dp)
        )
    }
}


@Composable
fun BoxItems(modifier: Modifier = Modifier, image : Int, text : String ) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(50.dp)
            .width(150.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
    ) {

        Row(modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = modifier
                    .size(50.dp)
                    .padding(end = 15.dp),
                tint = Color.Unspecified
            )


            Text(
                text = text,
                textAlign = TextAlign.Start,
                modifier = modifier,
                color = colorResource(R.color.black)
            )
        }

    }
}




@Composable
fun LoginBoxes(modifier: Modifier, onGoogle: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoxItems(modifier.clickable {
            onGoogle()
        }, R.drawable.ic_google, text = "Google")

    }
    Spacer(modifier = modifier.height(18.dp))
}



@Composable
fun ForgotPasswordText(modifier: Modifier = Modifier, onForgotPasswordClick: () -> Unit) {
    //Spacer
    Spacer(modifier = modifier.height(8.dp))
    // Forgot password text
    Text(
        text = "Forgot the password?",
        modifier = modifier
            .clickable(onClick = onForgotPasswordClick)
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        color = Color.Gray,
        textAlign = TextAlign.End
    )
}


@Composable
fun LoginActions(modifier: Modifier,label : String, onLoginClick: () -> Unit) {
    Spacer(modifier = modifier.height(18.dp))

    // Login button
    Button(
        shape = Shapes().large,
        onClick = onLoginClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
    ) {
        Text(text = label)
    }

    Spacer(modifier = modifier.height(18.dp))
    Text(
        text = "Or continue with",
        color = Color.Gray
    )
    Spacer(modifier = modifier.height(18.dp))
}


@Composable
 fun WelcomeHeader(modifier: Modifier = Modifier, label: String = "Welcome back to News Deluxe") {
    Spacer(modifier = modifier.height(58.dp))
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "Image",
        modifier = modifier.height(100.dp)
    )
    Spacer(modifier = modifier.height(8.dp))

    Text(
        text = label,
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        color = Color.White

    )
}