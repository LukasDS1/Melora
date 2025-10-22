package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.users.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val message: String? = null,
    val error: String? = null
)

class ForgotPasswordViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onRecoverPassword() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "El email no puede estar vacío",
                message = null
            )
            return
        }

        viewModelScope.launch {
            val user = userDao.getByEmail(email)
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    message = "Correo encontrado exitosamente. Revise su email.",
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "No existe una cuenta con ese correo electrónico.",
                    message = null
                )
            }
        }
    }
}