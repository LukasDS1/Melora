package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit,   // Acción para “volver” a Home
    onGoRegister: () -> Unit // Acción para ir a Registro
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val bg = MaterialTheme.colorScheme.secondaryContainer // Fondo distinto para contraste

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa
            .background(bg) // Fondo
            .padding(16.dp), // Margen
        contentAlignment = Alignment.Center // Centro
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium // Título
            )

            Spacer(Modifier.height(12.dp)) // Separación

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(20.dp)) // Separación

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    if (email == "test@example.com" && password == "1234") {
                        onLoginOk()
                    } else {
                        errorMessage = "Email or password incorrect."
                    }
                } else {
                    errorMessage = "Por favor completa todos los campos"
                }
            }) {
                Text("Iniciar Sesión")
            }
        }
    }
}