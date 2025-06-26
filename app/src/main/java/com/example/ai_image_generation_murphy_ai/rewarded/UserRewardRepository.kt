package com.example.ai_image_generation_murphy_ai.rewarded

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.jvm.java

class UserRewardRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun getUserDoc() = firestore.collection("users").document(auth.currentUser?.uid ?: "unknown")

    suspend fun getRewardData(): RewardData? {
        val snapshot = getUserDoc().get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(RewardData::class.java)
        } else null
    }

    suspend fun updateCredits(remaining: Int) {
        getUserDoc().update("dailyCreditsRemaining", remaining).await()
    }

    suspend fun setPremiumStatus(premium: Boolean) {
        getUserDoc().update("isPremium", premium).await()
    }

    suspend fun resetDailyCreditsIfNeeded(defaultCredits: Int = 5) {
        val today = LocalDate.now().toString()
        val doc = getUserDoc()
        val snapshot = doc.get().await()

        val lastClaimed = snapshot.getString("lastClaimedDate")
        if (lastClaimed != today) {
            doc.set(
                mapOf(
                    "dailyCreditsRemaining" to defaultCredits,
                    "lastClaimedDate" to today
                ),
                SetOptions.merge()
            ).await()
        }
    }

    suspend fun decrementCredit() {
        val doc = getUserDoc()
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(doc)
            val remaining = snapshot.getLong("dailyCreditsRemaining") ?: 0
            if (remaining > 0) {
                transaction.update(doc, "dailyCreditsRemaining", remaining - 1)
            }
        }.await()
    }

    suspend fun addCredit(count: Int) {
        val doc = getUserDoc()
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(doc)
            val current = snapshot.getLong("dailyCreditsRemaining") ?: 0
            transaction.update(doc, "dailyCreditsRemaining", current + count)
        }.await()
    }
}
