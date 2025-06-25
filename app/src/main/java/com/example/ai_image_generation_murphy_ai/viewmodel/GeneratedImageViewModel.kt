package com.example.ai_image_generation_murphy_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_image_generation_murphy_ai.data.repository.local.GeneratedImageDao
import com.example.ai_image_generation_murphy_ai.data.repository.local.GeneratedImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratedImageViewModel @Inject constructor(
    private val dao: GeneratedImageDao
) : ViewModel() {
    val allImages: StateFlow<List<GeneratedImageEntity>> = dao.getAllImages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveImage(prompt: String, imageUrl: String) {
        viewModelScope.launch {
            dao.insert(GeneratedImageEntity(prompt = prompt, imageUrl = imageUrl))
        }
    }
}

