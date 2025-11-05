package com.example.thechefbot.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

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

fun saveImageToInternalStorage(context: Context, uri: Uri,onSelected : (Uri) -> Unit) {
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

    onSelected(savedImageUri)


}


