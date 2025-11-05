package com.example.thechefbot.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thechefbot.model.data.ChatMessage
import com.example.thechefbot.model.data.ChatSession

@Database(entities = [ChatSession::class, ChatMessage::class], version = 3, exportSchema = true)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun chatSessionDao(): ChatSessionDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add the new column to existing table
        database.execSQL(
            "ALTER TABLE message_table ADD COLUMN imageUri TEXT"
        )
    }
}

