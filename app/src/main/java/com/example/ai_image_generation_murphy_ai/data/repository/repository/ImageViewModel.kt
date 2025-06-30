package com.example.ai_image_generation_murphy_ai.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_image_generation_murphy_ai.data.repository.ImageUploader
import com.example.ai_image_generation_murphy_ai.data.repository.model.GeneratedImage
import com.example.ai_image_generation_murphy_ai.data.repository.repository.ImageRepository
import com.example.ai_image_generation_murphy_ai.rewarded.UserRewardRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val rewardRepository: UserRewardRepository
) : ViewModel() {

    private val _creditsLeft = mutableStateOf(5)
    val creditsLeft: State<Int> = _creditsLeft

    private val _isPremium = mutableStateOf(false)
    val isPremium: State<Boolean> = _isPremium

    init {
        checkDailyReset()
    }


    fun hasCredits(): Boolean = creditsLeft.value > 0 || isPremium.value

    fun checkDailyReset() {
        viewModelScope.launch {
            rewardRepository.resetDailyCreditsIfNeeded()
            val data = rewardRepository.getRewardData()
            _creditsLeft.value = data?.dailyCreditsRemaining ?: 5
            _isPremium.value = data?.isPremium ?: false
        }
    }

    fun consumeCredit() {
        viewModelScope.launch {
            rewardRepository.decrementCredit()
            _creditsLeft.value -= 1
        }
    }

    fun earnCreditFromAd() {
        viewModelScope.launch {
            rewardRepository.addCredit(1)
            _creditsLeft.value += 1
        }
    }

    fun uploadAndSaveImage(
        bitmap: Bitmap,
        prompt: String,
        isPublic: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(false, "User not authenticated.")
            return
        }

        ImageUploader.uploadImageBitmap(
            bitmap = bitmap,
            onSuccess = { downloadUrl ->
                val image = GeneratedImage(
                    prompt = prompt,
                    imageUrl = downloadUrl,
                    isPublic = isPublic,
                    userId = userId
                )

                imageRepository.saveImageToFirestore(image) { success, error ->
                    onResult(success, error.toString())
                }
            },
            onFailure = { error ->
                onResult(false, error?.message ?: "Upload failed.")
            }
        )
    }
}
