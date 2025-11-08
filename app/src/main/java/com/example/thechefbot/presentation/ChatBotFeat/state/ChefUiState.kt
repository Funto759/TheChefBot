package com.example.thechefbot.presentation.ChatBotFeat.state

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession


@Immutable
data class ChefUiState(
    val loading : Boolean = false,
    val error : String = "",
    val success : Boolean = false,
    val errorState : Boolean = false,
    val expanded : Boolean = false,
    val settingsToggleExpanded : Boolean = false,
    val prompt : String = "",
    val newTitle : String = "",
    val result : String = "",
    val imageMode : Boolean = false,
    val selectedImages : Uri? = null,
    val imageUri : Uri? = null,
    val showDeleteDialog : Boolean = false,
    val showRenameDialog : Boolean = false,
    val sessionToDelete : Int? = null,
    val allSessions : List<ChatSession> = emptyList(),
    val activeSessionId : Int? = null,
    val session : ChatSession? = null,
    val userEmail : String = ""
)
