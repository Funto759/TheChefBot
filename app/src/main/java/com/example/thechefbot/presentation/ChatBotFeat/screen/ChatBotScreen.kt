package com.example.thechefbot.presentation.ChatBotFeat.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thechefbot.presentation.ChatBotFeat.util.DrawerContentView
import com.example.thechefbot.presentation.ChatBotFeat.util.ImagePickerMenu
import com.example.thechefbot.presentation.ChatBotFeat.util.InitialConversationScreen
import com.example.thechefbot.presentation.ChatBotFeat.util.MessagesList
import com.example.thechefbot.presentation.ChatBotFeat.util.PromptInputField
import com.example.thechefbot.presentation.ChatBotFeat.util.SendButton
import com.example.thechefbot.presentation.ChatBotFeat.util.TopChefBar
import com.example.thechefbot.presentation.ChatBotFeat.model.events.ChefScreenEvents
import com.example.thechefbot.presentation.ChatBotFeat.model.RecipeViewModel
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatSession
import com.example.thechefbot.presentation.ChatBotFeat.model.state.ChefUiState
import com.example.thechefbot.util.launchCamera
import com.example.thechefbot.util.launchPhotoPicker
import com.example.thechefbot.util.saveImageToInternalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var expanded by rememberSaveable { mutableStateOf(false) }
    var settingsToggleExpanded by rememberSaveable { mutableStateOf(false) }
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


        ModalDrawerView(
            modifier = Modifier,
            scope = scope,
            drawerState = drawerState,
            viewModel = viewModel,
            allSessions = allSessions,
            showDeleteDialog = {
                viewModel.handleEvent(ChefScreenEvents.DeleteAllSessions)
//                viewModel.handleEvent(ChefScreenEvents.UpdateShowDialogStatus(true,null))
            },
            sessionToDelete = {
                viewModel.handleEvent(ChefScreenEvents.DeleteSession(it))
//                   viewModel.handleEvent(ChefScreenEvents.UpdateShowDialogStatus(true, it))
            },
            activeSessionId = chefUiState.activeSessionId,
            context = context,
            content = {
                MainScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    chefUiState = chefUiState,
                    session = session,
                    context = context,
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
                    toggleExpanded = {
                        viewModel.handleEvent(ChefScreenEvents.ToggleGalleryMenuExpanded)
                    },
                    settingsToggleExpanded = {
                       viewModel.handleEvent(ChefScreenEvents.ToggleSettingsMenuExpanded)
                    },
                    onSettingsClicked = {

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
                    expanded = chefUiState.expanded,
                    settingsExpandedStatus = chefUiState.settingsToggleExpanded,
                    keyboardController = keyboardController,
                    content = { innerPadding ->
                        ConversationArea(
                            modifier = modifier,
                            paddingValues = innerPadding,
                            messages = messages,
                            chefUiState = chefUiState,
                            context = context,
                            session = session,
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
                            }
                        )
                    }
                )
            }
        )
}


@Composable
fun ModalDrawerView(modifier: Modifier = Modifier,
                    allSessions: List<ChatSession>,
                    activeSessionId: Int?,
                    context: Context,
                    showDeleteDialog: () -> Unit,
                    sessionToDelete: (Int?) -> Unit,
                    scope: CoroutineScope,
                    drawerState: DrawerState,
                    viewModel: RecipeViewModel,
                    content: @Composable (() -> Unit)) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                DrawerContentView(
                    modifier = modifier,
                    context = context ,
                    expandDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    },
                    showDeleteDialog = { showDeleteDialog() },
                    newChat = {
                        viewModel.createNewSession()
                        scope.launch { drawerState.close() }
                    },
                    allSessions = allSessions,
                    sessionToDelete = { sessionToDelete(it) },
                    activeSessionId = activeSessionId,
                    onItemClicked = { sessionId ->
                        viewModel.handleEvent(ChefScreenEvents.OpenSession(sessionId))
                        scope.launch { drawerState.close() }
                    }
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
               session: ChatSession? = null,
               viewModel: RecipeViewModel,
               chefUiState: ChefUiState,
               context: Context,
               launchPhotoPicker: () -> Unit = {},
               launchCamera: () -> Unit = {},
               toggleExpanded: () -> Unit = {},
               settingsToggleExpanded: () -> Unit = {},
               onSettingsClicked: () -> Unit,
               onDeleteClicked : () -> Unit,
               onToggleTheme :() -> Unit,
               onClick: () -> Unit,
               expanded: Boolean,
               settingsExpandedStatus: Boolean = false,
keyboardController: SoftwareKeyboardController?,
               content: @Composable ((PaddingValues) -> Unit)) {
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
                context = context,
                chefUiState = chefUiState,
                expanded = expanded,
                toggleExpanded = toggleExpanded,
                launchCamera = launchCamera,
                launchPhotoPicker = launchPhotoPicker,
                viewModel = viewModel,
                keyboardController = keyboardController
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}


@Composable
fun ConversationArea(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    chefUiState: ChefUiState,
    context: Context,
    session: ChatSession? = null,
    onDismissAlertDialog: () -> Unit = {},
    onConfirmAlertDialog: () -> Unit = {},
    onCancelAlertDialog: () -> Unit = {}
) {
    if (messages.isEmpty() && !chefUiState.loading) {
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
            chefUiState = chefUiState,
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
    context: Context,
    chefUiState: ChefUiState,
    expanded: Boolean,
    toggleExpanded: () -> Unit,
    launchCamera: () -> Unit,
    launchPhotoPicker: () -> Unit,
    viewModel: RecipeViewModel,
    keyboardController: SoftwareKeyboardController?
) {
    BottomAppBar(

        actions = {
            ImagePickerMenu(
                modifier = modifier,
                selectedImages = chefUiState.selectedImages,
                expanded = expanded,
                onCancelClicked = {
                    viewModel.handleEvent(ChefScreenEvents.ClearImage)
                    toggleExpanded()
                },
                toggleExpanded,
                launchCamera = { launchCamera() },
                launchPhotoPicker = { launchPhotoPicker() }
            )
            PromptInputField(
                loading = chefUiState.loading,
                prompt = chefUiState.prompt,
                modifier = modifier,
                onValueChange = {
                    viewModel.handleEvent(ChefScreenEvents.UpdatePrompt(it))
                }
            )
        },
        floatingActionButton = {
            SendButton(
                modifier = modifier
                , onSendClicked = {
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
            )
        },
    )
}



