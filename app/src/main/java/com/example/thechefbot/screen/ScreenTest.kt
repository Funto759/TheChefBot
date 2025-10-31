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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
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
import com.example.thechefbot.model.state.ChefUiState
import com.example.thechefbot.util.CommonUtil.copyToClipboard
import com.example.thechefbot.util.CommonUtil.parseMarkdown
import com.example.thechefbot.util.shimmer
import com.example.thechefbot.util.shimmerLoading
import com.google.firebase.Timestamp
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val viewModel = koinViewModel<RecipeViewModel>()
    val chefUiState by viewModel.chefUiState.collectAsStateWithLifecycle()
    val messages by viewModel.messagesForActiveSession.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(Uri.EMPTY) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var expanded by rememberSaveable { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { saveImageToInternalStorage(context, it, viewModel = viewModel) }
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


    MainScreen(
        modifier = Modifier,
        viewModel = viewModel,
        chefUiState = chefUiState,
        messages = messages,
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
            expanded = !expanded
        },
        expanded = expanded,
        keyboardController = keyboardController
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier,
               viewModel: RecipeViewModel,
               chefUiState: ChefUiState,
               messages: List<ChatMessage>,
               context: Context,
               launchPhotoPicker: () -> Unit = {},
               launchCamera: () -> Unit = {},
               toggleExpanded: () -> Unit = {},
               expanded: Boolean,
keyboardController: SoftwareKeyboardController?) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        topBar = {
         TopChefBar(
             scrollBehavior = scrollBehavior,
             modifier = modifier
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
        ConversationArea(
            modifier = modifier,
            paddingValues = innerPadding,
            messages = messages,
            chefUiState = chefUiState,
            context = context
        )
    }
}





@Composable
fun ConversationArea(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    chefUiState: ChefUiState,
    context: Context
) {
    if (messages.isEmpty() && !chefUiState.loading) {
        // initial landing state
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(paddingValues = paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = "Hello, Ask me Anything....",
                    fontSize = 24.sp
                )
            }
            item {
                Text(
                    text = "Last Update:"
                )
            }
            item {
                ContentBody(modifier = Modifier)
            }
        }
    } else {
        // We have chat history OR we are loading
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp).padding(paddingValues = paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            items(messages.size) { index ->
                val msg = messages[index]
                ChatMessageRow(msg = msg, context = context)
            }

            if (chefUiState.loading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
//                        CircularProgressIndicator()
//                        Text(text = "Loading")
                        ChatBubble(
                            text = chefUiState.prompt,
                            timestamp = 11,
                            isUser = true,
                            isMarkdown = false,
                            loading = true
                        )
                    }
                }
            }

            if (chefUiState.errorState) {
                item {
                    Text(
                        text = chefUiState.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}



    @Composable
    fun ContentBody(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.height(24.dp))

        Icon(
            painter = painterResource(R.drawable.ic_sun),
            contentDescription = "",
            tint = Color.Unspecified,
        )
        Spacer(modifier = modifier.height(14.dp))
        ElevatedCardExample()
        Spacer(modifier = modifier.height(14.dp))
        ElevatedCardExample()
        Spacer(modifier = modifier.height(24.dp))



        Icon(
            painter = painterResource(R.drawable.ic_cloud),
            contentDescription = "",
            tint = Color.Unspecified
        )
        Spacer(modifier = modifier.height(14.dp))
        ElevatedCardExample()
        Spacer(modifier = modifier.height(14.dp))
        ElevatedCardExample()
        Spacer(modifier = modifier.height(24.dp))
        Icon(
            painter = painterResource(R.drawable.ic_lightning),
            contentDescription = "",
            tint = Color.Unspecified
        )
        Spacer(modifier = modifier.height(14.dp))
        ElevatedCardExample()
        Spacer(modifier = modifier.height(14.dp))
        ElevatedCardExample()
        Spacer(modifier = modifier.height(24.dp))

    }

@Composable
fun ElevatedCardExample(modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp, start = 15.dp)
    ) {
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround){
            Icon(
                Icons.Default.SportsBaseball,
                contentDescription = "",
                modifier = modifier
                    .padding(10.dp))
            Text(
                text = "Remember what the user entered in former enquiries",
                modifier = modifier
                    .padding(10.dp),
                textAlign = TextAlign.Start,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ChatBubble(
    text: String,
    timestamp: Long,
    isUser: Boolean,
    isMarkdown: Boolean,
    modifier: Modifier = Modifier,
    loading : Boolean = false, 
    onClick : () -> Unit = {}
) {
    // colors
    val bubbleColor =
        if (isUser) Color.DarkGray
        else Color.DarkGray

    val textColor =
        if (isUser) Color.LightGray
        else Color.LightGray

    // alignment: user on the right, bot on the left
    val horizontalAlignment =
        if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        // the bubble itself
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp) // don't let it stretch full width
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            if (isMarkdown) {
                MarkdownViewer(
                    markdownText = text,
                    timestamp = timestamp, // we'll ignore timestamp inside viewer now
                    modifier = Modifier.fillMaxWidth(),
                    color = textColor
                )
            } else {
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    modifier = if (loading) modifier.shimmer() else modifier
                )
            }
        }

        Row(
            modifier = modifier.widthIn(max = 280.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // timestamp under bubble
            if (isMarkdown) {
            Text(
                text = formatTimestamp(timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, end = 10.dp)
            )
            }

            if (!loading) {
                IconButton(
                    onClick = {
                        onClick()
                    },
                    modifier = modifier.size(25.dp).padding(start = 3.dp, end = 7.dp)
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = "Localized description")
                }
            }
        }
    }
}

