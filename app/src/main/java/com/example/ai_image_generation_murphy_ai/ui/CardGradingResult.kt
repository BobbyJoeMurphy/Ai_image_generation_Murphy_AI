package com.example.ai_image_generation_murphy_ai.ui

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ai_image_generation_murphy_ai.repository.ImageViewModel
import com.example.ai_image_generation_murphy_ai.rewarded.RewardedAdManager

@Composable
fun CardGradingScreen(
    onGradeCard: (Uri, isFront: Boolean) -> Unit,
    frontUri: Uri?,
    backUri: Uri?,
    frontResult: String?,
    backResult: String?,
    viewModel: ImageViewModel,
    hasCredits: () -> Boolean,
    consumeCredit: () -> Unit,
    earnCreditFromAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity


    LaunchedEffect(Unit) {
        RewardedAdManager.loadAd(context)
        viewModel.checkDailyReset()
    }

    val frontPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onGradeCard(it, true) }
    }

    val backPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onGradeCard(it, false) }
    }

    fun tryUseCreditOrAd(openPicker: () -> Unit) {
        if (viewModel.hasCredits()) {
            viewModel.consumeCredit()
            openPicker()
        } else {
            if (RewardedAdManager.isAdReady()) {
                Toast.makeText(context, "Ad is loading...", Toast.LENGTH_SHORT).show()
                RewardedAdManager.showAd(
                    activity = activity,
                    onUserEarnedReward = {
                        viewModel.earnCreditFromAd()
                    },
                    onAdDismissed = {
                        Toast.makeText(context, "Ad dismissed. No credit used.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Ad is still loading. Please try again soon.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pokémon Card Grader", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // FRONT CARD
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val frontButtonLabel = if (viewModel.hasCredits()) "Use Credit to Upload Front" else "Watch Ad to Upload Front"
                Button(onClick = { tryUseCreditOrAd { frontPicker.launch("image/*") } }) {
                    Text(frontButtonLabel)
                }

                Spacer(Modifier.height(8.dp))
                frontUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Front Card",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Front Grading Result:", style = MaterialTheme.typography.titleMedium)
                    Text(frontResult ?: "", modifier = Modifier.padding(top = 4.dp))
                }
            }

            // BACK CARD
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val backButtonLabel = if (hasCredits()) "Use Credit to Upload Back" else "Watch Ad to Upload Back"
                Button(onClick = { tryUseCreditOrAd { backPicker.launch("image/*") } }) {
                    Text(backButtonLabel)
                }

                Spacer(Modifier.height(8.dp))
                backUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Back Card",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Back Grading Result:", style = MaterialTheme.typography.titleMedium)
                    Text(backResult ?: "", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}
