package com.example.ai_image_generation_murphy_ai.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ai_image_generation_murphy_ai.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun AuthGate(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val showRegister by viewModel.showRegister.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    if (!isLoggedIn) {
        AuthScreen(
            viewModel = viewModel,
            showRegister = showRegister,
            onToggleAuthMode = { viewModel.toggleAuthMode() },
            onAuthSuccess = {
                viewModel.setLoggedIn(true) // Optional, if manual trigger is needed
            }
        )
    } else {
        content()
    }
}

