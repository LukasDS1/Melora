package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.RecoverPassApiRepository
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
        _uiState.update { it.copy(token = value, tokenError = validateToken(value)) }
        recomputeCanSubmit()
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

    private fun validateToken(t: String): String? {
        if (t.isBlank()) return "Introduce el token"
        return null
    }

    private fun validatePassword(pw: String): String? {
        if (pw.isBlank()) return "La contraseña no puede estar vacía"
        if (pw.length < 6) return "Mínimo 6 caracteres"
        return null
    }

    private fun recomputeCanSubmit() {
        val s = _uiState.value
        val noErrors = s.tokenError == null && s.passwordError == null
        val filled = s.token.isNotBlank() && s.password.isNotBlank()

        _uiState.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submit() {
        val s = _uiState.value
        if (!s.canSubmit || s.isSubmitting) return

        // VALIDACIONES
        val tokenErr = validateToken(s.token)
        val pwErr = validatePassword(s.password)

        if (tokenErr != null || pwErr != null) {
            _uiState.update {
                it.copy(
                    tokenError = tokenErr,
                    passwordError = pwErr,
                    canSubmit = false
                )
            }
            return
        }

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
