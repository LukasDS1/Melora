package com.example.melora.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember {mutableStateOf<String?>(null)}
    val bg = PrimaryBg

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
//            .padding(16.dp)
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 40.dp, bottom = 650.dp)
        ) {
            Text(
                "Hello!",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth() // Usa to.do el ancho de la pantalla
                .align(Alignment.BottomCenter) // Alinea el card abajo y centrado
                .heightIn(min = 200.dp, max = 650.dp) // Limita el alto
                .padding(top = 100.dp), // Controla que tanto puede subir la card
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = SecondaryBg
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 15.dp, top = 8.dp)
                        .fillMaxWidth()
                        .clickable { onGoLogin() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver a login",
                        tint = Resaltado,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Regresar a Login",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Resaltado,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                                .padding(start = 15.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Resaltado,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp)
                )

                Spacer(Modifier.height(32.dp))

                // Form
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it},
                    label = { Text("Email")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it},
                    label = { Text("Password")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(Modifier.height(20.dp)) // Más espacio para coherencia visual con el espacio hacia el botón

                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?:"",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Resaltado
                    ),
                    onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onRegistered()
                    } else {
                        errorMessage = "Por favor ingrese todos los campos."
                    }
                }) {
                    Text("Registrarse")
                }
            }
        }
    }
}