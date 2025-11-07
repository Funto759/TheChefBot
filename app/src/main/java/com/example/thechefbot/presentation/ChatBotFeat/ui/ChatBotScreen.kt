package com.example.thechefbot.presentation.ChatBotFeat.ui

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thechefbot.navigation.Routes

import com.example.thechefbot.presentation.ChatBotFeat.util.DrawerContentView
import com.example.thechefbot.presentation.ChatBotFeat.util.ImagePickerMenu
import com.example.thechefbot.presentation.ChatBotFeat.util.InitialConversationScreen
import com.example.thechefbot.presentation.ChatBotFeat.util.MessagesList
import com.example.thechefbot.presentation.ChatBotFeat.util.PromptInputField
import com.example.thechefbot.presentation.ChatBotFeat.util.SendButton
import com.example.thechefbot.presentation.ChatBotFeat.util.TopChefBar
import com.example.thechefbot.presentation.ChatBotFeat.events.ChefScreenEvents
import com.example.thechefbot.presentation.ChatBotFeat.model.RecipeViewModel
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession
import com.example.thechefbot.presentation.ChatBotFeat.dummy.dummyMessages
import com.example.thechefbot.presentation.ChatBotFeat.dummy.dummySessions
import com.example.thechefbot.presentation.ChatBotFeat.state.ChefUiState
import com.example.thechefbot.presentation.SettingsFeat.model.SettingsViewModel
import com.example.thechefbot.ui.theme.TheChefBotTheme
import com.example.thechefbot.util.launchCamera
import com.example.thechefbot.util.launchPhotoPicker
import com.example.thechefbot.util.saveImageToInternalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val viewModel = koinViewModel<RecipeViewModel>()
    val chefUiState by viewModel.chefUiState.collectAsStateWithLifecycle()
    val messages by viewModel.messagesForActiveSession.collectAsStateWithLifecycle()
    val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val session by viewModel.selectedSession.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(Uri.EMPTY) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { saveImageToInternalStorage(context, it, onSelected = { uri ->
                viewModel.handleEvent(ChefScreenEvents.UpdateSelectedImage(uri))
            }) }
        }
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) viewModel.handleEvent(ChefScreenEvents.UpdateSelectedImage(cameraUri))
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentValues()
                )
                cameraUri?.let {
                    cameraLauncher.launch(it)
                }
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    ChatBotMainScreen(
        modifier = Modifier,
        email = chefUiState.userEmail,
        drawerState = drawerState,
        allSessions = allSessions,
        activeSessionId = chefUiState.activeSessionId,
        context = context,
        expandDrawer = {
            scope.launch {
                if (drawerState.isClosed) {
                    drawerState.open()
                } else {
                    drawerState.close()
                }
            }
        },
        onItemClicked = { sessionId ->
            viewModel.handleEvent(ChefScreenEvents.OpenSession(sessionId))
            scope.launch { drawerState.close() }
        },
        newChat = {
            viewModel.handleEvent(ChefScreenEvents.CreateNewSession)
            scope.launch { drawerState.close() }
        },
        onSettingsClicked = {
            navHostController.navigate(Routes.Profile)
        },
        showDeleteDialog = {
            viewModel.handleEvent(ChefScreenEvents.DeleteAllSessions)
//                viewModel.handleEvent(ChefScreenEvents.UpdateShowDialogStatus(true,null))
        },
        sessionToDelete = {
            viewModel.handleEvent(ChefScreenEvents.DeleteSession(it))
//                   viewModel.handleEvent(ChefScreenEvents.UpdateShowDialogStatus(true, it))
        },
        selectedImages = chefUiState.selectedImages,
        loading = chefUiState.loading,
        prompt = chefUiState.prompt,
        session = session,
        onSendClicked = {
            handleSendClick(viewModel, chefUiState, context, keyboardController)
        },
        onValueChange = {
            viewModel.handleEvent(ChefScreenEvents.UpdatePrompt(it))
        },
        launchPhotoPicker = {
            launchPhotoPicker(photoPicker = photoPicker)
        },
        launchCamera = {
            launchCamera(context = context, permissionLauncher = permissionLauncher, cameraLauncher = cameraLauncher, cameraUri = { uri ->
                cameraUri = uri
                cameraUri?.let { uri ->
                    cameraLauncher.launch(uri)
                }
            })
        },
        onCancelClicked = {
            viewModel.handleEvent(ChefScreenEvents.ClearImage)
            viewModel.handleEvent(ChefScreenEvents.ToggleGalleryMenuExpanded)
        },
        toggleExpanded = {
            viewModel.handleEvent(ChefScreenEvents.ToggleGalleryMenuExpanded)
        },
        settingsToggleExpanded = {
            viewModel.handleEvent(ChefScreenEvents.ToggleSettingsMenuExpanded)
        },
        onDeleteClicked = {
            viewModel.handleEvent(ChefScreenEvents.DeleteSession(chefUiState.activeSessionId))
//                        viewModel.handleEvent(ChefScreenEvents.UpdateShowDialogStatus(true,chefUiState.activeSessionId))
        },
        onToggleTheme = {

        },
        onClick = {
            scope.launch {
                if (drawerState.isClosed) {
                    drawerState.open()
                } else {
                    drawerState.close()
                }
            }
        },
        onDismissAlertDialog = {
            viewModel.handleEvent(ChefScreenEvents.ResetSessionToDelete)
        },
        onConfirmAlertDialog = {
            if (chefUiState.sessionToDelete != null) {
                viewModel.handleEvent(ChefScreenEvents.DeleteSession(chefUiState.sessionToDelete!!))
            } else {
                viewModel.handleEvent(ChefScreenEvents.DeleteAllSessions)
            }
        },
        onCancelAlertDialog = {
            viewModel.handleEvent(ChefScreenEvents.ResetSessionToDelete)
        },
        expanded = chefUiState.expanded,
        settingsExpandedStatus = chefUiState.settingsToggleExpanded,
        messages = messages,
        error = chefUiState.error,
        errorState = chefUiState.errorState,
        showDialogStatus = chefUiState.showDeleteDialog,
    )

}

