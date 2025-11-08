package com.example.thechefbot.presentation.ChatBotFeat.model

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionPrefs(context: Context) {
    private val sp = context.getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
    private val KEY_LAST_SESSION = "last_session_id"

    fun saveLastSessionId(id: Int) {
        sp.edit().putInt(KEY_LAST_SESSION, id).apply()
    }

    fun getLastSessionId(): Int? =
        if (sp.contains(KEY_LAST_SESSION)) sp.getInt(KEY_LAST_SESSION, -1).takeIf { it != -1 } else null

    fun clearLastSession() {
        sp.edit().remove(KEY_LAST_SESSION).apply()
    }
}

class ThemePrefs(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_DARK = "is_dark_theme"
    }

    private val _isDarkFlow = MutableStateFlow(isDark())
    val isDarkFlow: StateFlow<Boolean> = _isDarkFlow.asStateFlow()

    fun isDark(): Boolean = prefs.getBoolean(KEY_IS_DARK, false)

    fun setDark(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK, enabled).commit()
        _isDarkFlow.value = enabled
        println("Theme set to: $enabled") // Debug log
    }
}
