package com.example.thechefbot.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val output: String,
    val imageUri: String?,
    val timestamp: Long = System.currentTimeMillis()
)