package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ai_image_generation_murphy_ai.data.repository.repository.GeneratedImageStore
import com.example.ai_image_generation_murphy_ai.data.repository.repository.GeneratedImageStore.imageList
import com.example.ai_image_generation_murphy_ai.viewmodel.GeneratedImageViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: GeneratedImageViewModel = hiltViewModel()
) {
    val images by viewModel.allImages.collectAsState()

    var currentImageIndex by remember { mutableStateOf<Int?>(null) }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(images) { index, image ->
                    AsyncImage(
                        model = image.imageUrl,
                        contentDescription = "Generated image",
                        modifier = Modifier
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .clickable {
                                currentImageIndex = index
                            }
                    )
                }
            }

            // Fullscreen overlay
            currentImageIndex?.let { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { currentImageIndex = null },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = imageList[index].imageUrl,
                                contentDescription = "Full image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                            Text(
                                text = imageList[index].prompt,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Left nav
                        if (index > 0) {
                            IconButton(
                                onClick = { currentImageIndex = index - 1 },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Previous image",
                                    tint = Color.White
                                )
                            }
                        }

                        // Right nav
                        if (index < imageList.size - 1) {
                            IconButton(
                                onClick = { currentImageIndex = index + 1 },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Next image",
                                    tint = Color.White
                                )
                            }
                        }

                        // Close button
                        IconButton(
                            onClick = { currentImageIndex = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
