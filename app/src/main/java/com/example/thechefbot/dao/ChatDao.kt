package com.example.thechefbot.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insertMessage(message: ChatMessage): Long

    // Get the full transcript of a session, newest last
    @Query("SELECT * FROM message_table WHERE sessionOwnerId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Int): Flow<List<ChatMessage>>

    @Query("DELETE FROM message_table WHERE sessionOwnerId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: Int)

    @Query("DELETE FROM message_table WHERE email = :email")
    suspend fun deleteAllMessages(email : String)
}