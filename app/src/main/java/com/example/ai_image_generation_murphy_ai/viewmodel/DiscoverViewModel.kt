package com.example.ai_image_generation_murphy_ai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_image_generation_murphy_ai.data.repository.model.GeneratedImage
import com.example.ai_image_generation_murphy_ai.data.repository.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _publicImages = MutableStateFlow<List<GeneratedImage>>(emptyList())
    val publicImages: StateFlow<List<GeneratedImage>> = _publicImages

    fun fetchPublicImages() {
        viewModelScope.launch {
            val images = imageRepository.getPublicImages()
            _publicImages.value = images
        }
    }
}