@Composable
fun ChatImageBubble(
    imageUri: String,
    timestamp: Long,
    isUser: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val bubbleColor = Color.DarkGray
    val horizontalAlignment =
        if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 220.dp) // slightly narrower
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(8.dp)
        ) {
            AsyncImage(
                model = Uri.parse(imageUri),
                contentDescription = "user attachment",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp))
            )
        }

    }
}


@Composable
fun MarkdownViewer(
    markdownText: String,
    timestamp: Long,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val parsedContent = parseMarkdown(markdownText, color,timestamp)

    Column(modifier = modifier) {
        parsedContent.forEach { component ->
            component()
        }
    }
}

@Composable
fun ChatMessageRow(msg: ChatMessage, modifier: Modifier = Modifier,context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {

        if (msg.imageUri != null) {
            ChatImageBubble(
                imageUri = msg.imageUri,
                timestamp = msg.timestamp,
                isUser = true,
                onClick = {
                    // maybe later: open fullscreen, copy, etc.
                }
            )
            Spacer(modifier = modifier.height(6.dp))
        }

        ChatBubble(
            text = msg.prompt,
            timestamp = msg.timestamp,
            isUser = true,
            isMarkdown = false,
            onClick = {
                copyToClipboard(context = context, text = msg.prompt)
            }
        )

        Spacer(modifier = modifier.height(8.dp))



        ChatBubble(
            text = msg.answer,
            timestamp = msg.timestamp,
            isUser = false,
            isMarkdown = true,
            onClick = {
                copyToClipboard(context = context, text = msg.answer)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopChefBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Recipe Generator")
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.SubdirectoryArrowLeft,
                    contentDescription = ""
                )
            }

        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = ""
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun SendButton(modifier: Modifier, chefUiState: ChefUiState, context: Context, viewModel: RecipeViewModel,keyboardController: SoftwareKeyboardController?) {
    IconButton(
        modifier = modifier.padding(5.dp),
        onClick = {
            val enabled = chefUiState.prompt.isNotEmpty() || chefUiState.selectedImages != Uri.EMPTY

            if (chefUiState.prompt.isNotEmpty() && chefUiState.selectedImages != null) {
                    viewModel.handleEvent(ChefScreenEvents.GenerateRecipeWithImage(
                        context = context,
                        prompt = chefUiState.prompt,
                        imageUri = chefUiState.selectedImages,
                        sessionId = 1
                    ))
                keyboardController?.hide()
                }else if (chefUiState.prompt.isNotEmpty()) {
                println("2")
                    viewModel.handleEvent(ChefScreenEvents.GenerateRecipe(prompt = chefUiState.prompt, sessionId = 1))
                keyboardController?.hide()
                }else{
                    Toast.makeText(context, "Please enter a prompt", Toast.LENGTH_SHORT).show()
            }
        }) {
        Icon(Icons.Filled.Send, contentDescription = "Send message")
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
//            IconButton(onClick = { copyToClipboard(context, chefUiState.result) }) {
//                Icon(Icons.Filled.ContentPaste, contentDescription = "Localized description")
//            }
            IconButton(onClick = {
                toggleExpanded()
            }) {
                if (chefUiState.selectedImages != null){
                    AsyncImage(
                        model = chefUiState.selectedImages,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                }else {
                    Icon(
                        Icons.Filled.Image,
                        contentDescription = "Localized description",
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { toggleExpanded() }
            ) {
                DropdownMenuItem(
                    text = { Text("Cancel") },
                    onClick = {
                        toggleExpanded
                        viewModel.handleEvent(ChefScreenEvents.ClearImage)
                       },
                    leadingIcon = {
                        Icon(Icons.Default.Cancel, contentDescription = "Localized description")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Camera") },
                    onClick = {
                        toggleExpanded()
                        launchCamera() },
                    leadingIcon = {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Localized description")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Gallery") },
                    onClick = {
                        toggleExpanded()
                        launchPhotoPicker() },
                    leadingIcon = {
                        Icon(Icons.Default.Image, contentDescription = "Localized description")
                    }
                )
            }

            OutlinedTextField(
                value = if (chefUiState.loading) "" else chefUiState.prompt,
                onValueChange = {
                    viewModel.handleEvent(ChefScreenEvents.UpdatePrompt(it))
                },
                label = { Text(
                    "Ask me anything...",
                    modifier = modifier.clip(RoundedCornerShape(25.dp))
                ) },
                modifier = modifier,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    disabledLabelColor = Color.Gray,
                    cursorColor = Color.Black
                ),

                )

        },
        floatingActionButton = {
            SendButton(modifier = modifier, chefUiState = chefUiState, context = context, viewModel = viewModel,keyboardController = keyboardController)
        },
    )
}



fun formatTimestamp(ts: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy • HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(ts))
}

fun saveImageToInternalStorage(context: Context, uri: Uri,viewModel: RecipeViewModel) {
    val fileName = UUID.randomUUID().toString() + ".jpg"
    val inputStream = context.contentResolver.openInputStream(uri)

    val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    val savedImageFile = File(context.filesDir, fileName)

    val savedImageUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        savedImageFile
    )

    viewModel.handleEvent(ChefScreenEvents.UpdateSelectedImage(savedImageUri))
}

fun launchPhotoPicker(photoPicker : ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) =
    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


fun launchCamera(context: Context, cameraUri : (Uri?) -> Unit, permissionLauncher: ManagedActivityResultLauncher<String, Boolean>, cameraLauncher:  ManagedActivityResultLauncher<Uri, Boolean>) {
    if (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
       val camera = context.contentResolver.insert(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            android.content.ContentValues()
        )
       cameraUri(camera)
    } else {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}






//@Composable
//@Preview
//fun preview(){
//    TheChefBotTheme {
//        MainScreen()
//    }
//}