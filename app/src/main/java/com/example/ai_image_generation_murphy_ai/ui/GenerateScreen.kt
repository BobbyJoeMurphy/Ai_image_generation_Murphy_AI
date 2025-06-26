package com.example.ai_image_generation_murphy_ai.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.example.ai_image_generation_murphy_ai.data.repository.api.RetrofitInstance
import com.example.ai_image_generation_murphy_ai.data.repository.model.ImageGenerationRequest
import com.example.ai_image_generation_murphy_ai.repository.ImageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GenerateScreen(viewModel: ImageViewModel = hiltViewModel()) {
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isPublic by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generate an Image", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter a prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(model = it),
                contentDescription = "Generated Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Make image public")
            Switch(checked = isPublic, onCheckedChange = { isPublic = it })
        }

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val response = RetrofitInstance.api.generateImage(
                            auth = "Fk u",
                            request = ImageGenerationRequest(prompt = prompt)
                        )
                        val url = response.data.firstOrNull()?.url
                        if (url != null) {
                            val bitmap: Bitmap = withContext(Dispatchers.IO) {
                                Glide.with(context)
                                    .asBitmap()
                                    .load(url)
                                    .submit()
                                    .get()
                            }

                            viewModel.uploadAndSaveImage(bitmap, prompt, isPublic) { success, error ->
                                if (success) {
                                    println("Image uploaded and saved successfully!")
                                } else {
                                    println("Upload failed: $error")
                                }
                            }

                            imageUrl = url
                        }
                    } catch (e: Exception) {
                        imageUrl = null
                        prompt = "Error: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Generate")
            }
        }
    }
}
