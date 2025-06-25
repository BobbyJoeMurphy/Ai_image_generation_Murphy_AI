package com.example.ai_image_generation_murphy_ai.di

import com.example.ai_image_generation_murphy_ai.data.repository.api.ApiService
import com.example.ai_image_generation_murphy_ai.data.repository.repository.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://your-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): ImageRepository {
        return ImageRepository(apiService)
    }
}
