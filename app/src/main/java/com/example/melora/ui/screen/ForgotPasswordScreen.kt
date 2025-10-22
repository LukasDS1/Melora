package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.melora.data.local.MeloraDB
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.ui.viewmodel.ForgotPasswordViewModel
import com.example.melora.navigation.Route
import com.example.melora.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreenVm(
    vm: ForgotPasswordViewModel,

) {}

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = MeloraDB.getDatabase(context)
    val userDao = db.userDao()

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(SecondaryBg, shape = MaterialTheme.shapes.medium)
                .padding(24.dp)
                .fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Recuperar contraseña",
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryBg
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Resaltado,
                    cursorColor = Resaltado
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.onRecoverPassword() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg)
            ) {
                Text("Enviar", color = SecondaryBg)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate(Route.Login.path) }) {
                Text("Volver al inicio de sesión", color = Resaltado)
            }
        }
    }
}
