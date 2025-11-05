package com.example.thechefbot.presentation.SettingsFeat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.thechefbot.R
import com.example.thechefbot.navigation.Routes
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents
import com.example.thechefbot.presentation.AuthFeat.model.LoginViewModel
import com.example.thechefbot.util.TopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier,navHostController: NavHostController, paddingValues: PaddingValues,
                   onSignOut : (Boolean) -> Unit = {}) {

    val viewModel = koinViewModel<LoginViewModel>()
    val auth by viewModel.loginUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when{
        auth.authenticated -> {

        }
        auth.unAuthenticated -> {
           onSignOut(true)
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues)) {


        Column(modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues)) {

            TopBar(
                visibility = false,
                text = "Settings",
                onClick = {
//                navHostController.popBackStack()
                }
            )

            Spacer(modifier = modifier.height(10.dp))

            SettingsItem(
                icon = Icons.Default.Person,
                title = "Profile",
                modifier = modifier.clickable{
                    navHostController.navigate(Routes.UserProfile)
                }
            ){

            }

            Spacer(modifier = modifier.height(10.dp))

            SettingsItem(
                icon = Icons.Default.Help,
                title = "Help"
            )

            Spacer(modifier = modifier.height(10.dp))

            Row(
                modifier = modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp,)
                    .padding(8.dp)
                    .clickable{
                        viewModel.handleIntents(LoginEvents.SignOut(context = context))
                    }
            ) {
                Icon(
                    modifier = modifier.align(Alignment.CenterVertically),
                    imageVector =  Icons.Default.Logout,
                    contentDescription = null,
                    tint = colorResource(R.color.orange)
                )

                Spacer(modifier = modifier.width(6.dp))
                Text(
                    modifier = modifier.align(Alignment.CenterVertically).weight(1f),
                    text = "LogOut",
                    textAlign = TextAlign.Start
                )
                Icon(
                    modifier = modifier.align(Alignment.CenterVertically),
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = colorResource(R.color.orange)
                )

            }

//            SettingsItem(
//                icon = Icons.Default.Logout,
//                title = "LogOut",
//                modifier = modifier.clickable {
//                    viewModel.handleIntents(LoginEvents.SignOut)
//                }
//            )


        }


        when{
            auth.isLoading ->{
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }

    }
}



@Composable
fun SettingsItem(modifier: Modifier = Modifier,icon : ImageVector, title : String,
                 onClick : () -> Unit = {}) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp,)
            .padding(8.dp)
            .clickable{
                onClick
            }
    ) {
        Icon(
            modifier = modifier.align(Alignment.CenterVertically),
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(R.color.orange)
        )

        Spacer(modifier = modifier.width(6.dp))
        Text(
            modifier = modifier.align(Alignment.CenterVertically).weight(1f),
            text = title,
            textAlign = TextAlign.Start
        )
        Icon(
            modifier = modifier.align(Alignment.CenterVertically),
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = colorResource(R.color.orange)
        )

    }
}


//@Preview
//@Composable
//fun previewSettings(){
//    SettingsScreen()
//}