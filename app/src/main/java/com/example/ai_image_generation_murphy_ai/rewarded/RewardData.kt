package com.example.ai_image_generation_murphy_ai.rewarded

data class RewardData(
    val dailyCreditsRemaining: Int = 5,
    val lastClaimedDate: String = "",  // Store in format "yyyy-MM-dd"
    val isPremium: Boolean = false
)
