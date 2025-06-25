package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ai_image_generation_murphy_ai.data.repository.api.RetrofitInstance
import com.example.ai_image_generation_murphy_ai.data.repository.model.ImageGenerationRequest
import com.example.ai_image_generation_murphy_ai.viewmodel.GeneratedImageViewModel
import kotlinx.coroutines.launch

@Composable
fun GenerateScreen(viewModel: GeneratedImageViewModel = hiltViewModel()) {
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generate an Image", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter a prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            isLoading = true
            coroutineScope.launch {
                try {
                    val response = RetrofitInstance.api.generateImage(
                        auth = "redacted fk u",
                        request = ImageGenerationRequest(prompt = prompt)
                    )
                    val url = response.data.firstOrNull()?.url
                    if (url != null) {
                        imageUrl = url
                        viewModel.saveImage(prompt, url)
                    }
                } catch (e: Exception) {
                    imageUrl = null
                    prompt = "Error: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
                }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Generate")
            }
        }

        imageUrl?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(model = it),
                contentDescription = "Generated Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}