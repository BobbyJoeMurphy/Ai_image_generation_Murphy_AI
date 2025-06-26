package com.example.ai_image_generation_murphy_ai.repository

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.ai_image_generation_murphy_ai.data.repository.ImageUploader
import com.example.ai_image_generation_murphy_ai.data.repository.model.GeneratedImage
import com.example.ai_image_generation_murphy_ai.data.repository.repository.ImageRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

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
