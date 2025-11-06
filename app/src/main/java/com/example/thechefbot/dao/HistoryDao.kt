package com.example.thechefbot.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.thechefbot.presentation.ChatBotFeat.model.data.ChatSession

import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Insert
    suspend fun insertSession(session: ChatSession): Long
    // returns new sessionId as Long

    @Update
    suspend fun updateSession(session: ChatSession)

    @Query("SELECT * FROM session_table WHERE email = :email ORDER BY lastUsedTimeStamp DESC")
    fun getAllSessions(email : String): Flow<List<ChatSession>>

    @Query("SELECT * FROM session_table WHERE sessionId = :id LIMIT 1")
    suspend fun getSessionById(id: Int): ChatSession?

    @Query("SELECT * FROM session_table WHERE sessionId = :id LIMIT 1")
    fun getSessionByIdFlow(id: Int): Flow<ChatSession?>

    @Query("DELETE FROM session_table WHERE email = :email")
    suspend fun deleteAllSessions(email : String)

    @Delete
    suspend fun deleteSession(session: ChatSession)

    @Query("DELETE FROM session_table WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Int)
}