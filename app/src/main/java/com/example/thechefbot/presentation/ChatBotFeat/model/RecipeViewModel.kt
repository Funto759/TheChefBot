package com.example.thechefbot.presentation.ChatBotFeat.model

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatSession
import com.example.thechefbot.presentation.ChatBotFeat.model.events.ChefScreenEvents
import com.example.thechefbot.presentation.ChatBotFeat.model.state.ChefUiState
import com.example.thechefbot.presentation.SettingsFeat.events.SettingEvents
import com.example.thechefbot.presentation.SettingsFeat.model.UserRepository
import com.example.thechefbot.util.CommonUtil.getBitmapFromUri
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.MissingFieldException

class RecipeViewModel(
    private val generativeModel: GenerativeModel,
    private val chatRepository: ChatRepository,
    private val sessionPrefs: SessionPrefs,
    private val repo: UserRepository
) : ViewModel() {

    private val _activeSessionId = MutableStateFlow<Int?>(null)

    //    private val _activeSessionId = MutableStateFlow<Int?>(null)
    val activeSessionId = _activeSessionId.asStateFlow()

    val _chefUiState = MutableStateFlow(ChefUiState())
    val chefUiState = _chefUiState.asStateFlow()




    private var listener: ListenerRegistration? = null


    init {
       connectListener()
        viewModelScope.launch {
            val newSessionId = sessionPrefs.getLastSessionId()
            if (newSessionId != null){
                openSession(newSessionId)
            }else {
                createNewSession()
            }
        }
    }

    // expose messages for the currently active session
    // when session changes, we switch which flow we collect
    val messagesForActiveSession: StateFlow<List<ChatMessage>> =
        activeSessionId
            .flatMapLatest { sessionId ->
                if ( sessionId == null) {
                    flowOf(emptyList())
                } else {
                    chatRepository.getMessagesForSession(sessionId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // expose all sessions for a "history list" screen
    val allSessions: StateFlow<List<ChatSession>> =
        chefUiState
            .flatMapLatest { email ->
                if (email.userEmail.isEmpty()) flowOf(emptyList())
                else chatRepository.getAllSessions(email.userEmail)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    val selectedSession : StateFlow<ChatSession?> = activeSessionId
        .flatMapLatest { sessionId ->
            if ( sessionId == null) {
                flowOf(null)
            } else {
                chatRepository.getSession(sessionId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    // call this when you open the chat screen
    fun openSession(sessionId: Int?) {
        _activeSessionId.value = sessionId
        viewModelScope.launch {
            sessionPrefs.saveLastSessionId(sessionId ?: return@launch)
        }
        _chefUiState.update {
            it.copy(activeSessionId = sessionId)
        }

    }

    // NEW: Create a new chat session
    fun createNewSession(title : String? = null) {
        viewModelScope.launch {
            val email = requireUserEmailOrReturn() ?: return@launch
            val newSessionId = chatRepository.createNewSession(title = title, userEmail = _chefUiState.value.userEmail)
            openSession(newSessionId)

            // Clear UI state for fresh start
            _chefUiState.update {
                it.copy(
                    prompt = "",
                    selectedImages = null,
                    result = "",
                    errorState = false,
                    error = ""
                )
            }
        }
    }

    // NEW: Delete a session
    fun deleteSession(sessionId: Int) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)

            // If we deleted the active session, create a new one
            if (_chefUiState.value.activeSessionId == sessionId) {
                createNewSession()
            }
        }
    }

    // NEW: Delete all sessions
    fun deleteAllSessions() {
        viewModelScope.launch {
            chatRepository.deleteAllSessions(email = _chefUiState.value.userEmail)
            createNewSession() // Create a fresh session
        }
    }

    // NEW: Rename a session
    fun renameSession(sessionId: Int, newTitle: String) {
        viewModelScope.launch {
            chatRepository.renameSession(sessionId, newTitle)
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
            it.copy(
                prompt = "",
                selectedImages = null
            )
        }
    }

    fun clearImage() {
        _chefUiState.update {
            it.copy(selectedImages = null)
        }
    }


    fun sendPrompt(sessionId: Int, prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _chefUiState.update {
                it.copy(loading = true)
            }
            try {
                val email = requireUserEmailOrReturn() ?: return@launch
                // 1. Make sure we have / create a session for this conversation
                val ensuredSessionId = chatRepository.ensureSession(
                    existingSessionId = _chefUiState.value.activeSessionId,
                    firstPromptForTitle = prompt,
                    userEmail = _chefUiState.value.userEmail
                )

                _activeSessionId.value = ensuredSessionId
                _chefUiState.update {
                    it.copy(activeSessionId = ensuredSessionId)
                }
                val recipe = generativeModel.generateContent(
                    content {
                        text("$prompt")
                    }
                )
                val outputContent = recipe.text
                if (outputContent.isNullOrBlank()) {
                    // model came back but with no text
                    _chefUiState.update {
                        it.copy(
                            loading = false,
                            success = false,
                            errorState = true,
                            error = "No response received from the model"
                        )
                    }
                    return@launch
                }
                if ((selectedSession.value?.title ?: null) == null){
                    renameSession(ensuredSessionId, prompt)
                }

                // 3. Save prompt+answer to DB under this session
                chatRepository.addMessageToSession(
                    sessionId = ensuredSessionId,
                    prompt = prompt,
                    answer = outputContent,
                    imageUri = null,
                    userEmail = _chefUiState.value.userEmail
                )

                // 4. UI state cleanup: loading off, success true.
                _chefUiState.update {
                    it.copy(
                        loading = false,
                        success = true,
                        errorState = false,
                        error = "",
                        result = "",  // we don't rely on this for display anymore
                    )
                }

                // 5. clear input field
                handleEvent(ChefScreenEvents.ClearPrompt)

            } catch (e: Exception) {
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

                    e is MissingFieldException ->
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

    fun sendPromptedImage(context: Context, imageUri: Uri?, prompt: String, sessionId: Int) {
        viewModelScope.launch {
            _chefUiState.update {
                it.copy(loading = true)
            }
            val bitmap = getBitmapFromUri(
                context,
                imageUri
            )

            try {

                val email = requireUserEmailOrReturn() ?: return@launch

                // 1. Make / ensure session
                val ensuredSessionId = chatRepository.ensureSession(
                    existingSessionId = _chefUiState.value.activeSessionId,
                    firstPromptForTitle = prompt,
                    userEmail = _chefUiState.value.userEmail
                )
                _activeSessionId.value = ensuredSessionId
                _chefUiState.update {
                    it.copy()
                }

                val recipe = generativeModel.generateContent(
                    content {
                        image(bitmap!!)
                        text(prompt)
                    }
                )  // Sending query to Gemini API
                val outputContent = recipe.text
                if (outputContent.isNullOrBlank()) {
                    // model came back but with no text
                    _chefUiState.update {
                        it.copy(
                            loading = false,
                            success = false,
                            errorState = true,
                            error = "No response received from the model"
                        )
                    }
                    return@launch
                }

                if ((selectedSession.value?.title ?: null) == null){
                    renameSession(ensuredSessionId, prompt)
                }

                // 3. Save prompt+answer to DB under this session
                chatRepository.addMessageToSession(
                    sessionId = ensuredSessionId,
                    prompt = prompt,
                    answer = outputContent,
                    imageUri = imageUri.toString(),
                    userEmail = _chefUiState.value.userEmail
                )

                // 4. UI state cleanup: loading off, success true.
                _chefUiState.update {
                    it.copy(
                        loading = false,
                        success = true,
                        errorState = false,
                        error = "",
                        result = "",  // we don't rely on this for display anymore
                    )
                }

                // 5. clear input field
                handleEvent(ChefScreenEvents.ClearPrompt)

            } catch (e: Exception) {
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

                    e is MissingFieldException ->
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

    fun updateSessionToDelete(sessionId: Int){
        _chefUiState.update {
            it.copy(sessionToDelete = sessionId)
        }
    }

    fun resetSessionToDelete(){
        _chefUiState.update {
            it.copy(sessionToDelete = null,
                showDeleteDialog = false
            )
        }
    }

    fun updateShowDeleteDialog(status : Boolean, sessionToDelete : Int?){
        _chefUiState.update {
            it.copy(
                showDeleteDialog = status
                , sessionToDelete = sessionToDelete
            )
        }
        println("${chefUiState.value.showDeleteDialog} : ${chefUiState.value.sessionToDelete}")
    }


    fun toggleGalleryMenu(){
        _chefUiState.update {
            it.copy(expanded = !it.expanded)
        }
    }

    fun toggleSettingsMenu(){
        _chefUiState.update {
            it.copy(settingsToggleExpanded = !it.settingsToggleExpanded)
        }
    }

    fun connectListener(){
        // start listening immediately if signed in
        listener = repo.listenCurrentUser { user ->
            println("User: $user")
            updateUserEmail(user?.email.orEmpty())
        }
    }

    fun updateUserEmail(email : String){
        _chefUiState.update {
            it.copy(
                userEmail = email
            )
        }
    }

    private fun requireUserEmailOrReturn(): String? {
        val email = _chefUiState.value.userEmail
        if (email.isBlank()) {
            _chefUiState.update { it.copy(errorState = true, error = "Not signed in.") }
            return null
        }
        return email
    }



    fun handleEvent(event: ChefScreenEvents) {
        when (event) {
            is ChefScreenEvents.ToggleGalleryMenuExpanded -> {
                toggleGalleryMenu()
            }
            is ChefScreenEvents.ToggleSettingsMenuExpanded -> {
                toggleSettingsMenu()
            }
            is ChefScreenEvents.UpdateSelectedImage -> {
                updateSelectedImage(event.imageUri)
            }
            is ChefScreenEvents.UpdateShowDialogStatus -> {
                updateShowDeleteDialog(event.status, event.sessionToDelete)
            }
            is ChefScreenEvents.UpdateSessionToDelete -> {
                updateSessionToDelete(event.sessionId!!)
            }
            is ChefScreenEvents.ResetSessionToDelete -> {
                resetSessionToDelete()
            }
            is ChefScreenEvents.GenerateRecipe -> {
                sendPrompt(event.sessionId, event.prompt)
            }

            is ChefScreenEvents.GenerateRecipeWithImage -> {
                sendPromptedImage(event.context, event.imageUri, event.prompt, event.sessionId)
            }

            is ChefScreenEvents.UpdatePrompt -> {
                updatePrompt(event.prompt)
            }

            is ChefScreenEvents.ClearPrompt -> {
                clearPrompt()
            }

            is ChefScreenEvents.ResetState -> {
                resetState(event.state)
            }

            is ChefScreenEvents.ClearImage -> {
                clearImage()
            }

            is ChefScreenEvents.DeleteAllSessions -> {
               deleteAllSessions()
                handleEvent(ChefScreenEvents.ResetSessionToDelete)
            }

            is ChefScreenEvents.DeleteSession -> {
                deleteSession(event.sessionId!!)
                handleEvent(ChefScreenEvents.ResetSessionToDelete)
            }

            is ChefScreenEvents.CreateNewSession -> {
                createNewSession()
            }

            is ChefScreenEvents.OpenSession -> {
                openSession(event.sessionId)
            }

        }
    }

}
