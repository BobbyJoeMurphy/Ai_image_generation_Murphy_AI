package com.example.ai_image_generation_murphy_ai.rewarded

object UserCreditsManager {
    private const val MAX_FREE_CREDITS_PER_DAY = 5

    private var credits: Int = MAX_FREE_CREDITS_PER_DAY
    private var isPremium: Boolean = false

    fun getCredits(): Int = credits
    fun isPremiumUser(): Boolean = isPremium

    fun consumeCredit(): Boolean {
        return if (isPremium || credits > 0) {
            if (!isPremium) credits--
            true
        } else {
            false
        }
    }

    fun addCredit(amount: Int = 1) {
        credits += amount
    }

    fun setPremium(value: Boolean) {
        isPremium = value
    }

    fun resetDailyCredits() {
        credits = MAX_FREE_CREDITS_PER_DAY
    }
}