@Composable
fun ChatBotMainScreen(
    modifier: Modifier = Modifier,
    allSessions: List<ChatSession>,
    email : String,
    activeSessionId: Int?,
    context: Context,
    showDeleteDialog: () -> Unit ={},
    showDialogStatus: Boolean = false,
    onSettingsClicked: () -> Unit ={},
    sessionToDelete: (Int?) -> Unit ={},
    drawerState: DrawerState,
    expandDrawer: () -> Unit ={},
    newChat:() -> Unit ={},
    onItemClicked : (Int) -> Unit ={},
    session: ChatSession? = null,
    selectedImages: Uri? = null,
    messages: List<ChatMessage>,
    loading: Boolean = false,
    prompt: String = "",
    errorState: Boolean = false,
    error: String = "",
    launchPhotoPicker: () -> Unit = {},
    launchCamera: () -> Unit = {},
    toggleExpanded: () -> Unit = {},
    settingsToggleExpanded: () -> Unit = {},
    onDeleteClicked : () -> Unit ={},
    onToggleTheme :() -> Unit ={},
    onCancelClicked: () -> Unit ={},
    onValueChange: (String) -> Unit = {},
    onSendClicked: () -> Unit ={},
    onClick: () -> Unit ={},
    onDismissAlertDialog: () -> Unit = {},
    onConfirmAlertDialog: () -> Unit = {},
    onCancelAlertDialog: () -> Unit = {},
    expanded: Boolean,
    settingsExpandedStatus: Boolean = false,
){
    ModalDrawerView(
        modifier = modifier,
        email = email,
        drawerState = drawerState,
        allSessions = allSessions,
        activeSessionId = activeSessionId,
        context = context,
        expandDrawer = expandDrawer,
        onItemClicked = onItemClicked,
        newChat = newChat,
        onSettingsClicked = onSettingsClicked,
        showDeleteDialog = showDeleteDialog,
        sessionToDelete = sessionToDelete,
        content = {
            MainScreen(
                modifier = modifier,
                selectedImages = selectedImages,
                loading = loading,
                prompt = prompt,
                session = session,
                onSendClicked = onSendClicked,
                onValueChange = onValueChange,
                launchPhotoPicker =launchPhotoPicker,
                launchCamera = launchCamera,
                onCancelClicked = onCancelClicked,
                toggleExpanded = toggleExpanded,
                settingsToggleExpanded = settingsToggleExpanded,
                onSettingsClicked = onSettingsClicked,
                onDeleteClicked = onDeleteClicked,
                onToggleTheme = onToggleTheme,
                onClick = onClick,
                expanded = expanded,
                settingsExpandedStatus = settingsExpandedStatus,
                messages = messages,
                context = context,
                showDeleteDialog = showDialogStatus,
                error = error,
                errorState = errorState,
                onDismissAlertDialog = onDismissAlertDialog,
                onConfirmAlertDialog = onConfirmAlertDialog,
                onCancelAlertDialog = onCancelAlertDialog,
            )
        }
    )
}


