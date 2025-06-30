package com.example.ai_image_generation_murphy_ai.data.repository.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("your-endpoint")
    suspend fun generateImage(@Body prompt: Map<String, String>): Response<ImageResponse>
}
