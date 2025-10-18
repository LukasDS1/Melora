package com.example.melora.ui.screen

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.viewmodel.AuthViewModel

@Composable
fun RegisterScreenVm(
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit
) {
    val vm: AuthViewModel = viewModel()
    val state by vm.register.collectAsStateWithLifecycle() // Observa estado en tiempo real

    if (state.success) {
        vm.clearRegisterResult()
        onRegistered()
    }

    RegisterScreen(
        nickname = state.nickname,
        email = state.email,
        pass = state.pass,
        confirmPass = state.confirmPass,

        nicknameError = state.nicknameError,
        emailError = state.emailError,
        passError = state.passError,
        confirmPassError = state.confirmPassError,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMessage = state.errorMessage,

        onNicknameChange = vm::onNicknameChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmPassChange = vm::onConfirmChange,

        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}

@Composable
fun RegisterScreen(
    nickname: String,
    email: String,
    pass: String,
    confirmPass: String,

    nicknameError: String?,
    emailError: String?,
    passError: String?,
    confirmPassError: String?,

    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,

    onNicknameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmPassChange: (String) -> Unit,

    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    val bg = PrimaryBg
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

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
                fontFamily = FontFamily.SansSerif,
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
                    value = nickname,
                    onValueChange = onNicknameChange,
                    placeholder = { Text("Username")},
                    singleLine = true,
                    isError = nicknameError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                Spacer(Modifier.height(10.dp))
                if (nicknameError != null) {
                    Text(nicknameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

                Spacer(Modifier.height(20.dp))

                // Correo electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange, // use inputTransformation and output Transformation instead
                    placeholder = { Text("Email")},
                    isError = emailError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                Spacer(Modifier.height(10.dp))
                if (emailError != null) {
                    Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }

                Spacer(Modifier.height(20.dp))

                // Contraseña
                OutlinedTextField(
                    value = pass,
                    onValueChange = onPassChange,
                    placeholder = { Text("Password") },
                    isError = passError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton( onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
                if (passError != null) {
                    Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }

                Spacer(Modifier.height(20.dp))

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = onConfirmPassChange,
                    placeholder = { Text("Confirm Password")},
                    isError = confirmPassError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedIndicatorColor = Resaltado,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton( onClick = { showConfirm = !showConfirm }) {
                            Icon(
                                imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showConfirm) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
                if (confirmPassError != null) {
                    Text(confirmPassError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }

                Spacer(Modifier.height(16.dp))

                if (errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Resaltado
                    ),
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}