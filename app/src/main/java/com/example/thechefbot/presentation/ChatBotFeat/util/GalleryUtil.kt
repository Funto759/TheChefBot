package com.example.thechefbot.presentation.ChatBotFeat.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.example.thechefbot.presentation.ChatBotFeat.events.ChefScreenEvents
import com.example.thechefbot.presentation.ChatBotFeat.model.RecipeViewModel
import com.example.thechefbot.util.saveImageToInternalStorage

@Composable
 fun rememberPhotoPickerLauncher(
    context: Context,
    viewModel: RecipeViewModel
): androidx.activity.compose.ManagedActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { saveImageToInternalStorage(context, it, onSelected = { newUri ->
                viewModel.handleEvent(ChefScreenEvents.UpdateSelectedImage(newUri))
            }) }
        }
    )
}

@Composable
 fun rememberCameraLauncher(
    onUriReady: (Uri?) -> Unit
): ActivityResultLauncher<Uri> {
    return rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onUriReady(null) // URI is already updated, we can send it back
    }
}
