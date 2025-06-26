package com.example.ai_image_generation_murphy_ai.ui

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Auth : Routes("auth")
    object Login : Routes("login")
    object Home : Routes("home")
    object Discover : Routes("discover")
    object Generate : Routes("generate")
    object Card : Routes("card")
    object Settings : Routes("settings")
}
