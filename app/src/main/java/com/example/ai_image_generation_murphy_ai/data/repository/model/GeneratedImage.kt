package com.example.ai_image_generation_murphy_ai.data.repository.model

import androidx.annotation.Keep

@Keep
data class GeneratedImage(
    val id: String = "",
    val prompt: String = "",
    val imageUrl: String = "",
    val isPublic: Boolean = true,
    val userId: String = ""
)