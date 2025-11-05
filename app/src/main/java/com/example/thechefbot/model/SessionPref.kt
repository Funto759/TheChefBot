package com.example.thechefbot.model

import android.content.Context

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
