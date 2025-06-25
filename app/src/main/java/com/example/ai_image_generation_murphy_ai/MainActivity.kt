package com.example.ai_image_generation_murphy_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ai_image_generation_murphy_ai.ui.AppNavHost
import com.example.ai_image_generation_murphy_ai.ui.theme.Ai_image_generation_Murphy_AITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ai_image_generation_Murphy_AITheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
