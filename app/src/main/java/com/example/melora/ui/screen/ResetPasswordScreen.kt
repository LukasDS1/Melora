package com.example.melora.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.viewmodel.ResetPasswordViewModel
import com.example.melora.R

@Composable
fun ResetPasswordScreenVm(
    token: String,
    vm: ResetPasswordViewModel,
    onBackToLogin: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        vm.onTokenChange(token)
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearResult()
            onBackToLogin()
        }
    }

    ResetPasswordScreen(
        password = state.password,
        passwordError = state.passwordError,
        isSubmitting = state.isSubmitting,
        errorMessage = state.errorMessage,
        canSubmit = state.passwordError == null && state.password.isNotBlank(),
        onPasswordChange = vm::onPasswordChange,
        onSubmit = vm::submit,
        onBackToLogin = onBackToLogin
    )
}


@Composable
fun ResetPasswordScreen(
    password: String,
    passwordError: String?,
    isSubmitting: Boolean,
    errorMessage: String?,
    canSubmit: Boolean,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBg),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SecondaryBg)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Reset your password",
                    color = Resaltado,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PlayfairDisplay
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("New password", fontFamily = Lato) },
                    singleLine = true,
                    isError = passwordError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Resaltado,
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                if (errorMessage != null) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Resaltado),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                ) {
                    Text(
                        text = if (isSubmitting) "Resetting..." else "Reset password",
                        fontFamily = Lato
                    )
                }

                Spacer(Modifier.height(20.dp))

                TextButton(onClick = onBackToLogin) {
                    Text("Back to sign in", color = Resaltado, fontFamily = Lato)
                }
            }
        }
    }
}
