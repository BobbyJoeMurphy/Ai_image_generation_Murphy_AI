package com.example.ai_image_generation_murphy_ai.data.repository.repository

import android.util.Log
import com.example.ai_image_generation_murphy_ai.data.repository.api.ApiService
import com.example.ai_image_generation_murphy_ai.data.repository.model.GeneratedImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton
import kotlin.text.orEmpty

@Singleton
class ImageRepository(
    private val api: ApiService,
    private val firestore: FirebaseFirestore
) {
    suspend fun generateImage(prompt: String): Result<String> {
        return try {
            val response = api.generateImage(mapOf("prompt" to prompt))
            if (response.isSuccessful) {
                Result.success(response.body()?.imageUrl.orEmpty())
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublicImages(): List<GeneratedImage> {
        return try {
            val snapshot = firestore.collection("images")
                .whereEqualTo("public", true)
                .get()
                .await()

            snapshot.toObjects(GeneratedImage::class.java)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Error fetching images: ${e.localizedMessage}")
            emptyList()
        }
    }
    suspend fun getUserImages(): List<GeneratedImage> {
        return try {
            val snapshot = firestore.collection("images")
                .get()
                .await()
            val images = snapshot.toObjects(GeneratedImage::class.java)
            Log.d("ImageRepository", "Fetched ${images.size} images")
            images
        } catch (e: Exception) {
            Log.e("ImageRepository", "Error: ${e.localizedMessage}")
            emptyList()
        }
    }

    fun saveImageToFirestore(
        image: GeneratedImage,
        callback: (Boolean, Exception?) -> Unit
    ) {
        Firebase.firestore
            .collection("images")
            .add(image)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e) }
    }

    fun getPublicImages(onResult: (List<GeneratedImage>) -> Unit) {
        firestore.collection("images")
            .whereEqualTo("public", true)
            .get()
            .addOnSuccessListener { snapshot ->
                val images = snapshot.toObjects(GeneratedImage::class.java)
                Log.d("ImageRepository", "Fetched ${images.size} images from Firestore")
                onResult(images)
            }
            .addOnFailureListener { e ->
                Log.e("ImageRepository", "Firestore fetch failed: ${e.localizedMessage}")
            }
    }
}
