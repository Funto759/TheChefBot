package com.example.thechefbot.presentation.SettingsFeat.ui


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation3.runtime.NavBackStack
import com.example.thechefbot.R
import com.example.thechefbot.presentation.SettingsFeat.data.AppUser
import com.example.thechefbot.presentation.SettingsFeat.effects.SettingsEffects
import com.example.thechefbot.presentation.SettingsFeat.events.SettingEvents
import com.example.thechefbot.presentation.SettingsFeat.model.SettingsViewModel
import com.example.thechefbot.ui.theme.TheChefBotTheme
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMainScreen(modifier: Modifier = Modifier, backStack: NavBackStack) {

    val viewModel = koinViewModel<SettingsViewModel>()
    val profileUiState by viewModel.profileUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when(effect){
                is SettingsEffects.NavigateTo -> {
                    if (effect.route != null) {
                        backStack.add(effect.route)
                    }else{
                        backStack.removeLastOrNull()
                    }
                }
                is SettingsEffects.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when {
        profileUiState.onBackPressed -> {
            if (profileUiState.isEditable) {
                viewModel.handleIntents(SettingEvents.IsEditable(false))
                viewModel.handleIntents(SettingEvents.OnBackPressed(false))
            } else {
                viewModel.sendEffects(SettingsEffects.NavigateTo(null))
            }
        }
    }

    BackHandler {
        viewModel.handleIntents(SettingEvents.OnBackPressed(true))
    }

    ProfileScreenUI(
        isEditing = profileUiState.isEditable,
        fullName = profileUiState.fullName,
        phone = profileUiState.phone,
        email = profileUiState.email ?: "",
        bio = profileUiState.bio ?: "",
        profileImageRes = null,
        onBack = {
            viewModel.handleIntents(SettingEvents.OnBackPressed(true))
        },
        onEditClick = {
            viewModel.handleIntents(SettingEvents.IsEditable(true))
        },
        onImageClick = {

        },
        onNameChange = {
            viewModel.handleIntents(SettingEvents.UpdateFullName(it))
        },
        onPhoneChange = {
            viewModel.handleIntents(SettingEvents.UpdatePhoneNumber(it))
        },
        onEmailChange = {
            viewModel.handleIntents(SettingEvents.UpdateEmail(it))
        },
        onBioChange = {
            viewModel.handleIntents(SettingEvents.UpdateBio(it))
        },
        onSaveClick = {
            viewModel.handleIntents(SettingEvents.UpdateUser(
                user = AppUser(
                    full_name = profileUiState.fullName,
                    phone_number = profileUiState.phone,
                    bio = profileUiState.bio,
                    photoUrl = "",
                    email = profileUiState.email
                )
            ))
        },

    )

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenUI(
    // state in
    isEditing: Boolean,
    fullName: String,
    phone: String,
    email: String,
    bio: String,
    profileImageRes: Int?,     // pass drawable id (or null to show placeholder)
    // actions out
    onBack: () -> Unit = {},
    onEditClick: () -> Unit  = {},
    onSaveClick: () -> Unit  = {},
    onImageClick: () -> Unit  = {},
    onNameChange: (String) -> Unit  = {},
    onPhoneChange: (String) -> Unit  = {},
    onEmailChange: (String) -> Unit  = {},
    onBioChange: (String) -> Unit  = {},
) {
    val orange = colorResource(R.color.orange)
    val pink = colorResource(R.color.pink)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.SemiBold,
                        color = orange
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = orange)
                    }
                },
                actions = {
                    if (!isEditing) {
                        TextButton(
                            onClick = onEditClick,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(orange, RoundedCornerShape(10.dp))
                        ) {
                            Text("Edit")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image + Name
            item {
                ProfileAvatarCard(
                    orange = orange,
                    pink = pink,
                    name = fullName.ifBlank { "Your Name" },
                    profileImageRes = profileImageRes,
                    onImageClick = onImageClick
                )
            }

            // Fields
            item {
                LabeledField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = onNameChange,
                    enabled = isEditing,
                    orange = orange
                )
            }
            item {
                LabeledField(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = onPhoneChange,
                    enabled = isEditing,
                    orange = orange,
                    keyboardType = KeyboardType.Phone
                )
            }
            item {
                LabeledField(
                    label = "Email",
                    value = email,
                    onValueChange = onEmailChange,
                    enabled = isEditing,
                    orange = orange,
                    keyboardType = KeyboardType.Email
                )
            }
            item {
                LabeledField(
                    label = "Bio",
                    value = bio,
                    onValueChange = onBioChange,
                    enabled = isEditing,
                    orange = orange,
                    singleLine = false,
                    minLines = 3
                )
            }

            // Save button (only in edit mode)
            if (isEditing) {
                item {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orange,
                            contentColor = Color.White,
                            disabledContainerColor = orange.copy(alpha = 0.5f),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatarCard(
    orange: Color,
    pink: Color,
    name: String,
    profileImageRes: Int?,
    onImageClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .clickable { onImageClick() },
                contentAlignment = Alignment.BottomEnd
            ) {
                if (profileImageRes != null) {
                    Image(
                        painter = painterResource(id = profileImageRes),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                    )
                } else {
                    // Placeholder circle
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(pink.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "U",
                            color = orange,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Camera pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = orange
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .clickable { onImageClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Edit", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tap the photo to update",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    orange: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        modifier = Modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = orange,
            focusedLabelColor = orange,
            cursorColor = orange
        ),
        shape = RoundedCornerShape(12.dp)
    )
}


@Preview
@Composable
fun previewUi(){
    TheChefBotTheme {
        ProfileScreenUI(
            isEditing = true,
            fullName = "John Doe",
            phone = "123-456-7890",
            email = "john.mclean@examplepetstore.com",
            bio = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            profileImageRes = null,
        )
    }
}
