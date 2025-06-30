package com.example.ai_image_generation_murphy_ai.data.repository.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "generated_images_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideGeneratedImageDao(db: AppDatabase): GeneratedImageDao {
        return db.generatedImageDao()
    }
}
