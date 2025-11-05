package com.example.thechefbot.screen

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.thechefbot.R
import com.example.thechefbot.model.events.ChefScreenEvents
import com.example.thechefbot.model.RecipeViewModel
import com.example.thechefbot.model.data.ChatMessage
import com.example.thechefbot.model.data.ChatSession
import com.example.thechefbot.model.state.ChefUiState
import com.example.thechefbot.util.CommonUtil.copyToClipboard
import com.example.thechefbot.util.CommonUtil.parseMarkdown
import com.example.thechefbot.util.launchCamera
import com.example.thechefbot.util.launchPhotoPicker
import com.example.thechefbot.util.saveImageToInternalStorage
import com.example.thechefbot.util.shimmer
import com.example.thechefbot.util.shimmerLoading
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val viewModel = koinViewModel<RecipeViewModel>()
    val chefUiState by viewModel.chefUiState.collectAsStateWithLifecycle()
    val messages by viewModel.messagesForActiveSession.collectAsStateWithLifecycle()
    val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val session by viewModel.selectedSession.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(Uri.EMPTY) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var expanded by rememberSaveable { mutableStateOf(false) }
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
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    android.content.ContentValues()
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
            chefUiState = chefUiState,
            allSessions = allSessions,
            session = session,
            showDeleteDialog = {
                    viewModel.handleEvent(ChefScreenEvents.DeleteAllSessions)
            },
            sessionToDelete = {
                    viewModel.handleEvent(ChefScreenEvents.DeleteSession(it))
            },
            activeSessionId = chefUiState.activeSessionId,
            messages = messages,
            context = context,
            onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
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
            toggleExpanded = {
                expanded = !expanded
            },
            expanded = expanded,
            keyboardController = keyboardController
        )





}


