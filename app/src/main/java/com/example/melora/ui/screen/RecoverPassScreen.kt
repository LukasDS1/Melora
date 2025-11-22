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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import com.example.melora.viewmodel.RecoverPassViewModel
import com.example.melora.R

@Composable
fun RecoverPassScreenVm(
    vm: RecoverPassViewModel,
    onBackToLogin: () -> Unit,
    onGoToResetPassword: (String) -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearResult()
            onGoToResetPassword("")
        }
    }


    RecoverPassScreen(
        email = state.email,
        emailError = state.emailError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMessage = state.errorMessage,
        onEmailChange = vm::onEmailChange,
        onSubmit = vm::submit,
        onBackToLogin = onBackToLogin
    )
}

@Composable
fun RecoverPassScreen(
    email: String,
    emailError: String?,

    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val bg = PrimaryBg

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {

        //
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.melora_icon),
                contentDescription = "Melora icon",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Forgot your password?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = Lato,
                    color = Color.White,
                )
                Text(
                    text = "Let’s get you a new one.",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = Lato,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .heightIn(min = 200.dp, max = 700.dp)
                .padding(top = 100.dp),
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

                Text(
                    text = "Recover password",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Resaltado,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, start = 15.dp),
                    fontFamily = PlayfairDisplay
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("Email", fontFamily = Lato)},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email icon",
                            tint = Color.Gray
                        )
                    },
                    singleLine = true,
                    isError = emailError != null,
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
                    Text(
                        emailError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(20.dp))

                if (errorMessage != null) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(10.dp))
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Resaltado
                    ),
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                ) {
                    Text(
                        text = if (isSubmitting) "Sending..." else "Send recovery email",
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Lato)
                    )
                }

                Spacer(Modifier.height(30.dp))

                TextButton(onClick = onBackToLogin) {
                    Text("Back to sign in", color = Resaltado, fontFamily = Lato)
                }
            }
        }
    }
}