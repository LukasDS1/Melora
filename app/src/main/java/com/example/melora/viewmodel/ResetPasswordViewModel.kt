package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.RecoverPassRemoteModule
import com.example.melora.data.repository.RecoverPassApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val token: String = "",
    val newPassword: String = "",
    val passwordError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,

    val success: Boolean = false,
    val errorMessage: String? = null
)

class ResetPasswordViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val repository = RecoverPassApiRepository()

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState

    fun onTokenChange(value: String) {
        _uiState.update { it.copy(token = value) }
        recomputeCanSubmit()
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                newPassword = value,
                passwordError = if (value.length < 6) "Mínimo 6 caracteres" else null
            )
        }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val s = _uiState.value
        val can = s.token.isNotBlank() &&
                s.passwordError == null &&
                s.newPassword.isNotBlank()

        _uiState.update { it.copy(canSubmit = can) }
    }

    fun submit() {
        val s = _uiState.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            try {
                val res = repository.resetPassword(
                    token = s.token.trim(),
                    newPassword = s.newPassword.trim(),
                )

                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!

                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            success = body.status,
                            errorMessage = if (body.status) null else body.message
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMessage = "Error del servidor"
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = "Error de red: ${e.message}"
                    )
                }
            }

        }
    }

    fun clearResult() {
        _uiState.update { ResetPasswordUiState() }
    }
}
