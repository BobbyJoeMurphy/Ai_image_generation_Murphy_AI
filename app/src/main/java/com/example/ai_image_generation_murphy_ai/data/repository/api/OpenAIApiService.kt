package com.example.ai_image_generation_murphy_ai.data.repository.api


import com.example.ai_image_generation_murphy_ai.data.repository.model.ImageGenerationRequest
import com.example.ai_image_generation_murphy_ai.data.repository.model.ImageGenerationResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIApiService {
    @POST("v1/images/generations")
    suspend fun generateImage(
        @Header("Authorization") auth: String,
        @Body request: ImageGenerationRequest
    ): ImageGenerationResponse
}