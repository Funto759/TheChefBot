package com.example.thechefbot.events

import android.content.Context
import android.net.Uri


sealed interface ChefScreenEvents {
    data class GenerateRecipe(val prompt: String) : ChefScreenEvents

    data class GenerateRecipeWithImage(val context: Context, val prompt: String, val imageUri: Uri?) : ChefScreenEvents
    data class UpdatePrompt(val prompt: String) : ChefScreenEvents
    data object ClearPrompt : ChefScreenEvents
    data class UpdateSelectedImage(val imageUri: Uri?) : ChefScreenEvents

    data class ResetState(val state: Boolean) : ChefScreenEvents

}


