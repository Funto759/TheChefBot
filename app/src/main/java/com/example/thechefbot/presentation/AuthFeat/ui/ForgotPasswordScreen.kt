package com.example.thechefbot.presentation.AuthFeat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thechefbot.navigation.data.Routes
import okhttp3.Route

@Composable
fun ForgotPasswordScreen( navController: NavHostController, paddingValues: PaddingValues){

    var otpText by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize().padding(paddingValues = paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        otpField(
            otpText = otpText,
        ) {
            otpText = it
        }

        if(otpText.length == 6) {
            Button(onClick = {
                navController.navigate(Routes.Login)
            }) {
                Text(
                    text = "Submit"
                )
            }
        }


    }
}

@Composable
fun otpField(
             otpText : String,
             onValueChange : (String) -> Unit,
             ) {
    val autofill = LocalAutofill.current
    val autofillTree = LocalAutofillTree.current
    val autofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.SmsOtpCode),
            onFill = {
                if(it.length <= 6 && it.all { char -> char.isDigit() }){
                onValueChange(it)
            }
            }
        )
    }
    // Register the node with the AutofillTree
    LaunchedEffect(Unit) {
        autofillTree += autofillNode
    }

    val autofillModifier = Modifier
        .onGloballyPositioned { coordinates ->
            autofillNode.boundingBox = coordinates.boundsInWindow()
            autofill?.requestAutofillForNode(autofillNode)
        }
    BasicTextField(
        value = otpText,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        modifier = autofillModifier.focusable().semantics { contentDescription = "OTP Text Field" },
        onValueChange = {
            if(it.length <= 6 && it.all { char -> char.isDigit() }){
                onValueChange(it)
            }
        },
        decorationBox = {view ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6){ index ->
                    val char = otpText.getOrNull(index)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (char.isEmpty()) Color.White else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            color = Color.Black,
                            style = MaterialTheme.typography.headlineMedium,

                        )
                    }
                }
            }
        }
    )




}




//
//@Composable
//@Preview(showBackground = true)
//fun ForgotPasswordScreenPreview(){
//    ForgotPasswordScreen()
//}