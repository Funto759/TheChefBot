package com.example.thechefbot.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thechefbot.dao.ChatDao
import com.example.thechefbot.dao.ChatSessionDao
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatMessage
import com.example.thechefbot.presentation.ChatBotFeat.data.ChatSession

@Database(entities = [ChatSession::class, ChatMessage::class], version = 4, exportSchema = true)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun chatSessionDao(): ChatSessionDao
}

val MIGRATION_1_2 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE message_table ADD COLUMN email TEXT"
        )
        database.execSQL(
            "ALTER TABLE session_table ADD COLUMN email TEXT"
        )
    }
}

