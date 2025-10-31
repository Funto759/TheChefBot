package com.example.thechefbot.model

import com.example.thechefbot.dao.ChatDao
import com.example.thechefbot.dao.ChatSessionDao
import com.example.thechefbot.model.data.ChatMessage
import com.example.thechefbot.model.data.ChatSession
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val sessionDao: ChatSessionDao,
    private val messageDao: ChatDao
) {

    fun getAllSessions(): Flow<List<ChatSession>> =
        sessionDao.getAllSessions()

    fun getMessagesForSession(sessionId: Int): Flow<List<ChatMessage>> =
        messageDao.getMessagesForSession(sessionId)

    /**
     * Ensure there is a valid sessionId to attach messages to.
     * If `existingSessionId` is null, we create a brand new session
     * named after the user's first prompt.
     *
     * Returns the sessionId to use.
     */
    suspend fun ensureSession(
        existingSessionId: Int?,
        firstPromptForTitle: String
    ): Int {
        if (existingSessionId != null) {
            return existingSessionId
        }

        val now = System.currentTimeMillis()
        val newSession = ChatSession(
            title = generateTitleFromPrompt(firstPromptForTitle),
            lastUsedTimeStamp = now
        )
        val newId = sessionDao.insertSession(newSession) // returns Long
        return newId.toInt()
    }

    /**
     * Save a new question/answer pair under a given session,
     * and bump the session's lastUsedTimeStamp so it sorts to top.
     */
    suspend fun addMessageToSession(
        sessionId: Int,
        prompt: String,
        answer: String,
        imageUri: String? = null
    ) {
        val now = System.currentTimeMillis()

        // 1. insert message
        val message = ChatMessage(
            sessionOwnerId = sessionId,
            prompt = prompt,
            answer = answer,
            timestamp = now,
            imageUri = imageUri
        )
        messageDao.insertMessage(message)

        // 2. update session recency
        val currentSession = sessionDao.getSessionById(sessionId)
        if (currentSession != null) {
            sessionDao.updateSession(
                currentSession.copy(
                    lastUsedTimeStamp = now
                )
            )
        }
    }

    private fun generateTitleFromPrompt(prompt: String): String {
        // You can be smarter here. For now:
        // Take the first ~40 chars of the user's first question.
        return if (prompt.length <= 40) prompt
        else prompt.take(37) + "..."
    }
}