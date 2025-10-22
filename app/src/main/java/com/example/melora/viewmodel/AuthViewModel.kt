package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.UserRepository
import com.example.melora.domain.validation.validateConfirmPassword
import com.example.melora.domain.validation.validateEmail
import com.example.melora.domain.validation.validateNickname
import com.example.melora.domain.validation.validatePassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState (
    val email: String = "",
    val emailError: String? = null,
    val pass: String = "",
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null // error general
)

data class RegisterUiState (
    val nickname: String = "",
    val email: String = "",
    val pass: String = "",
    val confirmPass: String = "",

    val nicknameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmPassError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(

    private val repository: UserRepository
) : ViewModel() {


    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // ---------------- LOGIN: handlers y envío ----------------

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value, passError = validatePassword(value)) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null &&
                s.email.isNotBlank() &&
                s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMessage = null, success = true) }
            delay(500)

            val result = repository.login(s.email.trim(), s.pass)

            _login.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMessage = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMessage = null) }
    }

    // ---------------- REGISTRO: handlers y envío ----------------

    fun onNicknameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(nickname = filtered, nicknameError = validateNickname(value))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validatePassword(value)) }

        _register.update { it.copy(confirmPassError = validateConfirmPassword(it.pass, it.confirmPass)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirmPass = value, confirmPassError = validateConfirmPassword(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nicknameError, s.emailError, s.passError, s.confirmPassError).all { it == null }
        val filled = s.nickname.isNotBlank() && s.email.isNotBlank() && s.pass.isNotBlank() && s.confirmPass.isNotBlank() // Todo lleno
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMessage = null, success = false) }
            delay(700)

            val result = repository.register(
                nickname = s.nickname.trim(),
                email = s.email.trim(),
                password = s.pass
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMessage = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "No se pudo registrar."
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMessage = null) }
    }
}