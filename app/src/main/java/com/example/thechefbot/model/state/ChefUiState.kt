package com.example.thechefbot.model.state

import android.net.Uri

data class ChefUiState(
    val loading : Boolean = false,
    val error : String = "",
    val success : Boolean = false,
    val errorState : Boolean = false,
    val prompt : String = "",
    val result : String = "",
    val imageMode : Boolean = false,
    val selectedImages : Uri? = null,
    val imageUri : Uri? = null
)
