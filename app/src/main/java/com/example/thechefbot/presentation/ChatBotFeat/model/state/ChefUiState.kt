package com.example.thechefbot.presentation.ChatBotFeat.model.state

import android.net.Uri
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatSession

data class ChefUiState(
    val loading : Boolean = false,
    val error : String = "",
    val success : Boolean = false,
    val errorState : Boolean = false,
    val expanded : Boolean = false,
    val settingsToggleExpanded : Boolean = false,
    val prompt : String = "",
    val result : String = "",
    val imageMode : Boolean = false,
    val selectedImages : Uri? = null,
    val imageUri : Uri? = null,
    val showDeleteDialog : Boolean = false,
    val sessionToDelete : Int? = null,
    val allSessions : List<ChatSession> = emptyList(),
    val activeSessionId : Int? = null,
    val session : ChatSession? = null
)
