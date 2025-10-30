package com.example.thechefbot.screen

import android.R.attr.textColor
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.thechefbot.events.ChefScreenEvents
import com.example.thechefbot.model.RecipeViewModel
import com.example.thechefbot.state.ChefUiState
import com.example.thechefbot.ui.theme.TheChefBotTheme
import com.example.thechefbot.util.CommonUtil.copyToClipboard
import com.example.thechefbot.util.CommonUtil.parseMarkdown
import com.google.firebase.platforminfo.DefaultUserAgentPublisher.component
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.util.UUID
import kotlin.text.insert

@Composable
fun RecipeScreen(navHostController: NavHostController){

    val viewModel = koinViewModel<RecipeViewModel>()
    val chefUiState by viewModel.chefUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(Uri.EMPTY) }
    val keyboardController = LocalSoftwareKeyboardController.current


    fun saveImageToInternalStorage(context: Context, uri: Uri) {
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

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { saveImageToInternalStorage(context, it) }
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

    fun launchPhotoPicker() =
        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


    fun launchCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraUri = context.contentResolver.insert(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                android.content.ContentValues()
            )
            cameraUri?.let {
                cameraLauncher.launch(it)
            }
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }






    FullScreen(
        modifier = Modifier,
        viewModel = viewModel,
        chefUiState = chefUiState,
        context = context,
        launchCamera = { launchCamera(context) },
        launchPhotoPicker = { launchPhotoPicker() }
    )

}








@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreen(modifier: Modifier = Modifier,viewModel: RecipeViewModel, chefUiState: ChefUiState,context: Context,
               launchCamera: () -> Unit = {},
               launchPhotoPicker: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Recipe Generator")
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "History")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val enabled = chefUiState.prompt.isNotEmpty() || chefUiState.selectedImages != Uri.EMPTY

                if (enabled) {
                    if (chefUiState.imageMode){
                        viewModel.handleEvent(ChefScreenEvents.GenerateRecipeWithImage(
                            context = context,
                            prompt = chefUiState.prompt,
                            imageUri = chefUiState.selectedImages
                        ))
                    }else {
                        viewModel.handleEvent(ChefScreenEvents.GenerateRecipe(prompt = chefUiState.prompt))
                    }
                }
            }) {
                Text(text = "Generate", modifier = modifier.padding(16.dp))
            }
        },
        modifier = modifier.imePadding()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (!chefUiState.imageMode) {
            Button(
                onClick = {
                    viewModel.handleEvent(ChefScreenEvents.ResetState(true))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Generate based on Image Instead")
            }
            OutlinedTextField(
                value = chefUiState.prompt,
                label = { Text("Recipe Prompt") },
                onValueChange = { viewModel.handleEvent(ChefScreenEvents.UpdatePrompt(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            } else {
                Button(
                    onClick = {
                      viewModel.handleEvent(ChefScreenEvents.ResetState(false))

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Generate based on Text Instead")
                }
                Row(modifier = Modifier.padding(16.dp)) {
                    Button(onClick = { launchCamera() }, modifier = Modifier.weight(0.5f)) {
                        Text("Camera")
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(onClick = { launchPhotoPicker() }, modifier = Modifier.weight(0.5f)) {
                        Text("Gallery")
                    }
                }
                AsyncImage(
                    model = chefUiState.selectedImages,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(150.dp),
                    contentScale = ContentScale.FillWidth
                )
            }

            if (chefUiState.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                var textColor = MaterialTheme.colorScheme.onSurface
                if (chefUiState.errorState) {
                    textColor = MaterialTheme.colorScheme.error
                } else if (chefUiState.success) {
                    textColor = MaterialTheme.colorScheme.onSurface

                }
                val scrollState = rememberScrollState()
                Column {
                    if (chefUiState.result.isNotEmpty()) {
                        Button(
                            onClick = {
                                copyToClipboard(context, chefUiState.result)
                            },
                            Modifier.padding(start = 16.dp)
                        ) {
                            Text("Copy this Recipe!")
                        }
                    }
                    MarkdownViewer(
                        markdownText = if (!chefUiState.errorState) chefUiState.result else chefUiState.error,
                        color = textColor,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    )
                }
            }

        }
    }
}

@Composable
fun MarkdownViewer(
    markdownText: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val parsedContent = parseMarkdown(markdownText, color)

    Column(modifier = modifier) {
        parsedContent.forEach { component ->
            component()
        }
    }
}

@Composable
@Preview
fun prev(){
    TheChefBotTheme {
        RecipeScreen(navHostController = NavHostController(LocalContext.current))

    }
}