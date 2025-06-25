package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val images = listOf(
        "https://picsum.photos/300/300",
        "https://picsum.photos/301/300",
        "https://picsum.photos/300/301",
        "https://picsum.photos/302/300",
        "https://picsum.photos/300/302",
        "https://picsum.photos/303/300"
    )
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(8.dp),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(images) { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Generated image",
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f)  // square images
                )
            }
        }
    }
}
