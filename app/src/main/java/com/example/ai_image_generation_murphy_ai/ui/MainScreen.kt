package com.example.ai_image_generation_murphy_ai.ui

import BottomBar
import DiscoverScreen
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ai_image_generation_murphy_ai.repository.ImageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

@Composable
fun MainScreen(
    navController: NavController,
    currentRoute: String,
    onLogout: () -> Unit
) {
    val viewModel: ImageViewModel = hiltViewModel()

    var frontUri by remember { mutableStateOf<Uri?>(null) }
    var backUri by remember { mutableStateOf<Uri?>(null) }
    var frontResult by remember { mutableStateOf<String?>(null) }
    var backResult by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun handleCardSelected(uri: Uri, isFront: Boolean) {
        if (isFront) {
            frontUri = uri
            frontResult = "Grading front..."
        } else {
            backUri = uri
            backResult = "Grading back..."
        }

        coroutineScope.launch {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val imageBytes = inputStream?.readBytes()
                inputStream?.close()

                if (imageBytes != null) {
                    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                    val prompt = """
You are simulating a Pokémon TCG card rating system based on the given card. Return only the following in this format:

hypothetically grade this,

Corners: [1–10]
Edges: [1–10]
Centering: [1–10]
Nicks/Markings: [1–10]
Overall: [1–10]

No explanations, notes, or extra text—just the scores.
""".trimIndent()

                    val result = withContext(Dispatchers.IO) {
                        OpenAIApi.gradeCardFromImage(base64Image, prompt)
                    }

                    if (isFront) frontResult = result else backResult = result
                } else {
                    if (isFront) frontResult = "Failed to read front image."
                    else backResult = "Failed to read back image."
                }
            } catch (e: Exception) {
                if (isFront) frontResult = "Error grading front: ${e.localizedMessage}"
                else backResult = "Error grading back: ${e.localizedMessage}"
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                onGenerateClick = { navController.navigate("generate") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentRoute) {
                "home" -> HomeScreen()
                "discover" -> DiscoverScreen()
                "generate" -> GenerateScreen()
                "card" -> CardGradingScreen(
                    onGradeCard = { uri, isFront -> handleCardSelected(uri, isFront) },
                    frontUri = frontUri,
                    backUri = backUri,
                    frontResult = frontResult,
                    backResult = backResult,
                    viewModel = viewModel,
                    hasCredits = { viewModel.hasCredits() },
                    consumeCredit = { viewModel.consumeCredit() },
                    earnCreditFromAd = { viewModel.earnCreditFromAd() }
                )
                "settings" -> SettingsScreen(onLogout = onLogout)
            }
        }
    }
}
