package com.example.thechefbot.presentation.SettingsFeat.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thechefbot.R
import com.example.thechefbot.presentation.SettingsFeat.data.AppUser
import com.example.thechefbot.presentation.SettingsFeat.events.SettingEvents
import com.example.thechefbot.presentation.SettingsFeat.model.SettingsViewModel
import com.example.thechefbot.presentation.SettingsFeat.state.SettingsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier,paddingValues: PaddingValues,navHostController: NavHostController) {

    val viewModel = koinViewModel <SettingsViewModel>()

    val context = LocalContext.current
    val fullName by viewModel.fullName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val user by viewModel.user.collectAsState()
    val profileUiState by viewModel.profileUiState.collectAsState()

    when{
        profileUiState.onBackPressed ->{
            if (profileUiState.isEditable){
                viewModel.handleIntents(SettingEvents.IsEditable(false))
                viewModel.handleIntents(SettingEvents.OnBackPressed(false))
            }else{
                navHostController.popBackStack()
            }
        }
    }

    BackHandler {
        viewModel.handleIntents(SettingEvents.OnBackPressed(true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    modifier = modifier.padding(5.dp),
                    text = "Profile",
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                    Image(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = modifier.clickable {
                            viewModel.handleIntents(SettingEvents.OnBackPressed(true))
                        }.padding(5.dp)
                    )
            },

            actions = {
                when{
                 !profileUiState.isEditable -> {
                     Button(
                         shape = Shapes().small,
                         colors = ButtonColors(
                             containerColor = colorResource(R.color.orange),
                             contentColor = Color.White,
                             disabledContainerColor = colorResource(R.color.orange),
                             disabledContentColor = Color.White
                         ),
                         onClick = {
                         viewModel.handleIntents(SettingEvents.IsEditable(true))
                     }) {
                         Text(text = "Edit")
                     }
                 }
                }
            }
        )

        Spacer(modifier = modifier.height(22.dp))

                ProfileFields(text = "Full Name", viewModel = viewModel,value = fullName) {
                    when{
                        profileUiState.isEditable -> {
                            viewModel.handleIntents(SettingEvents.UpdateFullName(it))
                        }
                    }

                }

        ProfileFields(text = "Phone Number", viewModel = viewModel,value = phone) {
                   when{
                       profileUiState.isEditable -> {
                           viewModel.handleIntents(SettingEvents.UpdatePhoneNumber(it))
                       }
                   }
                }
                ProfileFields(text = "Email", viewModel = viewModel,value = email ?: "") {
                    when{
                        profileUiState.isEditable -> {
                            viewModel.handleIntents(SettingEvents.UpdateEmail(it))
                        }
                    }
                }

                ProfileFields(text = "Bio", viewModel = viewModel,value = bio ?: "---------") {
                   when{
                       profileUiState.isEditable -> {
                           viewModel.handleIntents(SettingEvents.UpdateBio(it))
                       }
                   }
                }

        Spacer(modifier = modifier.height(42.dp))

        when{
            profileUiState.isEditable -> {
                Button(modifier = modifier.fillMaxWidth(),
                    shape = Shapes().large,
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.orange),
                        contentColor = Color.White,
                        disabledContainerColor = colorResource(R.color.orange),
                        disabledContentColor = Color.White
                    ),
                    onClick = {
                    viewModel.handleIntents(SettingEvents.UpdateUser(
                        user = AppUser(
                            full_name = fullName,
                            phone_number = phone,
                            bio = bio,
                            photoUrl = "",
                            email = email
                        )
                    ))
                }) {
                    Text(text = "Save")
                }
            }
        }


    }



    }

@Composable
fun onBackPressed(profileUiState: SettingsState, navHostController: NavHostController, viewModel: SettingsViewModel){
    when{
        profileUiState.isEditable -> {
            BackHandler {
                viewModel.handleIntents(SettingEvents.IsEditable(false))
            }
        }
        !profileUiState.isEditable -> {
            BackHandler {
                navHostController.popBackStack()
            }
        }
    }
}




@Composable
fun ProfileFields(modifier: Modifier = Modifier,text: String, viewModel: SettingsViewModel,value:String,onValueChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = colorResource(R.color.orange),
        ),
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                text = text,
                color = Color.White
            )
        }

    )
}