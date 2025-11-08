package com.example.thechefbot.presentation.AuthFeat.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents

@Composable
fun EditableView(
    modifier: Modifier = Modifier,
    value : String,
    onValueChange : (String) -> Unit,
    hint : String,
    passWordVisible: Boolean = true,
    isPasswordField : Boolean = false,
    togglePasswordVisibility : () -> Unit = {},
){

    val state = if (passWordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    val keyboardOptions = if (isPasswordField){
        KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    }else{
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        visualTransformation = state,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(R.color.orange),
            focusedLabelColor = colorResource(R.color.orange),
            unfocusedTextColor = Color.White,
            focusedBorderColor = colorResource(R.color.orange),
        ),
        value = value,
        onValueChange = {
            onValueChange(it)
//            viewModel.handleIntents(LoginEvents.UpdateFullName(it))
        },
        label = {
            Text(
                text = hint
            )
        },
        trailingIcon = {
            if (isPasswordField){

                    IconButton(onClick = {
                       togglePasswordVisibility()
                    }) {
                        if (passWordVisible) {
                            Icon(
                                painter = painterResource(id = R.drawable.eye_show_svgrepo_com),
                                contentDescription = null,
                                modifier = modifier.height(20.dp),
//                                tint = colorResource(R.color.orange)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.eye_off_svgrepo_com),
                                contentDescription = null,
                                modifier = modifier.height(20.dp),
//                                tint = colorResource(R.color.orange)
                            )
                        }
                }
            }
        }

    )
}