package com.example.melora.ui.screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ResetPasswordScreenVm(
    vm: ResetPasswordViewModel,
    onBackToLogin: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearResult()
            onBackToLogin()
        }
    }

    ResetPasswordScreen(
        token = state.token,
        password = state.password,
        confirmPassword = state.confirmPassword,
        tokenError = state.tokenError,
        passwordError = state.passwordError,
        confirmPasswordError = state.confirmPasswordError,
        isSubmitting = state.isSubmitting,
        errorMessage = state.errorMessage,
        canSubmit = state.canSubmit,
        onTokenChange = vm::onTokenChange,
        onPasswordChange = vm::onPasswordChange,
        onConfirmPasswordChange = vm::onConfirmPasswordChange,
        onSubmit = {
            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_LONG).show()
            vm.submit()
            onBackToLogin()
                   },
        onBackToLogin = onBackToLogin
    )
}


@Composable
fun ResetPasswordScreen(
    token: String,
    password: String,
    confirmPassword: String,
    tokenError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    isSubmitting: Boolean,
    errorMessage: String?,
    canSubmit: Boolean,
    onTokenChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
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

                // TOKEN
                OutlinedTextField(
                    value = token,
                    onValueChange = onTokenChange,
                    placeholder = { Text("Token received by email", fontFamily = Lato) },
                    singleLine = true,
                    isError = tokenError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Resaltado,
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                if (tokenError != null) {
                    Text(
                        text = tokenError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // PASSWORD
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

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholder = { Text("Confirm password", fontFamily = Lato) },
                    singleLine = true,
                    isError = confirmPasswordError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(30.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Resaltado,
                        unfocusedIndicatorColor = SecondaryBg,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                if (confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Error general
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(Modifier.height(10.dp))
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