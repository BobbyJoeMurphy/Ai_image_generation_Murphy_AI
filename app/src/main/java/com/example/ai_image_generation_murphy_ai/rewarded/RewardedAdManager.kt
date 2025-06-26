package com.example.ai_image_generation_murphy_ai.rewarded

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun isAdReady(): Boolean = rewardedAd != null

    fun loadAd(context: Context) {
        if (isAdReady() || isLoading) return // Already loaded or loading

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                isLoading = false
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                isLoading = false
                Log.e("AdManager", "Failed to load: ${adError.message}")
            }
        })
    }

    fun showAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadAd(activity) // Preload next ad
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    rewardedAd = null
                }
            }

            ad.show(activity) {
                onUserEarnedReward()
            }
        }
    }
}
