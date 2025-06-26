package com.example.ai_image_generation_murphy_ai.data.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val localPath: String,
    val timestamp: Long = System.currentTimeMillis()
)