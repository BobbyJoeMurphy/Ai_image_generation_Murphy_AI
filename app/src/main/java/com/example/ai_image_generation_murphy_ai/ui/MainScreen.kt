package com.example.ai_image_generation_murphy_ai.ui

import BottomBar
import DiscoverScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    navController: androidx.navigation.NavController,
    currentRoute: String,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                onGenerateClick = { navController.navigate("generate") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (currentRoute) {
                "home" -> HomeScreen()
                "discover" -> DiscoverScreen()
                "generate" -> GenerateScreen()
                "card" -> CardScreen()
                "settings" -> SettingsScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun CardScreen() {
    TODO("Not yet implemented")
}
