package com.example.ai_image_generation_murphy_ai.di

import com.example.ai_image_generation_murphy_ai.data.repository.api.ApiService
import com.example.ai_image_generation_murphy_ai.data.repository.repository.ImageRepository
import com.example.ai_image_generation_murphy_ai.data.repository.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
            .baseUrl("https://your-api.com/") // Replace with your real base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideImageRepository(
        apiService: ApiService,
        firestore: FirebaseFirestore
    ): ImageRepository {
        return ImageRepository(apiService, firestore)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): UserRepository {
        return UserRepository(firebaseAuth, firestore)
    }
}
