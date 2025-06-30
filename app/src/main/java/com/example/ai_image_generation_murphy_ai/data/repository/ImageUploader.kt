package com.example.ai_image_generation_murphy_ai.data.repository

import android.graphics.Bitmap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

object ImageUploader {

    fun uploadImageBitmap(
        bitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)

        uploadTask
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // ✅ Firebase Storage URL
                }.addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }
}
