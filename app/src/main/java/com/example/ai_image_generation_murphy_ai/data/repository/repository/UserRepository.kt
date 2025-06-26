package com.example.ai_image_generation_murphy_ai.data.repository.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    fun registerUser(
        email: String,
        password: String,
        fullName: String,
        username: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid
                    if (uid == null) {
                        onResult(false, "User ID not found after registration.")
                        return@addOnCompleteListener
                    }

                    val userMap = mapOf(
                        "uid" to uid,
                        "email" to email,
                        "fullName" to fullName,
                        "username" to username
                    )

                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Failed to write user data: ${e.localizedMessage}")
                            onResult(false, e.localizedMessage ?: "Failed to save user info.")
                        }
                } else {
                    val errorMsg = when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> "Registration failed."
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                        else -> exception?.localizedMessage ?: "Registration failed."
                    }
                    onResult(false, errorMsg)
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage ?: "Unknown error occurred during registration.")
            }
    }
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorMsg = when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> "User not found."
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
                        else -> exception?.localizedMessage ?: "Login failed."
                    }
                    onResult(false, errorMsg)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