@Composable
fun ModalDrawerView(modifier: Modifier = Modifier,
                    allSessions: List<ChatSession>,
                    activeSessionId: Int?,
                    session: ChatSession? = null,
                    showDeleteDialog: () -> Unit,
                    sessionToDelete: (Int?) -> Unit,
                    scope: CoroutineScope,
                    drawerState: DrawerState,
                    viewModel: RecipeViewModel,
                    chefUiState: ChefUiState,
                    messages: List<ChatMessage>,
                    context: Context,
                    launchPhotoPicker: () -> Unit = {},
                    launchCamera: () -> Unit = {},
                    toggleExpanded: () -> Unit = {},
                    onClick: () -> Unit,
                    expanded: Boolean,
                    keyboardController: SoftwareKeyboardController?) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }) {
                        Icon(Icons.Default.MenuOpen, contentDescription = "Menu",
                            tint = colorResource(R.color.orange))
                    }

                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "user attachment",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "Funmito",
                        modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()

                    NavigationDrawerItem(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("New Chat") },
                        selected = false,
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_chat_save),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            viewModel.createNewSession()
                            scope.launch { drawerState.close() }}
                    )
                    NavigationDrawerItem(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("Delete Chat") },
                        selected = false,
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_chat_del),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            viewModel.handleEvent(ChefScreenEvents.UpdateShowDialogStatus(true))
                            showDeleteDialog()
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Chat History",
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // LIST OF ALL SESSIONS
                    allSessions.forEach { session ->
                        NavigationDrawerItem(
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_chat_options),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (session.title.isNullOrEmpty()) "New Chat" else session.title,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )

                                    if (allSessions.size > 1) {
                                        IconButton(
                                            onClick = {
                                                sessionToDelete(session.sessionId)
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Cancel,
                                                contentDescription = "Delete session",
                                                tint = colorResource(R.color.orange)
                                            )
                                        }
                                    }
                                }
                            },
                            selected = activeSessionId == session.sessionId,
                            onClick = {
                                viewModel.handleEvent(ChefScreenEvents.OpenSession(session.sessionId))
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ){
        MainScreen(
            modifier = Modifier,
            viewModel = viewModel,
            chefUiState = chefUiState,
            messages = messages,
            session = session,
            context = context,
            launchPhotoPicker = {
                launchPhotoPicker()
            },
            launchCamera = {
                launchCamera()
            },
            toggleExpanded = {
                toggleExpanded()
            },
            onClick = {
                onClick()
            },
            expanded = expanded,
            keyboardController = keyboardController
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier,
               session: ChatSession? = null,
               viewModel: RecipeViewModel,
               chefUiState: ChefUiState,
               messages: List<ChatMessage>,
               context: Context,
               launchPhotoPicker: () -> Unit = {},
               launchCamera: () -> Unit = {},
               toggleExpanded: () -> Unit = {},
               onClick: () -> Unit,
               expanded: Boolean,
keyboardController: SoftwareKeyboardController?,
               content: @Composable ((PaddingValues) -> Unit)) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        topBar = {
         TopChefBar(
             text = session?.title ?: null ,
             scrollBehavior = scrollBehavior,
             modifier = modifier,
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
        ConversationArea(
            modifier = modifier,
            paddingValues = innerPadding,
            messages = messages,
            chefUiState = chefUiState,
            context = context,
            session = session
        )
    }
}





@Composable
fun ConversationArea(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    chefUiState: ChefUiState,
    context: Context,
    session: ChatSession? = null
) {
    if (messages.isEmpty() && !chefUiState.loading) {
        InitialConversationScreen(modifier = modifier, paddingValues = paddingValues, session = session, messages = messages)
    } else {
        MessagesList(
            modifier = modifier,
            paddingValues = paddingValues,
            messages = messages,
            context = context,
            chefUiState = chefUiState
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
                selectedImages = chefUiState.selectedImages
                , expanded = expanded
                , onCancelClicked = {
                    viewModel.handleEvent(ChefScreenEvents.ClearImage)
                    toggleExpanded()
                                    }
                , toggleExpanded
                , launchCamera =  { launchCamera() }
                , launchPhotoPicker =  { launchPhotoPicker() }
            )
            PromptInputField(
                loading = chefUiState.loading
                , prompt = chefUiState.prompt
                , modifier = modifier
                , onValueChange = {
                viewModel.handleEvent(ChefScreenEvents.UpdatePrompt(it))
            }
            )
        },
        floatingActionButton = {
            SendButton(
                modifier = modifier
                ,onSendClicked = {
                    if (chefUiState.prompt.isNotEmpty() && chefUiState.selectedImages != null) {
                        viewModel.handleEvent(ChefScreenEvents.GenerateRecipeWithImage(
                            context = context,
                            prompt = chefUiState.prompt,
                            imageUri = chefUiState.selectedImages,
                            sessionId = chefUiState.activeSessionId!!
                        ))
                        keyboardController?.hide()
                    }else if (chefUiState.prompt.isNotEmpty()) {
                        println("2")
                        viewModel.handleEvent(ChefScreenEvents.GenerateRecipe(prompt = chefUiState.prompt, sessionId = chefUiState.activeSessionId!!))
                        keyboardController?.hide()
                    }else{
                        Toast.makeText(context, "Please enter a prompt", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        },
    )
}



//@Composable
//fun showAlertDialog(showDeleteDialog: Boolean, sessionToDelete: Int?, viewModel: RecipeViewModel) {
//    AlertDialog(
//        onDismissRequest = {
//            showDeleteDialog = false
//            sessionToDelete = null
//        },
//        title = { Text("Delete Chat?") },
//        text = {
//            Text(
//                if (sessionToDelete == null)
//                    "Are you sure you want to delete all chat sessions?"
//                else
//                    "Are you sure you want to delete this chat session?"
//            )
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    if (sessionToDelete != null) {
//                        viewModel.deleteSession(sessionToDelete!!)
//                    } else {
//                        viewModel.deleteAllSessions()
//                    }
//                    showDeleteDialog = false
//                    sessionToDelete = null
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.error
//                )
//            ) {
//                Text("Delete")
//            }
//        },
//        dismissButton = {
//            TextButton(
//                onClick = {
//                    showDeleteDialog = false
//                    sessionToDelete = null
//                }
//            ) {
//                Text("Cancel")
//            }
//        }
//    )
//}



fun formatTimestamp(ts: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy • HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(ts))
}








//@Composable
//@Preview
//fun preview(){
//    TheChefBotTheme {
//        MainScreen()
//    }
//}