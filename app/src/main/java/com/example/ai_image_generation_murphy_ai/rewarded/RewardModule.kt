package com.example.ai_image_generation_murphy_ai.rewarded

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RewardModule {

    @Provides
    fun provideUserRewardRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): UserRewardRepository {
        return UserRewardRepository(firestore, auth)
    }
}
