package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ai_image_generation_murphy_ai.ui.screens.HomeScreen
import com.example.ai_image_generation_murphy_ai.ui.screens.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(route = "login") {
                        popUpTo(route = "splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                modifier = Modifier,
                onSkipLogin = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen()
        }
    }
}