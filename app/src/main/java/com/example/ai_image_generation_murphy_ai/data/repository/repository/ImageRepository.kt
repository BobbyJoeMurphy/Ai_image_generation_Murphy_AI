package com.example.ai_image_generation_murphy_ai.data.repository.repository

import com.example.ai_image_generation_murphy_ai.data.repository.api.ApiService
import javax.inject.Singleton
import kotlin.text.orEmpty

@Singleton
class ImageRepository(private val api: ApiService) {
    suspend fun generateImage(prompt: String): Result<String> {
        return try {
            val response = api.generateImage(mapOf("prompt" to prompt))
            if (response.isSuccessful) {
                Result.success(response.body()?.imageUrl.orEmpty())
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
