package com.example.thechefbot.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thechefbot.data.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(history: History): Long

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    fun getHistories(): Flow<List<History>>

    @Query("DELETE FROM history_table WHERE id = :id")
    fun deleteHistory(id: Int): Int

    @Query("SELECT * FROM history_table WHERE id = :id")
    fun getHistoryById(id: Int): Flow<History>
}