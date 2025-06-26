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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GeneratedImageViewModel @Inject constructor(
    private val dao: GeneratedImageDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val allImages: StateFlow<List<GeneratedImageEntity>> = dao.getAllImages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun saveImage(prompt: String, imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileName = "img_${System.currentTimeMillis()}.png"
                val savedPath = saveImageToGallery(imageUrl, fileName)
                dao.insert(GeneratedImageEntity(prompt = prompt, localPath = savedPath))
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
