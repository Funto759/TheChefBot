package com.example.thechefbot.presentation.SettingsFeat.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import com.example.thechefbot.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thechefbot.navigation.Routes
import com.example.thechefbot.presentation.AuthFeat.effects.AuthEffect
import com.example.thechefbot.presentation.AuthFeat.events.LoginEvents
import com.example.thechefbot.presentation.AuthFeat.model.LoginViewModel
import com.example.thechefbot.presentation.SettingsFeat.effects.SettingsEffects
import com.example.thechefbot.presentation.SettingsFeat.events.SettingEvents
import com.example.thechefbot.presentation.SettingsFeat.util.SettingsItemView
import com.example.thechefbot.presentation.SettingsFeat.util.SettingsSwitchItem
import com.example.thechefbot.presentation.SettingsFeat.util.SettingsTittleView
import com.example.thechefbot.presentation.SettingsFeat.model.SettingsViewModel
import com.example.thechefbot.presentation.SettingsFeat.state.SettingsState
import com.example.thechefbot.presentation.SettingsFeat.util.SettingsUserDetailsView
import com.example.thechefbot.ui.theme.TheChefBotTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(navHostController: NavHostController,onSignOut : (Boolean) -> Unit = {}) {
    var pushNotificationsEnabled by rememberSaveable { mutableStateOf(true) }

    val viewModel = koinViewModel<LoginViewModel>()
    val settingsViewModel = koinViewModel <SettingsViewModel>()
    val profileUiState by settingsViewModel.profileUiState.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        settingsViewModel.effects.collect { effect ->
            when(effect){
                is SettingsEffects.NavigateTo -> {
                    if (effect.route != null) {
                        navHostController.navigate(effect.route)
                    }else{
                        navHostController.popBackStack()
                    }
                }
                is SettingsEffects.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when(effect){
                is AuthEffect.Navigate -> {
                    if (effect.route == "Sign_Out") {
                        onSignOut(true)
                    }else{
                        navHostController.navigate(effect.route)
                    }
                }
                is AuthEffect.Toast-> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    SettingMainUi(
        email = profileUiState.email,
        pushNotificationsEnabled = pushNotificationsEnabled,
        onPushNotificationsToggle = { pushNotificationsEnabled = it },
        darkModeEnabled = profileUiState.isDark,
        onDarkModeToggle = { settingsViewModel.handleIntents(SettingEvents.ToggleTheme(it)) },
        onSignOutClick = { viewModel.handleIntents(LoginEvents.SignOut(context = context)) },
        onBackClick = { settingsViewModel.sendEffects(SettingsEffects.NavigateTo(null)) },
        onUserProfileClick = {settingsViewModel.sendEffects(SettingsEffects.NavigateTo(Routes.UserProfile))}
    )
}


@Composable
fun SettingMainUi(
    email: String,
    pushNotificationsEnabled: Boolean,
    onPushNotificationsToggle: (Boolean) -> Unit={},
    darkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit={},
    onSignOutClick: () -> Unit={},
    onBackClick: () -> Unit={},
    onUserProfileClick: () -> Unit ={}
) {
    SettingsContentScaffold(
        onBackClick = onBackClick,
        content = { innerPadding ->
            SettingsContent(
                innerPadding = innerPadding,
                email = email,
                pushNotificationsEnabled = pushNotificationsEnabled,
                onPushNotificationsToggle = onPushNotificationsToggle,
                darkModeEnabled = darkModeEnabled,
                onDarkModeToggle = onDarkModeToggle,
                onSignOutClick = onSignOutClick,
                onUserProfileClick = onUserProfileClick
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContentScaffold(
    onBackClick: () -> Unit,
    content: @Composable ((PaddingValues) -> Unit)
){
    val orange = colorResource(R.color.orange)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.SemiBold,
                    color = orange
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = orange)
                }
            }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun SettingsContent(
    innerPadding: PaddingValues,
    email: String,
    pushNotificationsEnabled: Boolean,
    onPushNotificationsToggle: (Boolean) -> Unit,
    darkModeEnabled: Boolean,
    onUserProfileClick: () -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onSignOutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),            // apply scaffold padding
        verticalArrangement = Arrangement.spacedBy(8.dp),  // space between items
        contentPadding = PaddingValues(vertical = 16.dp)   // top/bottom padding for list
    ) {
        // Profile section at top
        item {
            SettingsUserDetailsView(
                email = email,
                onClick = onUserProfileClick,
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
            )
        }

        // "Account Settings" section header
        item {
            SettingsTittleView(label = "Account Settings")
        }

        // Edit Profile item
        item {
            SettingsItemView(
                modifier = Modifier,
                text = "Edit Profile",
                onClick = onUserProfileClick,
                leadingIcon = Icons.Default.Person
            )
        }

        // Rate The App item
        item {
            SettingsItemView(
                modifier = Modifier,
                text = "Rate The App",
                onClick = { /*TODO*/ },
                leadingIcon = Icons.Default.Star
            )
        }

        // Log Out item
        item {
            SettingsItemView(
                modifier = Modifier,
                text = "Log Out",
                onClick = onSignOutClick,
                leadingIcon = Icons.Default.Logout
            )
        }

        // "More" section header
        item {
            SettingsTittleView(label = "More")
        }

        // Push Notifications toggle item
        item {
            SettingsSwitchItem(
                modifier = Modifier,
                pushNotificationsEnabled = pushNotificationsEnabled,
                togglePushNotifications = onPushNotificationsToggle,
                label = "Push Notifications",
                leadingIcon = Icons.Default.Notifications
            )
        }

        // Dark Mode toggle item
        item {
            SettingsSwitchItem(
                modifier = Modifier,
                pushNotificationsEnabled = darkModeEnabled,
                togglePushNotifications = onDarkModeToggle,
                label = "Dark Mode",
                leadingIcon = Icons.Default.InvertColors
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewSettings(){
    TheChefBotTheme {
        SettingMainUi(
            email = "john.hessin.clarke@examplepetstore.com",
            pushNotificationsEnabled = true,
            onPushNotificationsToggle = {},
            darkModeEnabled = false,
            onDarkModeToggle = {},
            onSignOutClick = {},
            onBackClick = {},
            onUserProfileClick = {}
        )
    }
}