package com.example.ai_image_generation_murphy_ai.ui

import BottomBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                onGenerateClick = { navController.navigate("generate") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("discover") { DiscoverScreen() }
            composable("generate") { GenerateScreen() }
            composable("card") { CardScreen() }
            composable("settings") {
                SettingsScreen(
                    onLogout = onLogout
                )
            }
        }
    }
}


@Composable
fun CardScreen() {
    TODO("Not yet implemented")
}


@Composable
fun DiscoverScreen() {
    TODO("Not yet implemented")
}
