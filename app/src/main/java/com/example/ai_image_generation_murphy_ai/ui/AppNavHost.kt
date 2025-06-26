package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ai_image_generation_murphy_ai.viewmodel.AuthViewModel
import com.example.ai_image_generation_murphy_ai.ui.screens.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Auth.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val showRegister by viewModel.showRegister.collectAsState()

            AuthScreen(
                viewModel = viewModel,
                showRegister = showRegister,
                onToggleAuthMode = { viewModel.toggleAuthMode() },
                onAuthSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onSkipLogin = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ✅ Bottom nav destinations (real roots)
        composable(Routes.Home.route) {
            MainScreen(
                currentRoute = Routes.Home.route,
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Discover.route) {
            MainScreen(
                currentRoute = Routes.Discover.route,
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Discover.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Generate.route) {
            MainScreen(
                currentRoute = Routes.Generate.route,
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Generate.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Card.route) {
            MainScreen(
                currentRoute = Routes.Card.route,
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Card.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Settings.route) {
            MainScreen(
                currentRoute = Routes.Settings.route,
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.Auth.route) {
                        popUpTo(Routes.Settings.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
