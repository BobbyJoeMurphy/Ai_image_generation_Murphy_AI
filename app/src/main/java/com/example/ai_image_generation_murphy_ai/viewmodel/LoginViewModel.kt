package com.example.ai_image_generation_murphy_ai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_image_generation_murphy_ai.data.repository.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            repository.loginUser(email, password) { success, error ->
                isLoading = false
                if (!success) {
                    errorMessage = error ?: "Login failed"
                }
            }
        }
    }
}


