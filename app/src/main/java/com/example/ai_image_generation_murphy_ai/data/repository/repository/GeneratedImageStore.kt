package com.example.ai_image_generation_murphy_ai.data.repository.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.ai_image_generation_murphy_ai.data.repository.model.GeneratedImage

object GeneratedImageStore {
    val imageList = mutableStateListOf<GeneratedImage>()
}
