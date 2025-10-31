package com.example.thechefbot.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.thechefbot.model.data.ChatSession

import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Insert
    suspend fun insertSession(session: ChatSession): Long
    // returns new sessionId as Long

    @Update
    suspend fun updateSession(session: ChatSession)

    @Query("SELECT * FROM session_table ORDER BY lastUsedTimeStamp DESC")
    fun getAllSessions(): Flow<List<ChatSession>>

    @Query("SELECT * FROM session_table WHERE sessionId = :id LIMIT 1")
    suspend fun getSessionById(id: Int): ChatSession?
}