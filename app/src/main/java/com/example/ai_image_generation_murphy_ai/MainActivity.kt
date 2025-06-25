package com.example.ai_image_generation_murphy_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ai_image_generation_murphy_ai.ui.LoginScreen
import com.example.ai_image_generation_murphy_ai.ui.screens.SplashScreen
import com.example.ai_image_generation_murphy_ai.ui.theme.Ai_image_generation_Murphy_AITheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ai_image_generation_Murphy_AITheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)  // Apply padding here
                    ) {
                        composable(route = "splash") {
                            SplashScreen()
                            LaunchedEffect(Unit) {
                                delay(2000)
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                        composable(route = "login") {
                            LoginScreen(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}
