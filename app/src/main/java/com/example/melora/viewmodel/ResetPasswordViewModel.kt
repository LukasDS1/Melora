package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.RecoverPassApiRepository
import com.example.melora.domain.validation.validateConfirmPassword
import com.example.melora.domain.validation.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val token: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val tokenError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

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
        _uiState.update { it.copy(token = value, tokenError = validateTokenNotNull(value)) }
        recomputeCanSubmit()

        if (value.length >= 4) validateTokenOnline(value)
    }

    private fun validateTokenOnline(token: String) {
        viewModelScope.launch {
            try {
                val res = repository.validateToken(token)

                if (res.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            tokenError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            tokenError = "Invalid token or expired"
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(tokenError = "Error validating token")
                }
            }

            recomputeCanSubmit()
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = validatePassword(value)
            )
        }
        recomputeCanSubmit()
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value,
                confirmPasswordError = validateConfirmPassword(it.password, value)
            )
        }
        recomputeCanSubmit()
    }

    private fun validateTokenNotNull(t: String): String? {
        if (t.isBlank()) return "Write token"
        return null
    }

    private fun recomputeCanSubmit() {
        val s = _uiState.value
        val noErrors = s.tokenError == null &&
                s.passwordError == null &&
                s.confirmPasswordError == null

        val filled = s.token.isNotBlank() &&
                s.password.isNotBlank() &&
                s.confirmPassword.isNotBlank()

        _uiState.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submit() {
        val s = _uiState.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            try {
                val res = repository.resetPassword(
                    token = s.token.trim(),
                    newPassword = s.password.trim()
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
                    val msg = try { res.errorBody()?.string() } catch (_: Exception) { null }
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMessage = msg ?: "Error del servidor"
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = "Error de red: ${e.message ?: "desconocido"}"
                    )
                }
            }
        }
    }

    fun clearResult() {
        _uiState.update { ResetPasswordUiState() }
    }
}
