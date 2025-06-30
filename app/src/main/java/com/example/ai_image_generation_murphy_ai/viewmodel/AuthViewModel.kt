package com.example.ai_image_generation_murphy_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_image_generation_murphy_ai.data.repository.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val showRegister = MutableStateFlow(false)

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val fullName = MutableStateFlow("")
    val username = MutableStateFlow("")

    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)
    val isLoggedIn = MutableStateFlow(userRepository.isLoggedIn())

    fun toggleAuthMode() {
        showRegister.value = !showRegister.value
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            userRepository.loginUser(email, password) { success, message ->
                isLoading.value = false
                isLoggedIn.value = success
                if (!success) error.value = message
                onResult(success, message)
            }
        }
    }

    fun register(
        email: String,
        password: String,
        fullName: String,
        username: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            userRepository.registerUser(email, password, fullName, username) { success, message ->
                isLoading.value = false
                isLoggedIn.value = success
                if (!success) error.value = message
                onResult(success, message)
            }
        }
    }

    fun logout() {
        userRepository.logout()
        isLoggedIn.value = false
        email.value = ""
        password.value = ""
        fullName.value = ""
        username.value = ""
    }

    fun setLoggedIn(value: Boolean) {
        isLoggedIn.value = value
    }
}
