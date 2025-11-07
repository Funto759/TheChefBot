package com.example.thechefbot.presentation.ChatBotFeat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_table")
data class ChatSession(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int = 0,

    // Human-friendly label for the conversation. You can generate this
    // from the first user prompt ("What can I cook with tuna?").
    val title: String?,

    // When this session was last active (epoch millis).
    // Useful for sorting sessions by "most recent".
    val lastUsedTimeStamp: Long,

    val email : String
)

@Entity(tableName = "message_table")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int = 0,

    // FK-like link to ChatSession.sessionId
    val sessionOwnerId: Int,

    // user email
    val email : String,

    // The question from the user
    val prompt: String,

    // The AI's reply
    val answer: String,

    val imageUri: String?,

    // When this exchange happened
    val timestamp: Long
)