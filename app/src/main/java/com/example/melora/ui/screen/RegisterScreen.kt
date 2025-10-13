package com.example.melora.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.semantics.semantics
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
    // Variables
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val passwordState = rememberTextFieldState()
    val confirmPasswordState = rememberTextFieldState()
    var focusedField by remember { mutableStateOf<String?>(null)}

    // Autofill and color of background
    val autofillManager = LocalAutofillManager.current
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
                .fillMaxWidth()                         // Usa to.do el ancho de la pantalla
                .align(Alignment.BottomCenter)         // Alinea el card abajo y centrado
                .heightIn(min = 200.dp, max = 700.dp) // Limita el alto
                .padding(top = 100.dp),              // Controla que tanto puede subir la card
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

                // Nombre de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it }, // use inputTransformation and output Transformation instead
                    placeholder = { Text("Username")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .semantics { ContentType.EmailAddress }
                        .onFocusChanged { focus ->
                            focusedField = if (focus.isFocused) "username" else null
                        }
                )

                AnimatedVisibility(visible = focusedField == "username") {
                    ConstraintList(
                        title = "Username must contain;",
                        constraints = listOf(
                            "At least 6 characters" to (username.length >= 6),
                            "Only letters or digits" to (username.all { it.isLetterOrDigit() }),
                            "Start with a letter" to (username.firstOrNull()?.isLetter() == true)
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Correo electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it}, // use inputTransformation and output Transformation instead
                    placeholder = { Text("Email")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.semantics { ContentType.EmailAddress }
                )

                Spacer(Modifier.height(20.dp))

                // Contraseña
                OutlinedSecureTextField(
                    state = passwordState,
                    placeholder = { Text("Password")},
                    supportingText = {
                        Text("Password must be at least 12 characters")
                    },
                    isError = passwordState.text.length in 1..7,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.semantics { ContentType.NewPassword }
                )

                Spacer(Modifier.height(20.dp))

                // Confirmar contraseña
                OutlinedSecureTextField(
                    state = confirmPasswordState,
                    placeholder = { Text("Confirm Password")},
                    supportingText = { Text("Passwords must match.") },
                    isError = confirmPasswordState.text.isNotEmpty() &&
                            confirmPasswordState.text != passwordState.text,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.semantics { ContentType.NewPassword }
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Resaltado
                    ),
                    onClick = {
                    if (email.isNotBlank() && passwordState.text.isNotBlank()) {
                        autofillManager?.commit()
                        onRegistered()
                    }
                    },
                    enabled = passwordState.text.length >= 12
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}

@Composable
fun ConstraintList(
    title: String,
    constraints: List<Pair<String, Boolean>>
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        constraints.forEach { (text, valid) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = if (valid)
                        Icons.Filled.CheckCircle
                    else
                        Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = if (valid)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = text,
                    color = if (valid)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
