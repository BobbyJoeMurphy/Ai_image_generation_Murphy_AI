package com.example.ai_image_generation_murphy_ai.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GeneratedImageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun generatedImageDao(): GeneratedImageDao
}