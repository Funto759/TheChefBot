package com.example.thechefbot.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thechefbot.events.ChefScreenEvents
import com.example.thechefbot.state.ChefUiState
import com.example.thechefbot.util.CommonUtil.getBitmapFromUri
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class RecipeViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    val _chefUiState = MutableStateFlow(ChefUiState())
    val chefUiState = _chefUiState.asStateFlow()


    fun handleEvent(event: ChefScreenEvents) {
        when(event) {
            is ChefScreenEvents.GenerateRecipe -> {
                sendPrompt(event.prompt)
            }
            is ChefScreenEvents.GenerateRecipeWithImage -> {
                sendPromptedImage(event.context, event.imageUri, event.prompt)
            }
            is ChefScreenEvents.UpdatePrompt -> {
                updatePrompt(event.prompt)
            }
            is ChefScreenEvents.ClearPrompt -> {
                clearPrompt()
            }
            is ChefScreenEvents.UpdateSelectedImage -> {
                updateSelectedImage(event.imageUri)
            }
            is ChefScreenEvents.ResetState -> {
                resetState(event.state)
            }

        }
    }



    fun resetState(state: Boolean) {
        _chefUiState.update {
            it.copy(
                loading = false,
                success = false,
                errorState = false,
                error = "",
                result = "",
                prompt = "",
                imageMode = state
            )
        }
    }

    fun updateSelectedImage(imageUri: Uri?) {
        _chefUiState.update {
            it.copy(selectedImages = imageUri)
        }
    }



    fun updatePrompt(prompt: String) {
        _chefUiState.update {
            it.copy(prompt = prompt)
        }
    }

    fun clearPrompt() {
        _chefUiState.update {
            it.copy(prompt = "")
        }
    }


    fun sendPrompt(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _chefUiState.update {
                it.copy(loading = true)
            }
            try {
                val recipe = generativeModel.generateContent(
                    content {
                        text("$prompt Recipe")
                    }
                )
                recipe.text?.let { outputContent ->
                    println(outputContent)
//                addHistory(prompt, outputContent, null)
                    _chefUiState.update {
                        it.copy(
                            loading = false,
                            success = true,
                            errorState = false,
                            error = "",
                            result = outputContent
                        )
                    }
                }?: run {
                    // Handle case where text is null
                    _chefUiState.update {
                        it.copy(
                            loading = false,
                            success = false,
                            errorState = true,
                            error = "No response received from the model"
                        )
                    }
                }
        } catch (e: Exception){
            println(e.message.toString())

                e.printStackTrace()

                val errorMessage = when {
                    e.message?.contains("503") == true ||
                            e.message?.contains("overloaded") == true ->
                        "The AI service is currently busy. Please try again in a few moments."

                    e.message?.contains("429") == true ->
                        "Too many requests. Please wait a moment before trying again."

                    e.message?.contains("401") == true ||
                            e.message?.contains("API key") == true ->
                        "Invalid API key. Please check your configuration."

                    e.message?.contains("network") == true ||
                            e.message?.contains("timeout") == true ->
                        "Network error. Please check your connection."

                    e is kotlinx.serialization.MissingFieldException ->
                        "Service temporarily unavailable. Please try again."

                    else ->
                        "An error occurred: ${e.message ?: "Unknown error"}"
                }

                _chefUiState.update {
                    it.copy(
                        loading = false,
                        success = false,
                        errorState = true,
                        error = errorMessage
                    )
                }
        }
    }
    }

    fun sendPromptedImage(context: Context, imageUri: Uri?,prompt: String) {
        _chefUiState.update {
            it.copy(loading = true)
        }
        val bitmap = getBitmapFromUri(
            context,
            imageUri
        )

        try {
            viewModelScope.launch {
                val recipe = generativeModel.generateContent(
                    content {
                        image(bitmap!!)
                        text(prompt)
                    }
                )  // Sending query to Gemini API
                recipe.text?.let { outputContent ->
//                    addHistory(prompt, outputContent, imageUri)
                    _chefUiState.update {
                        it.copy(
                            loading = false,
                            success = true,
                            errorState = false,
                            error = "",
                            result = outputContent
                        )
                    }
                } // Handle response and save to database
            }
        } catch(e: Exception) {
            e.printStackTrace()

            val errorMessage = when {
                e.message?.contains("503") == true ||
                        e.message?.contains("overloaded") == true ->
                    "The AI service is currently busy. Please try again in a few moments."

                e.message?.contains("429") == true ->
                    "Too many requests. Please wait a moment before trying again."

                e.message?.contains("401") == true ||
                        e.message?.contains("API key") == true ->
                    "Invalid API key. Please check your configuration."

                e.message?.contains("network") == true ||
                        e.message?.contains("timeout") == true ->
                    "Network error. Please check your connection."

                e is kotlinx.serialization.MissingFieldException ->
                    "Service temporarily unavailable. Please try again."

                else ->
                    "An error occurred: ${e.message ?: "Unknown error"}"
            }

            _chefUiState.update {
                it.copy(
                    loading = false,
                    success = false,
                    errorState = true,
                    error = errorMessage
                )
            }
        }
    }

}