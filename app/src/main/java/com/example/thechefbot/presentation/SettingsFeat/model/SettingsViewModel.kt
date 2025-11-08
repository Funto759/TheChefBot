package com.example.thechefbot.presentation.SettingsFeat.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thechefbot.presentation.ChatBotFeat.model.SessionPrefs
import com.example.thechefbot.presentation.ChatBotFeat.model.ThemePrefs

import com.example.thechefbot.presentation.SettingsFeat.data.AppUser
import com.example.thechefbot.presentation.SettingsFeat.events.SettingEvents
import com.example.thechefbot.presentation.SettingsFeat.state.SettingsState
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


class SettingsViewModel (
    private val repo: UserRepository,
    private val sessionPrefs: SessionPrefs,
    private val themePrefs: ThemePrefs,
): ViewModel() {


    private val _user = MutableStateFlow<AppUser?>(null)
    val user = _user.asStateFlow()

   private val _profileUiState = MutableStateFlow(SettingsState())
    val profileUiState = _profileUiState.asStateFlow()


    private var listener: ListenerRegistration? = null

    init {
        handleIntents(SettingEvents.ConnectListener)
        _profileUiState.update {
            it.copy(isDark = themePrefs.isDark())
        }
    }

    fun handleIntents(events : SettingEvents){
       when(events){
           is SettingEvents.ToggleTheme -> {
               toggleTheme(events.enabled)
           }
           is SettingEvents.DeleteLastSession -> {
               deleteLastSession()
           }
          is SettingEvents.ConnectListener -> {
               connectListener()
           }
           is SettingEvents.UpdateBio -> {
               updateBio(events.bio)
           }
           is SettingEvents.UpdateEmail -> {
               updateEmail(events.email)
           }
           is SettingEvents.UpdateFullName -> {
               updateFullName(events.fullName)
           }

           is SettingEvents.UpdatePhoneNumber -> {
               updatePhone(events.phoneNumber)
           }
           is SettingEvents.UpdatePhotoUrl -> TODO()
           is SettingEvents.UpdateUser -> {
               updateFields(
                   phone_number = events.user.phone_number,
                   photoUrl = events.user.photoUrl,
                   email = events.user.email,
                   fullName = events.user.full_name,
                   bio = events.user.bio
               ) { ok, mess ->
                   if (ok) {
                       handleIntents(SettingEvents.IsEditable(false))
                   }
               }
           }

           is SettingEvents.IsEditable -> {
               println("isEditable: ${events.isEditable}")
               _profileUiState.value = _profileUiState.value.copy(
                   isEditable = events.isEditable
               )
           }

           is SettingEvents.OnBackPressed -> {
               _profileUiState.value = _profileUiState.value.copy(
                   onBackPressed = events.onBackPressed
               )
           }
       }
    }

    fun connectListener(){
        // start listening immediately if signed in
        listener = repo.listenCurrentUser { user ->
            println("User: $user")
            _user.value = user
            updateFullName(user?.full_name.orEmpty())
            updateBio(user?.bio.orEmpty())
            updateEmail(user?.email.orEmpty())
            updatePhone(user?.phone_number.orEmpty())

        }
    }

    fun updateFullName(name: String){
        _profileUiState.update {
            it.copy(
                fullName = name
            )
        }
    }


    fun updateBio(bio: String){
        _profileUiState.update {
            it.copy(
                bio = bio
            )
        }
    }

    fun updateEmail(email: String){
        _profileUiState.update {
            it.copy(
                email = email
            )
        }

    }

    fun updatePhone(phone: String){
        _profileUiState.update {
            it.copy(
                phone = phone
            )
        }
    }

    fun deleteLastSession(){
        viewModelScope.launch {
            sessionPrefs.clearLastSession()
        }
    }


    fun setThemeStatus(){
        viewModelScope.launch {
            themePrefs.isDarkFlow.collect { isDark ->
                _profileUiState.update {
                    it.copy(isDark = isDark)
                }
            }
        }
    }

    fun toggleTheme(enabled: Boolean) {
        println("ViewModel: Toggling theme to $enabled") // Debug log

        // Update ThemePrefs (triggers MainActivity to recompose)
        themePrefs.setDark(enabled)

        // Update local state (for toggle UI)
        _profileUiState.update {
            it.copy(isDark = enabled)
        }
    }


    fun createOrMergeUser(extra: AppUser? = null, onDone: (Boolean, String?) -> Unit) {
        repo.upsertCurrentUser(extra, onDone)
    }

    fun updateFields(phone_number: String? = null,photoUrl: String? = null,email : String? = null,fullName: String? = null, bio: String? = null, onDone: (Boolean, String?) -> Unit) {
        val map = mapOf(
            "photoUrl" to photoUrl,
            "email" to email,
            "full_name" to fullName,
            "bio" to bio,
            "updatedAt" to System.currentTimeMillis(),
            "phone_number" to phone_number
        )
        repo.updateCurrentUser(map, onDone)
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}