@Composable
fun ModalDrawerView(modifier: Modifier = Modifier,
                    allSessions: List<ChatSession>,
                    email : String,
                    activeSessionId: Int?,
                    context: Context,
                    showDeleteDialog: () -> Unit,
                    onSettingsClicked: () -> Unit,
                    sessionToDelete: (Int?) -> Unit,
                    drawerState: DrawerState,
                    expandDrawer: () -> Unit,
                    newChat:() -> Unit,
                    onItemClicked : (Int) -> Unit,
                    content: @Composable (() -> Unit)) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                DrawerContentView(
                    modifier = modifier,
                    email = email,
                    context = context ,
                    expandDrawer = expandDrawer,
                    showDeleteDialog = { showDeleteDialog() },
                    onSettingsClicked = {
                        onSettingsClicked()
                    },
                    newChat = newChat,
                    allSessions = allSessions,
                    sessionToDelete = { sessionToDelete(it) },
                    activeSessionId = activeSessionId,
                    onItemClicked = onItemClicked
                )
            }
        },
        drawerState = drawerState
    ){
       content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier,
               context: Context,
               session: ChatSession? = null,
               selectedImages: Uri? = null,
               messages: List<ChatMessage>,
               loading: Boolean = false,
               prompt: String = "",
               showDeleteDialog: Boolean = false,
               errorState: Boolean = false,
               error: String = "",
               launchPhotoPicker: () -> Unit = {},
               launchCamera: () -> Unit = {},
               toggleExpanded: () -> Unit = {},
               settingsToggleExpanded: () -> Unit = {},
               onSettingsClicked: () -> Unit,
               onDeleteClicked : () -> Unit,
               onToggleTheme :() -> Unit,
               onCancelClicked: () -> Unit,
               onValueChange: (String) -> Unit = {},
               onSendClicked: () -> Unit,
               onClick: () -> Unit,
               onDismissAlertDialog: () -> Unit = {},
               onConfirmAlertDialog: () -> Unit = {},
               onCancelAlertDialog: () -> Unit = {},
               expanded: Boolean,
               settingsExpandedStatus: Boolean = false,
               ) {


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        topBar = {
            TopChefBar(
                text = session?.title ?: null,
                scrollBehavior = scrollBehavior,
                modifier = modifier,
                expanded = settingsExpandedStatus,
                toggleExpanded = {
                    settingsToggleExpanded()
                },
            onSettingsClicked = {
                onSettingsClicked()
            },
                onDeleteClicked = {
                    onDeleteClicked()
                },
                onToggleTheme = {
                    onToggleTheme()
                },
                onClick = {
                    onClick()
                }
            )
        },
        bottomBar = {
            ChefBottomBar(
                modifier = modifier,
                prompt = prompt,
                selectedImages = selectedImages,
                loading = loading,
                expanded = expanded,
                toggleExpanded = toggleExpanded,
                launchCamera = launchCamera,
                launchPhotoPicker = launchPhotoPicker,
                onCancelClicked = onCancelClicked,
                onValueChange = onValueChange,
                onSendClicked = onSendClicked,

            )
        }
    ) { innerPadding ->
        ConversationArea(
            modifier = modifier,
            paddingValues = innerPadding,
            messages = messages,
            showDeleteDialog= showDeleteDialog,
            errorState = errorState,
            error = error,
            loading = loading,
            context = context,
            session = session,
            onDismissAlertDialog = onDismissAlertDialog,
            onConfirmAlertDialog = onConfirmAlertDialog,
            onCancelAlertDialog =onCancelAlertDialog
        )
    }
}


