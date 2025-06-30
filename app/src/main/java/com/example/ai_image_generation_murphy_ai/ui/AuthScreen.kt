package com.example.ai_image_generation_murphy_ai.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ai_image_generation_murphy_ai.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    showRegister: Boolean,
    onToggleAuthMode: () -> Unit,
    onAuthSuccess: () -> Unit = {}
) {
    val context = LocalContext.current

    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val isRegisterMode = showRegister

    // NEW: Local state for fullName and username (only needed in register mode)
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) onAuthSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegisterMode) "Register" else "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Email") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (isRegisterMode) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (error != null) {
            Text(error!!, color = Color.Red)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (isRegisterMode) {
                    viewModel.register(
                        email = viewModel.email.value,
                        password = viewModel.password.value,
                        fullName = fullName,
                        username = username
                    ) { success, message ->
                        if (success) {
                            Toast.makeText(context, "Registration successful 🎉", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        } else {
                            Toast.makeText(context, message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    viewModel.login(
                        email = viewModel.email.value,
                        password = viewModel.password.value
                    ) { success, message ->
                        if (success) {
                            Toast.makeText(context, "Login successful 🎉", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        } else {
                            Toast.makeText(context, message ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isRegisterMode) "Register" else "Login")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onToggleAuthMode) {
            Text(
                if (isRegisterMode)
                    "Already have an account? Login"
                else
                    "No account? Register"
            )
        }

        if (isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
