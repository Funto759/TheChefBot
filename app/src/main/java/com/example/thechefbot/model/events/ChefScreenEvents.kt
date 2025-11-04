package com.example.thechefbot.model.events

import android.content.Context
import android.net.Uri


sealed interface ChefScreenEvents {
    data class GenerateRecipe(val prompt: String, val sessionId: Int) : ChefScreenEvents

    data class GenerateRecipeWithImage(val context: Context, val prompt: String, val imageUri: Uri?, val sessionId: Int) : ChefScreenEvents
    data class UpdatePrompt(val prompt: String) : ChefScreenEvents
    data object ClearPrompt : ChefScreenEvents
    data object ClearImage : ChefScreenEvents
    data class UpdateSelectedImage(val imageUri: Uri?) : ChefScreenEvents

    data class ResetState(val state: Boolean) : ChefScreenEvents

    data class DeleteSession(val sessionId : Int?) : ChefScreenEvents

//    data object RenameSession : ChefScreenEvents


    data class UpdateShowDialogStatus(val status : Boolean) : ChefScreenEvents


    data class OpenSession(val sessionId : Int) : ChefScreenEvents

    data object CreateNewSession : ChefScreenEvents

    data object DeleteAllSessions : ChefScreenEvents

    data class UpdateSessionToDelete(val sessionId : Int?) : ChefScreenEvents

    data object ResetSessionToDelete : ChefScreenEvents



}


