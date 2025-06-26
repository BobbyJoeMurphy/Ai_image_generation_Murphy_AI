package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ai_image_generation_murphy_ai.ui.screens.SplashScreen
import com.example.ai_image_generation_murphy_ai.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue

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
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("auth") {
            val viewModel: AuthViewModel = hiltViewModel()
            val showRegister by viewModel.showRegister.collectAsState()

            AuthScreen(
                viewModel = viewModel,
                showRegister = showRegister,
                onToggleAuthMode = { viewModel.toggleAuthMode() },
                onAuthSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onSkipLogin = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
