package com.example.ai_image_generation_murphy_ai.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_image_generation_murphy_ai.data.repository.local.GeneratedImageDao
import com.example.ai_image_generation_murphy_ai.data.repository.local.GeneratedImageEntity
import com.example.ai_image_generation_murphy_ai.data.repository.model.GeneratedImage
import com.example.ai_image_generation_murphy_ai.data.repository.repository.ImageRepository
import com.example.ai_image_generation_murphy_ai.rewarded.UserRewardRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GeneratedImageViewModel @Inject constructor(
    private val dao: GeneratedImageDao,
    private val rewardRepository: UserRewardRepository,
    private val imageRepository: ImageRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _allImages = MutableStateFlow<List<GeneratedImage>>(emptyList())
    val allImages: StateFlow<List<GeneratedImage>> = _allImages

    init {
        fetchImagesFromFirestore()
    }

    fun fetchImagesFromFirestore() {
        viewModelScope.launch {
            val images = imageRepository.getUserImages()
            _allImages.value = images
        }
    }

    fun saveImage(prompt: String, imageUrl: String, isPublic: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileName = "img_${System.currentTimeMillis()}.png"
                val savedPath = saveImageToGallery(imageUrl, fileName)

                dao.insert(GeneratedImageEntity(prompt = prompt, localPath = savedPath))

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val image = GeneratedImage(
                    id = UUID.randomUUID().toString(),
                    prompt = prompt,
                    imageUrl = imageUrl,
                    isPublic = isPublic,
                    userId = userId
                )
                imageRepository.saveImageToFirestore(image) { success, error ->
                    if (!success) {
                        Log.e("GeneratedImageViewModel", "Firestore error: $error")
                    }
                }

            } catch (e: Exception) {
                Log.e("GeneratedImageViewModel", "Failed to save image: ${e.message}")
            }
        }
    }

    private fun saveImageToGallery(imageUrl: String, fileName: String): String {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/AI Image Generator"
            )
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException("Failed to create MediaStore entry")

        val inputStream = OkHttpClient().newCall(Request.Builder().url(imageUrl).build())
            .execute().body?.byteStream()
            ?: throw IOException("Failed to download image")

        resolver.openOutputStream(uri)?.use { outputStream ->
            inputStream.copyTo(outputStream)
        } ?: throw IOException("Failed to open output stream")

        return uri.toString()
    }
}
