package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.users.UserEntity
import com.example.melora.data.repository.UserRepository
import com.example.melora.data.storage.UserPreferences
import com.example.melora.domain.validation.validateConfirmPassword
import com.example.melora.domain.validation.validateEmail
import com.example.melora.domain.validation.validateNickname
import com.example.melora.domain.validation.validatePassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
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

    private val repository: UserRepository,
    private val app: Application
) : AndroidViewModel(app ) {

    private val prefs = UserPreferences(app)

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _currentRoleId = MutableStateFlow<Long?>(null)
    val currentRoleId: StateFlow<Long?> = _currentRoleId

    val isLoggedIn: Flow<Boolean> = prefs.isLoggedIn

    init {
        viewModelScope.launch {
            prefs.isLoggedIn.collect { logged ->
                if (logged) {
                    val id = prefs.userId.firstOrNull()
                    val roleId = prefs.userRoleId.firstOrNull()
                    _currentRoleId.value = roleId
                    if (id != null) {
                        val user = repository.getUserById(id)
                        _currentUser.value = user
                        if (user != null && roleId != null && user.rolId != roleId) {
                            prefs.saveLoginState(true, id, user.rolId)
                        }
                    }
                } else {
                    _currentUser.value = null
                    _currentRoleId.value = null
                }
            }
        }
    }

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
            // 1. Estamos procesando, PERO AÚN NO ES ÉXITO
            _login.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                    success = false // <-- clave: NO digas éxito todavía
                )
            }

            val result = repository.login(s.email.trim(), s.pass)

            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                _currentRoleId.value = user.rolId
                prefs.saveLoginState(true, user.idUser,user.rolId)

                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMessage = null
                    )
                }
            } else {
                _login.update {

                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Error de autenticación"
                    )
                }
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            prefs.clear()
            _currentUser.value = null
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
            _register.update { it.copy(isSubmitting = true, errorMessage = null) }

            val result = repository.register(s.nickname, s.email, s.pass)
            if (result.isSuccess) {
                _register.update { it.copy(isSubmitting = false, success = true) }
            } else {
                _register.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Cant register."
                    )
                }
            }
        }
    }


    fun clearRegisterResult() {
        _register.update {
            RegisterUiState()
        }
    }
}