@Composable
fun ConversationArea(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    showDeleteDialog: Boolean = false,
    loading: Boolean = false,
    prompt: String = "",
    errorState: Boolean = false,
    error: String = "",
    context: Context,
    session: ChatSession? = null,
    onDismissAlertDialog: () -> Unit = {},
    onConfirmAlertDialog: () -> Unit = {},
    onCancelAlertDialog: () -> Unit = {}
) {
    if (messages.isEmpty() && loading) {
        InitialConversationScreen(
            modifier = modifier,
            paddingValues = paddingValues,
            session = session,
            messages = messages
        )
    } else {
        MessagesList(
            modifier = modifier,
            paddingValues = paddingValues,
            messages = messages,
            context = context,
            errorState = errorState,
            error = error,
            loading = loading,
            prompt = prompt,
            showDeleteDialog = showDeleteDialog,
            session = session,
            onDismiss = {
                onDismissAlertDialog()
            }
            ,onConfirm = {
                onConfirmAlertDialog()
            }
            ,onCancel = {
                onCancelAlertDialog()
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChefBottomBar(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    selectedImages: Uri? = null,
    loading: Boolean = false,
    prompt: String = "",
    toggleExpanded: () -> Unit,
    launchCamera: () -> Unit,
    launchPhotoPicker: () -> Unit,
    onCancelClicked : () -> Unit,
    onValueChange: (String) -> Unit = {},
    onSendClicked: () -> Unit = {},
) {
    BottomAppBar(
        actions = {
            ImagePickerMenu(
                modifier = modifier,
                selectedImages = selectedImages,
                expanded = expanded,
                onCancelClicked = onCancelClicked,
                toggleExpanded,
                launchCamera = { launchCamera() },
                launchPhotoPicker = { launchPhotoPicker() }
            )
            PromptInputField(
                loading = loading,
                prompt = prompt,
                modifier = modifier,
                onValueChange = onValueChange
            )
        },
        floatingActionButton = {
            SendButton(
                modifier = modifier
                , onSendClicked = onSendClicked
            )
        },
    )
}

private fun handleSendClick(
    viewModel: RecipeViewModel,
    chefUiState: ChefUiState,
    context: Context,
    keyboardController: SoftwareKeyboardController?
) {
    if (chefUiState.prompt.isNotEmpty() && chefUiState.selectedImages != null) {
        viewModel.handleEvent(
            ChefScreenEvents.GenerateRecipeWithImage(
                context = context,
                prompt = chefUiState.prompt,
                imageUri = chefUiState.selectedImages,
                sessionId = chefUiState.activeSessionId!!
            )
        )
        keyboardController?.hide()
    } else if (chefUiState.prompt.isNotEmpty()) {
        println("2")
        viewModel.handleEvent(
            ChefScreenEvents.GenerateRecipe(
                prompt = chefUiState.prompt,
                sessionId = chefUiState.activeSessionId!!
            )
        )
        keyboardController?.hide()
    } else {
        Toast.makeText(context, "Please enter a prompt", Toast.LENGTH_SHORT).show()
    }
}



@Preview
@Composable
fun previewChatBotMainScreen(){
    TheChefBotTheme {
        ChatBotMainScreen(
            modifier = Modifier,
            allSessions = dummySessions,
            email = "",
            activeSessionId = 3,
            session = ChatSession(sessionId = 1, title = "Funto", lastUsedTimeStamp = 1L, email = "user@example.com"),
            context = LocalContext.current,
            showDeleteDialog = {},
            showDialogStatus = false,
            onSettingsClicked = {},
            sessionToDelete = {},
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            expandDrawer = {},
            newChat = {},
            onItemClicked = {},
            messages = dummyMessages,
            loading = false,
            prompt = "",
            errorState = false,
            error = "",
            launchPhotoPicker = {},
            launchCamera = {},
            toggleExpanded = {},
            settingsToggleExpanded = {},
            onDeleteClicked = {},
            onToggleTheme = {},
            onCancelClicked = {},
            onValueChange = {},
            onSendClicked = {},
            onClick = {},
            expanded = false,
            settingsExpandedStatus = false
        )
    }
}









