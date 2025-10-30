package com.example.thechefbot.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.thechefbot.data.History

@Database(entities = [History::class], version = 1)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}